package git.klodhem.backend.services;

import org.springframework.web.multipart.MultipartFile;

public interface FileUploadService {
    long uploadFile(MultipartFile file, String title, String fileName);
}
