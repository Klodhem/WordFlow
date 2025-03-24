package git.klodhem.backend.controllers;

import git.klodhem.backend.dto.UserInfoDTO;
import git.klodhem.backend.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Log4j2
public class UserController {
    private final UserService userService;

    @GetMapping("/getUser")
    public UserInfoDTO getUser() {
        return userService.getUserInfo();
    }
}
