package git.klodhem.backend.controllers;

import git.klodhem.backend.dto.VideoDTO;
import git.klodhem.backend.services.VideoProcessingService;
import git.klodhem.backend.services.VideoService;
import git.klodhem.backend.util.Language;
import git.klodhem.backend.util.LanguageTranslate;
import git.klodhem.backend.util.TranslateProposal;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRange;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

@RestController
@RequestMapping("/video")
@RequiredArgsConstructor
@Log4j2
public class VideoController {
    private final VideoProcessingService videoProcessingService;

    private final VideoService videoService;

    private static final long CHUNK_SIZE = 2 * 1024 * 1024;

    @PostMapping("/upload")
    public boolean upload(@RequestParam("file") MultipartFile file, @RequestParam Language language,
                                  @RequestParam(required = false) LanguageTranslate languageTranslate) {
        String fileName = file.getOriginalFilename();
        return videoProcessingService.videoProcessing(file, fileName, language, languageTranslate);
    }

    @GetMapping("/getVideos")
    public List<VideoDTO> getVideos() {
        return videoService.getVideosDTO();
    }

    @GetMapping("/select")
    public ResponseEntity<String> select(@RequestParam(name = "videoId") Long videoId) {
        String path = videoService.getPath(videoId);
        return ResponseEntity.ok(path);
    }

    @GetMapping("/searchPhrase")
    public ResponseEntity<Object> searchPhrase(@RequestParam(name = "videoId") Long videoId,
                             @RequestParam(name = "phrase") String phrase) {
        Long time = videoService.searchPhrase(videoId, phrase);
        if (time == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Фраза не найдена");
        }
        return ResponseEntity.ok(time);
    }

    @GetMapping("/originalSubtitle/{name}")
    public ResponseEntity<Resource> originalSubtitle(@PathVariable String name){
        File file = videoService.getVttFile(name, "original");
        if (!file.exists()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        Resource resource = new FileSystemResource(file);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + name + "\"")
                .body(resource);
    }

    @GetMapping("/translateSubtitle/{name}")
    public ResponseEntity<Resource> translateSubtitle(@PathVariable String name){
        File file = videoService.getVttFile(name, "translate");
        if (!file.exists()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        Resource resource = new FileSystemResource(file);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + name + "\"")
                .body(resource);
    }

    @GetMapping("/dictionary/{name}")
    public List<TranslateProposal> dictionary(@PathVariable String name){
        return videoService.getDictionary(name);
    }


    @GetMapping("/watch/{name}")
    public ResponseEntity<ResourceRegion> streamVideo(@PathVariable String name, @RequestHeader HttpHeaders headers) {
        File videoFile = videoService.getVideoFile(name);
        long contentLength = videoFile.length();

        Resource videoResource = new FileSystemResource(videoFile);
        HttpRange range = headers.getRange().stream().findFirst().orElse(null);

        ResourceRegion region;
        if (range != null) {
            long start = range.getRangeStart(contentLength);
            long end = range.getRangeEnd(contentLength);

            if (end >= contentLength) {
                end = start + CHUNK_SIZE - 1;
            }

            long requestedSize = end - start + 1;
            long rangeLength = Math.max(requestedSize, CHUNK_SIZE);
            rangeLength = Math.min(rangeLength, contentLength - start); // Ограничиваем размер сегмента
            region = new ResourceRegion(videoResource, start, rangeLength);
        } else {
            region = new ResourceRegion(videoResource, 0, contentLength);
        }

        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
            .contentType(MediaTypeFactory.getMediaType(videoResource).orElse(MediaType.APPLICATION_OCTET_STREAM))
            .body(region);
    }

}
