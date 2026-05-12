package app.application.adapters.persistence.sql;

import app.application.adapters.persistence.sql.entities.UserEntity;
import app.application.adapters.persistence.sql.repositories.UserRepository;
import app.domain.models.User;
import app.domain.ports.UserPort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserPersistenceAdapter implements UserPort {

    private final UserRepository userRepository;

    public UserPersistenceAdapter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void save(User user) {
        userRepository.save(toEntity(user));
    }

    @Override
    public User findByDocument(Long document) {
        return toModel(userRepository.findByDocument(document));
    }

    @Override
    public User findByUsername(String username) {
        return toModel(userRepository.findByUsername(username));
    }

    @Override
    public User findByEmail(String email) {
        return toModel(userRepository.findByEmail(email));
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll()
                .stream().map(this::toModel).collect(Collectors.toList());
    }

    private UserEntity toEntity(User user) {
        UserEntity e = new UserEntity();
        e.setName(user.getNameUser());
        e.setDocument(user.getDocument());
        e.setEmail(user.getEmail());
        e.setCellphone(user.getCellphone());
        e.setAddress(user.getAdress());
        e.setBirthdate(user.getBirthdate());
        e.setUsername(user.getDocument().toString());
        e.setIdCustomer(user.getIdCustomer());
        e.setRole(user.getRole());
        e.setCustomerRole(user.getCustomerRole());
        e.setUserStatus(user.getUserStatus());
        return e;
    }

    private User toModel(UserEntity e) {
        if (e == null) return null;
        User user = new User();
        user.setIdUser(e.getId());
        user.setNameUser(e.getName());
        user.setDocument(e.getDocument());
        user.setEmail(e.getEmail());
        user.setPassword(e.getPassword());
        user.setCellphone(e.getCellphone());
        user.setAdress(e.getAddress());
        user.setBirthdate(e.getBirthdate());
        user.setIdCustomer(e.getIdCustomer());
        user.setRole(e.getRole());
        user.setCustomerRole(e.getCustomerRole());
        user.setUserStatus(e.getUserStatus());
        return user;
    }
}