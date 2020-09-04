package nix.edu.dao.impl;

import nix.edu.config.DbConfig;
import nix.edu.dao.AccountDao;
import nix.edu.dao.UserDao;
import nix.edu.entity.*;
import nix.edu.logger.FinanceLogger;
import org.slf4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class AccountDaoImpl implements AccountDao {

    private static Connection connection;
    private static UserDao userDao;
    private Logger logger = FinanceLogger.getLogger();

    public AccountDaoImpl(String name, String password) {
        connection = DbConfig.configForJdbc(name, password);
        userDao = new UserDaoImpl(name, password);
    }

    @Override
    public void addOperationToCurrentUser(Long userId, Long accountId, Operation operation) {
        logger.info("Adding a new operation for user's id = " + userId + " , account_id = " + accountId);
        Long operationAmount = operation.getAmount();
        User user = userDao.findById(userId);
        Account currentAccount = null;
        for (Account account : user.getAccounts()) {
            if (accountId == account.getId()) {
                currentAccount = account;
            }
        }
        if (currentAccount != null) {
            Long accountAmount = currentAccount.getAmount();
            Class<? extends Operation> operationClass = operation.getClass();

            if (operationClass.equals(Income.class)) {
                currentAccount.setAmount(accountAmount + operationAmount);
                logger.info("New income was detected");

            } else if (operationClass.equals(Expense.class)) {
                if (isAvailableForWithdrawal(accountAmount, operationAmount)) {
                    currentAccount.setAmount(accountAmount - operationAmount);
                    logger.info("Expense from account balance was detected");
                }
            }
            operation.setAccount(currentAccount);
            currentAccount.getOperations().add(operation);
            userDao.update(user);
            logger.info("New operation was added for user by id = " + userId);

        } else {
            logger.error("Current user with id = " + userId + "hasn't got an account by id = " + accountId);
        }
    }

    private Boolean isAvailableForWithdrawal(Long balance, Long amountToWithdraw) {
        return balance > amountToWithdraw || balance.equals(amountToWithdraw);
    }

    @Override
    public Long getBalance(Long id, LocalDate from, LocalDate to) {
        logger.info("Calculating of the balance");
        return getTotalIncome(id, from, to) - getTotalExpense(id, from, to);
    }

    @Override
    public Long getTotalIncome(Long id, LocalDate from, LocalDate to) {
        logger.info("Calculation of the total income");
        Long sum = 0L;
        String query = "select sum(amount) from operations " +
                "where operations.account_id = (?) and operations.timestamp between (?) and (?) and operations.amount > 0";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setLong(1, id);
            preparedStatement.setTimestamp(2, Timestamp.valueOf(from.atStartOfDay()));
            preparedStatement.setTimestamp(3, Timestamp.valueOf(to.atStartOfDay()));
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                sum = resultSet.getLong(1);
            }
        } catch (SQLException e) {
            logger.error(String.valueOf(e));
        }
        return sum;
    }

    public Long getTotalExpense(Long id, LocalDate from, LocalDate to) {
        logger.info("Calculation of the total expense");
        Long sum = 0L;
        String query = "select sum(amount) from operations " +
                "where operations.account_id = (?) and operations.timestamp between (?) and (?) and operations.amount < 0";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setLong(1, id);
            preparedStatement.setTimestamp(2, Timestamp.valueOf(from.atStartOfDay()));
            preparedStatement.setTimestamp(3, Timestamp.valueOf(to.atStartOfDay()));
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                sum = resultSet.getLong(1);
            }
        } catch (SQLException e) {
            logger.error(String.valueOf(e));
        }

        return sum;
    }

    @Override
    public List<Operation> getOperationsForThePeriod(Long id, LocalDate from, LocalDate to) {
        logger.info("Processing of operation's list detecting");
        List<Operation> result = new ArrayList<>();
        String query = "select id, amount, timestamp from operations" +
                "where operations.account_id = (?) and operations.timestamp between (?) and (?);";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setLong(1, id);
            preparedStatement.setTimestamp(2, Timestamp.valueOf(from.atStartOfDay()));
            preparedStatement.setTimestamp(3, Timestamp.valueOf(to.atStartOfDay()));
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Operation operation = new Operation();
                operation.setId(resultSet.getLong("id"));
                operation.setAmount(resultSet.getLong("amount"));
                operation.setTimestamp(resultSet.getTimestamp("timestamp").toInstant());
                result.add(operation);
            }
        } catch (SQLException e) {
            logger.error(String.valueOf(e));
        }

        return result;
    }

    public void getAccountStatementToCsv(Long id, LocalDate from, LocalDate to) {
        logger.info("Export account's statement. Id = " + id);
        File csvOutputFile = new File("accountStatement.csv");
        try {
            csvOutputFile.createNewFile();
        } catch (IOException e) {
            System.out.println("Cannot create file.");
        }
        List<Operation> operations = getOperationsForThePeriod(id, from, to);
        Long incomeOfAllOperations = getTotalIncome(id, from, to);
        Long balance = getBalance(id, from, to);
        List<String[]> dataLines = new ArrayList<>();
        for (Operation operation : operations) {
            dataLines.add(new String[]{
                    String.valueOf(operation.getId()),
                    String.valueOf(operation.getAmount()),
                    operation.getTimestamp() + "/n"});
        }
        dataLines.add(new String[]{String.valueOf(incomeOfAllOperations), String.valueOf(balance)});
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(csvOutputFile);
        } catch (FileNotFoundException e) {
            System.out.println("File doesn't exist");
        }
        dataLines.stream()
                .map(this::convertToCSV)
                .forEach(pw::println);
    }

    private String convertToCSV(String[] data) {
        return String.join(",", data);
    }
}
