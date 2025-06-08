package git.klodhem.backend.controllers;

import git.klodhem.backend.dto.TranslateProposalDTO;
import git.klodhem.backend.dto.model.VideoDTO;
import git.klodhem.backend.services.VideoProcessingService;
import git.klodhem.backend.services.VideoService;
import git.klodhem.backend.util.Language;
import git.klodhem.backend.util.LanguageTranslate;
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
import org.springframework.web.bind.annotation.DeleteMapping;
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
@RequestMapping("/videos")
@RequiredArgsConstructor
@Log4j2
public class VideoController {
    private final VideoProcessingService videoProcessingService;

    private final VideoService videoService;

    private static final long CHUNK_SIZE = 2 * 1024 * 1024;

    @PostMapping()
    public boolean upload(@RequestParam("file") MultipartFile file, @RequestParam Language language,
                          @RequestParam() LanguageTranslate languageTranslate,
                          @RequestParam(required = false) boolean generateTest,
                          @RequestParam() String videoName) {
        return videoProcessingService.videoProcessing(file, videoName, language, languageTranslate, generateTest);
    }

    @GetMapping()
    public List<VideoDTO> getVideos() {
        return videoService.getVideos();
    }

    @GetMapping("/{videoId}/phrase")
    public ResponseEntity<Object> searchPhrase(@PathVariable(name = "videoId") Long videoId,
                                               @RequestParam(name = "phrase") String phrase) {
        Long time = videoService.searchPhrase(videoId, phrase);
        if (time == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Фраза не найдена");
        }
        return ResponseEntity.ok(time);
    }

    @GetMapping("/{videoId}/originalSubtitle")
    public ResponseEntity<Resource> originalSubtitle(@PathVariable long videoId) {
        File file = videoService.getVttFile(videoId, null, "original");
        if (!file.exists()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        Resource resource = new FileSystemResource(file);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; fileId=\"" + videoId + "\"")
                .body(resource);
    }

    @GetMapping("/group/{groupId}/{videoId}/originalSubtitle")
    public ResponseEntity<Resource> originalSubtitleGroup(@PathVariable long videoId, @PathVariable long groupId) {
        File file = videoService.getVttFile(videoId, groupId, "original");
        if (!file.exists()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        Resource resource = new FileSystemResource(file);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; fileId=\"" + videoId + "\"")
                .body(resource);
    }

    @GetMapping("/{videoId}/translateSubtitle")
    public ResponseEntity<Resource> translateSubtitle(@PathVariable long videoId) {
        File file = videoService.getVttFile(videoId, null, "translate");
        if (!file.exists()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        Resource resource = new FileSystemResource(file);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; fileId=\"" + videoId + "\"")
                .body(resource);
    }

    @GetMapping("/group/{groupId}/{videoId}/translateSubtitle")
    public ResponseEntity<Resource> translateSubtitleGroup(@PathVariable long videoId, @PathVariable long groupId) {
        File file = videoService.getVttFile(videoId, groupId, "translate");
        if (!file.exists()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        Resource resource = new FileSystemResource(file);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; fileId=\"" + videoId + "\"")
                .body(resource);
    }

    @GetMapping("/{videoId}/dictionary")
    public List<TranslateProposalDTO> dictionary(@PathVariable long videoId,
                                                 @RequestParam(required = false) Long groupId) {
        return videoService.getDictionary(videoId, groupId);
    }


    @GetMapping("/{videoId}/watch")
    public ResponseEntity<ResourceRegion> streamVideo(@PathVariable long videoId, @RequestHeader HttpHeaders headers) {
        File videoFile = videoService.getVideoFile(videoId, null);
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
            rangeLength = Math.min(rangeLength, contentLength - start);
            region = new ResourceRegion(videoResource, start, rangeLength);
        } else {
            region = new ResourceRegion(videoResource, 0, contentLength);
        }

        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                .contentType(MediaTypeFactory.getMediaType(videoResource).orElse(MediaType.APPLICATION_OCTET_STREAM))
                .body(region);
    }

    @GetMapping("/group/{groupId}")
    public List<VideoDTO> videosGroup(@PathVariable("groupId") long groupId) {
        return videoService.getVideosGroupDTO(groupId);
    }

    @GetMapping("/group/{groupId}/{videoId}/watch")
    public ResponseEntity<ResourceRegion> streamVideoGroup(@PathVariable long videoId, @PathVariable long groupId, @RequestHeader HttpHeaders headers) {
        File videoFile = videoService.getVideoFile(videoId, groupId);
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
            rangeLength = Math.min(rangeLength, contentLength - start);
            region = new ResourceRegion(videoResource, start, rangeLength);
        } else {
            region = new ResourceRegion(videoResource, 0, contentLength);
        }

        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                .contentType(MediaTypeFactory.getMediaType(videoResource).orElse(MediaType.APPLICATION_OCTET_STREAM))
                .body(region);
    }

    @DeleteMapping("/{videoId}")
    public void deleteVideo(@PathVariable("videoId") long videoId) {
        videoService.deleteVideo(videoId);
    }
}
