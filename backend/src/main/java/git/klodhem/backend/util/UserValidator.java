package git.klodhem.backend.util;


import git.klodhem.backend.model.User;
import git.klodhem.backend.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class UserValidator implements Validator {
    private final UserService userService;

    @Override
    public boolean supports(Class<?> clazz) {
        return User.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        User user = (User) target;

        if(userService.findByUsername(user.getUsername()).isPresent()){
            errors.rejectValue("username", null, "Username already exists");
        }

        if(userService.findByEmail(user.getUsername()).isPresent()){
            errors.rejectValue("email", null, "Email already exists");
        }

    }
}
