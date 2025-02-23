package git.klodhem.backend.controllers;

import git.klodhem.backend.dto.AuthenticationDTO;
import git.klodhem.backend.dto.UserDTO;
import git.klodhem.backend.exception.UserLoginException;
import git.klodhem.backend.exception.UserRegistrationException;
import git.klodhem.backend.model.User;
import git.klodhem.backend.security.JWTUtil;
import git.klodhem.backend.services.UserService;
import git.klodhem.backend.util.UserValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;

    private final JWTUtil jwtUtil;

    private final ModelMapper modelMapper;

    private final UserService userService;

    private final UserValidator userValidator;

    @PostMapping("/registration")
    public Map<String,String> performRegistration(@RequestBody @Valid UserDTO userDTO,
                                                  BindingResult bindingResult){
        User user = convertToUser(userDTO);
        userValidator.validate(user, bindingResult);
//        String field = bindingResult.getFieldError().getField();
        if (bindingResult.hasErrors()) {
            throw new UserRegistrationException("Ошибка при регистрации пользователя");
        }

        userService.register(user);

        String token = jwtUtil.generateToken(user.getUsername());
        return Map.of("jwt-token", token);
    }

    @PostMapping("/login")
    public Map<String,String> performLogin(@RequestBody AuthenticationDTO authenticationDTO){
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(authenticationDTO.getUsername(),
                        authenticationDTO.getPassword());

        try {
            authenticationManager.authenticate(authenticationToken);
        }catch (BadCredentialsException e){
            throw new UserLoginException("Ошибка при авторизации пользователя");
        }

        String token = jwtUtil.generateToken(authenticationDTO.getUsername());
        return Map.of("jwt-token", token);
    }

    private User convertToUser(UserDTO userDTO){
        return modelMapper.map(userDTO, User.class);
    }
}
