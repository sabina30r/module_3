package nix.edu.dao;

import nix.edu.entity.ExpenseCategory;
import nix.edu.entity.IncomeCategory;

public interface CategoryDao {
    IncomeCategory findIncomeByName(String name);
    ExpenseCategory findExpenseByName(String name);
}
