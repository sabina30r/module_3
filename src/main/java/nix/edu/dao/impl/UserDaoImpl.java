package nix.edu.dao.impl;

import nix.edu.config.DbConfig;
import nix.edu.dao.UserDao;
import nix.edu.entity.User;
import nix.edu.logger.FinanceLogger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;


public class UserDaoImpl implements UserDao {
    private static Session session;
    private Logger logger = FinanceLogger.getLogger();

    UserDaoImpl(String name, String password) {
        session = DbConfig.configForHibernate(name, password).openSession();
    }

    @Override
    public User findById(Long id) {
        logger.info("Detecting of the user by id = " + id);
        return session.get(User.class, id);
    }

    @Override
    public void update(User user) {
        logger.info("Updating information for user " + user);
        Transaction beginTransaction = session.beginTransaction();
        try {
            session.update(user);
            beginTransaction.commit();
        } catch (Exception e) {
            logger.error("Information wasn't updated");
            beginTransaction.rollback();
        }
    }

    @Override
    public void save(User user) {
        logger.info("Saving user " + user);
        Transaction beginTransaction = session.beginTransaction();
        try {
            session.save(user);
            beginTransaction.commit();
        } catch (Exception e) {
            logger.error("Information wasn't saved");
            beginTransaction.rollback();
        }
    }
}
