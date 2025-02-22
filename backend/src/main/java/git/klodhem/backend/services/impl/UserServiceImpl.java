package git.klodhem.backend.services.impl;

import git.klodhem.backend.model.User;
import git.klodhem.backend.repositories.UsersRepository;
import git.klodhem.backend.services.UserService;
import lombok.RequiredArgsConstructor;
import org.checkerframework.common.aliasing.qual.Unique;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void register(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("ROLE_USER");
        usersRepository.save(user);
    }

    @Override
    public Optional<User> findByUsername(@Unique String username) {
        return usersRepository.findByUsername(username);
    }

    @Override
    public Optional<User> findByEmail(@Unique String email) {
        return usersRepository.findByEmail(email);
    }


}
