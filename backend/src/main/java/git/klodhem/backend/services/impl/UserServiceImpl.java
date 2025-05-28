package git.klodhem.backend.services.impl;

import git.klodhem.backend.dto.UserInfoDTO;
import git.klodhem.backend.exception.UserNotFoundException;
import git.klodhem.backend.model.Student;
import git.klodhem.backend.model.Teacher;
import git.klodhem.backend.model.User;
import git.klodhem.backend.repositories.UsersRepository;
import git.klodhem.backend.services.UserService;
import git.klodhem.backend.util.RoleUser;
import lombok.RequiredArgsConstructor;
import org.checkerframework.common.aliasing.qual.Unique;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static git.klodhem.backend.util.SecurityUtil.getCurrentUser;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UsersRepository usersRepository;

    private final PasswordEncoder passwordEncoder;

    private final ModelMapper modelMapper;

    @Override
    public void registerStudent(Student student) {
        student.setPassword(passwordEncoder.encode(student.getPassword()));
        usersRepository.save(student);
    }

    @Override
    public void registerTeacher(Teacher teacher) {
        teacher.setPassword(passwordEncoder.encode(teacher.getPassword()));
        usersRepository.save(teacher);
    }

    @Override
    public Optional<User> findByUsername(@Unique String username) {
        return usersRepository.findByUsername(username);
    }

    @Override
    public Optional<User> findByEmail(@Unique String email) {
        return usersRepository.findByEmail(email);
    }

    @Override
    public UserInfoDTO getUserInfo() {
        Optional<User> userOptional = usersRepository.findById(getCurrentUser().getUserId());
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            return convertToUserInfoDTO(user);
        }
        else throw new UserNotFoundException("Пользователь не найден в БДы");
    }

    private UserInfoDTO convertToUserInfoDTO(User user) {
        return modelMapper.map(user, UserInfoDTO.class);
    }

    private User convertToUser(UserInfoDTO userInfoDTO) {
        return modelMapper.map(userInfoDTO, User.class);
    }
}
