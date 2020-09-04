package nix.edu.entity;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "expense_categories")
public class ExpenseCategory extends OperationCategory {

    @ManyToMany(mappedBy = "categories")
    private final List<Expense> operations = new ArrayList<>();

    public List<Expense> getOperations() {
        return operations;
    }
}
