package service;

import model.User;
import dao.UserDAO;
import java.sql.SQLException;
import java.util.List;

public class UserService {

    private UserDAO userDAO;

    public UserService() {
        this.userDAO = new UserDAO();
    }

    public List<User> getAllUsers() throws SQLException {
        return userDAO.findAll();
    }

    public User findUserById(int id) throws SQLException {
        return userDAO.findById(id);
    }

    public void saveUser(User user) throws SQLException {
        userDAO.save(user);
    }

    public void addUser(String name, int age) throws SQLException {
        User user = new User(name, age);
        userDAO.save(user);
    }

    public void updateUser(User user) throws SQLException {
        userDAO.update(user);
    }

    public void deleteUser(int id) throws SQLException {
        userDAO.delete(id);
    }
}