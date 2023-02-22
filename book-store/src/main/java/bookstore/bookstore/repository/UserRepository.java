package bookstore.bookstore.repository;

import bookstore.bookstore.model.BaseEntity;
import bookstore.bookstore.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository<T extends BaseEntity> extends JpaRepository<T, Long> {
    Optional<User> findByUsername(String username);

    //Boolean existsByUsername(String username);
}