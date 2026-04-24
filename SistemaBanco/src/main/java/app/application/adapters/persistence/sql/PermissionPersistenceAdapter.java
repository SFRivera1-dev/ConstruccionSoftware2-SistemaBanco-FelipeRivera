package app.application.adapters.persistence.sql;

import app.application.adapters.persistence.sql.entities.UserEntity;
import app.application.adapters.persistence.sql.repositories.UserRepository;
import app.domain.models.User;
import app.domain.ports.PermissionPort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PermissionPersistenceAdapter implements PermissionPort {

    private final UserRepository userRepository;

    public PermissionPersistenceAdapter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void grantPermission(Long companyDocument, Long targetUserId) {
        userRepository.findById(targetUserId).ifPresent(e -> {
            e.setIdCustomer(companyDocument);
            userRepository.save(e);
        });
    }

    @Override
    public void revokePermission(Long companyDocument, Long targetUserId) {
        userRepository.findById(targetUserId).ifPresent(e -> {
            e.setIdCustomer(null);
            userRepository.save(e);
        });
    }

    @Override
    public List<User> findUsersByCompany(Long companyDocument) {
        return userRepository.findAll()
                .stream()
                .filter(e -> companyDocument.equals(e.getIdCustomer()))
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    private User toModel(UserEntity e) {
        if (e == null) return null;
        User user = new User();
        user.setIdUser(e.getId());
        user.setNameUser(e.getName());
        user.setDocument(e.getDocument());
        user.setEmail(e.getEmail());
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