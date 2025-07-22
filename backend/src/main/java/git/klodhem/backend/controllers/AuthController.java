package git.klodhem.backend.controllers;

import git.klodhem.backend.dto.auth.AuthenticationDTO;
import git.klodhem.backend.dto.auth.UserLoginDTO;
import git.klodhem.backend.exception.UserLoginException;
import git.klodhem.backend.model.Student;
import git.klodhem.backend.model.Teacher;
import git.klodhem.backend.model.User;
import git.klodhem.backend.security.JWTUtil;
import git.klodhem.backend.security.UserDetailsImpl;
import git.klodhem.backend.services.UserService;
import git.klodhem.backend.util.RoleUser;
import git.klodhem.backend.util.UserValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
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
    public Map<String, String> performRegistration(@RequestBody @Validated UserLoginDTO userLoginDTO) {
        Student student = convertToStudent(userLoginDTO);
        userValidator.validate(student);
        student.setRole(RoleUser.ROLE_STUDENT);
        userService.registerStudent(student);

        String token = jwtUtil.generateToken(student);
        return Map.of("jwt-token", token);
    }

    @PostMapping("/registrationTeacher")
    public Map<String, String> performRegistrationTeacher(@RequestBody @Valid UserLoginDTO userLoginDTO) {
        Teacher teacher = convertToTeacher(userLoginDTO);
        userValidator.validate(teacher);
        userService.registerTeacher(teacher);
        teacher.setRole(RoleUser.ROLE_TEACHER);
        String token = jwtUtil.generateToken(teacher);
        return Map.of("jwt-token", token);
    }

    @PostMapping("/login")
    public Map<String, String> performLogin(@RequestBody @Validated AuthenticationDTO authenticationDTO) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(authenticationDTO.getUsername(),
                        authenticationDTO.getPassword());

        Authentication auth;
        try {
            auth = authenticationManager.authenticate(authenticationToken);
        } catch (BadCredentialsException e) {
            throw new UserLoginException("Ошибка при авторизации пользователя");
        }
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        User user = userDetails.getUser();

        String token = jwtUtil.generateToken(user);
        return Map.of("jwt-token", token);
    }

    private Teacher convertToTeacher(UserLoginDTO userLoginDTO) {
        return modelMapper.map(userLoginDTO, Teacher.class);
    }

    private Student convertToStudent(UserLoginDTO userLoginDTO) {
        return modelMapper.map(userLoginDTO, Student.class);
    }
}
