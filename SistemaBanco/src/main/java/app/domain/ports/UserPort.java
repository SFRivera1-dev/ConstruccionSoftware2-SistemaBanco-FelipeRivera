package app.domain.ports;

import app.domain.models.User;

public interface UserPort {
    User findByDocument(String document);
    User findByEmail(String email);
    void save(User user);
}