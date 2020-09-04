package nix.edu.dao;

import nix.edu.entity.User;

public interface UserDao {
    User findById(Long id);

    void update(User user);

    void save(User user);

}
