package nix.edu.dao.impl;

import nix.edu.config.DbConfig;
import nix.edu.dao.CategoryDao;
import nix.edu.entity.ExpenseCategory;
import nix.edu.entity.IncomeCategory;
import nix.edu.logger.FinanceLogger;
import org.hibernate.Session;
import org.slf4j.Logger;


public class CategoryDaoImpl implements CategoryDao {
    private static Session session;
    private Logger logger = FinanceLogger.getLogger();

    public CategoryDaoImpl(String name, String password) {
        session = DbConfig.configForHibernate(name, password).openSession();
    }

    @Override
    public IncomeCategory findIncomeByName(String name) {
        logger.info("Detecting of income category by name");
        return session.byNaturalId(IncomeCategory.class).using("name", name).getReference();
    }

    @Override
    public ExpenseCategory findExpenseByName(String name) {
        logger.info("Detecting of expense category by name");
        return session.byNaturalId(ExpenseCategory.class).using("name", name).getReference();
    }
}
