package git.klodhem.backend.controllers;

import git.klodhem.backend.services.FileUploadServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/video")
@RequiredArgsConstructor
@Log4j2
public class FileUploadController {
    private final FileUploadServiceImpl fileUploadServiceImpl;

    @PostMapping("/upload")
    public String upload(@RequestParam("file") MultipartFile file) {
        log.debug("Началась загрузка файла");
        String res = fileUploadServiceImpl.uploadFile(file);
        return "success";
    }
}
