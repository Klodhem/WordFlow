package git.klodhem.backend.repositories;

import git.klodhem.backend.model.User;
import jakarta.validation.constraints.Email;
import org.checkerframework.common.aliasing.qual.Unique;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Optional<User> findByUserId(Long userId);

    Optional<User> findByEmail(@Email @Unique String email);
}
