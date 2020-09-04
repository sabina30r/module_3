package nix.edu;

import nix.edu.config.DbConfig;
import nix.edu.dao.AccountDao;
import nix.edu.dao.CategoryDao;
import nix.edu.dao.impl.AccountDaoImpl;
import nix.edu.dao.impl.CategoryDaoImpl;
import nix.edu.entity.Expense;
import nix.edu.entity.Income;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Month;

public class Main {
    public static void main(String[] args) {
        String userId = "1";
        String username = "postgres";
        String password = "ruzudzhenk";

        DbConfig.configure(username, password);


        Income incomeOperation = new Income();
        Expense expenseOperation = new Expense();
        AccountDao account = null;

        account = new AccountDaoImpl(username, password);


        CategoryDao incomeCategory = new CategoryDaoImpl(username, password);
        CategoryDao expenseCategory = new CategoryDaoImpl(username, password);
        incomeOperation.setAmount(10000L);
        incomeOperation.getCategories().add(incomeCategory.findIncomeByName("Заработная плата"));
        incomeOperation.setTimestamp(Instant.now(Clock.systemUTC()));
        if (account != null) {
            account.addOperationToCurrentUser(Long.valueOf(userId), 1L, incomeOperation);
        }
        expenseOperation.setAmount(5000L);
        expenseOperation.getCategories().add(expenseCategory.findExpenseByName("Коммунальные платежи"));
        expenseOperation.setTimestamp(Instant.now(Clock.systemUTC()));

        account.addOperationToCurrentUser(Long.valueOf(userId), 1L, expenseOperation);

        LocalDate from = LocalDate.of(2020, Month.JUNE, 1);
        LocalDate to = LocalDate.of(2020, Month.JULY, 31);

        new AccountDaoImpl(username, password).getAccountStatementToCsv(1L, from, to);

    }
}
