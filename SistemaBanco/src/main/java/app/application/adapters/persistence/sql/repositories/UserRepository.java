package app.application.adapters.persistence.sql.repositories;

import app.application.adapters.persistence.sql.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    UserEntity findByDocument(String document);
    UserEntity findByEmail(String email);
}