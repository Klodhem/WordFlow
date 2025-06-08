package git.klodhem.backend.util;


import git.klodhem.backend.exception.UserRegistrationException;
import git.klodhem.backend.model.User;
import git.klodhem.backend.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserValidator {
    private final UserService userService;

    public void validate(Object target) {
        User user = (User) target;

        if (userService.findByUsername(user.getUsername()).isPresent()) {
            throw new UserRegistrationException("Пользователь с таким именем уже существует");
        }

        if (userService.findByEmail(user.getUsername()).isPresent()) {
            throw new UserRegistrationException("Пользователь с такой почтой уже существует");
        }

    }
}
