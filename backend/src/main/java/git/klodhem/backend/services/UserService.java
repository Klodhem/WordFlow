package git.klodhem.backend.services;

import git.klodhem.backend.dto.UserInfoDTO;
import git.klodhem.backend.dto.UserLoginDTO;
import git.klodhem.backend.model.Student;
import git.klodhem.backend.model.Teacher;
import git.klodhem.backend.model.User;
import jakarta.validation.constraints.NotEmpty;
import org.checkerframework.common.aliasing.qual.Unique;

import java.util.Optional;

public interface UserService {
    void registerStudent(Student student);

    void registerTeacher(Teacher teacher);

    Optional<User> findByUsername(@NotEmpty(message = "Поле не должно быть пустым") @Unique String username);

    Optional<User> findByEmail(@NotEmpty(message = "Поле не должно быть пустым") @Unique String email);

    UserInfoDTO getUserInfo();
}
