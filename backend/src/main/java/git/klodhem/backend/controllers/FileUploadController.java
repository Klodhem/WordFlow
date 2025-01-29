package git.klodhem.backend.controllers;

import git.klodhem.backend.services.FileUploadServiceImpl;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/video")
@RequiredArgsConstructor
public class FileUploadController {
    private static final Logger logger = LoggerFactory.getLogger(FileUploadController.class);

    private final FileUploadServiceImpl fileUploadServiceImpl;

    @PostMapping("/upload")
    public String upload(@RequestParam("file") MultipartFile file) {
        logger.debug("Началась загрузка файла");
        String res = fileUploadServiceImpl.uploadFile(file);
        return "success";
    }
}
