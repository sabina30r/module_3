package nix.edu.config;

import nix.edu.entity.*;
import nix.edu.logger.FinanceLogger;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class DbConfig {
    private static SessionFactory sessionFactory;

    private static SessionFactory getSessionFactory(String username, String password) {
        if (sessionFactory == null) {
            Configuration configuration = new Configuration().configure();
            configuration.setProperty("hibernate.connection.username", username);
            configuration.setProperty("hibernate.connection.password", password);
            configuration.addAnnotatedClass(User.class);
            configuration.addAnnotatedClass(Account.class);
            configuration.addAnnotatedClass(Operation.class);
            configuration.addAnnotatedClass(Income.class);
            configuration.addAnnotatedClass(Expense.class);
            configuration.addAnnotatedClass(IncomeCategory.class);
            configuration.addAnnotatedClass(ExpenseCategory.class);
            sessionFactory = configuration.configure("hibernate.cfg.xml").buildSessionFactory();
        }
        return sessionFactory;
    }

    public static void configure(String name, String password) {
        configForHibernate(name, password);
        configForJdbc(name, password);
    }

    public static SessionFactory configForHibernate(String name, String password) {
        return getSessionFactory(name, password);

    }

    public static Connection configForJdbc(String name, String password) {
        Connection connection = null;
        try {
            Class.forName("org.postgresql.Driver");
             connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/finances", name, password);
        } catch (SQLException | ClassNotFoundException e){
            e.printStackTrace();
        }
        return connection;
    }

}
