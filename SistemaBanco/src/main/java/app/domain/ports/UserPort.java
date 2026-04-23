package app.domain.ports;

import app.domain.models.User;
import java.util.List;

public interface UserPort {
    void save(User user);
    User findByDocument(Long document);
    User findByUsername(String username);
    User findByEmail(String email);
    List<User> findAll();
}