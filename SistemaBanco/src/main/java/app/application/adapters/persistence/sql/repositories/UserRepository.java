package app.application.adapters.persistence.sql.repositories;

import app.application.adapters.persistence.sql.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    UserEntity findByDocument(String document);
    UserEntity findByUsername(String username);
    UserEntity findByEmail(String email);
    boolean existsByDocument(String document);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}