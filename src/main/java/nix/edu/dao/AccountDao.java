package nix.edu.dao;

import nix.edu.entity.Operation;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public interface AccountDao {
    void addOperationToCurrentUser(Long userId, Long accountId, Operation operation);
    Long getBalance(Long id, LocalDate from, LocalDate to) throws SQLException;
    Long getTotalIncome(Long id, LocalDate from, LocalDate to) throws SQLException;
    List<Operation> getOperationsForThePeriod(Long id, LocalDate from, LocalDate to) throws SQLException;

}
