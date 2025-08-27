package git.klodhem.backend.controllers;

import git.klodhem.backend.gRPC.VideoGrpcClientService;
import git.klodhem.backend.services.GroupService;
import git.klodhem.backend.services.VideoProcessingService;
import git.klodhem.common.dto.TranslateProposalDTO;
import git.klodhem.common.dto.model.VideoDTO;
import git.klodhem.common.exception.ErrorResponse;
import git.klodhem.common.util.Language;
import git.klodhem.common.util.LanguageTranslate;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.HttpHeaders;
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
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/videos")
@RequiredArgsConstructor
@Log4j2
public class VideoController {
    private final VideoProcessingService videoProcessingService;

    private final VideoGrpcClientService videoGrpcClientService;

    private final GroupService groupService;

    @PostMapping()
    public ResponseEntity<Object> upload(@RequestParam("file") MultipartFile file, @RequestParam Language language,
                                         @RequestParam() LanguageTranslate languageTranslate,
                                         @RequestParam(required = false) boolean generateTest,
                                         @RequestParam() String videoName) {
        try {
            videoProcessingService.videoProcessing(file, videoName, language, languageTranslate, generateTest);
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(e.getMessage(), LocalDateTime.now()));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }

    @GetMapping()
    public List<VideoDTO> getVideos() {
        return videoGrpcClientService.getVideos();
    }

    @GetMapping("/{videoId}/phrase")
    public ResponseEntity<Object> searchPhrase(@PathVariable(name = "videoId") String videoId,
                                               @RequestParam(name = "phrase") String phrase) {
        Long time = videoGrpcClientService.searchPhrase(videoId, phrase);
        if (time == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Фраза не найдена");
        }
        return ResponseEntity.ok(time);
    }

    @GetMapping("/{videoId}/originalSubtitle")
    public ResponseEntity<Resource> originalSubtitle(@PathVariable String videoId) {
        File file = videoGrpcClientService.getVttFile(videoId, false, "original");
        if (!file.exists()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        Resource resource = new FileSystemResource(file);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; fileId=\"" + videoId + "\"")
                .body(resource);
    }

    @GetMapping("/group/{groupId}/{videoId}/originalSubtitle")
    public ResponseEntity<Resource> originalSubtitleGroup(@PathVariable String videoId, @PathVariable long groupId) {
        File file = groupService.getVttFileVideoGroup(videoId, groupId, "original");
        if (!file.exists()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        Resource resource = new FileSystemResource(file);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; fileId=\"" + videoId + "\"")
                .body(resource);
    }

    @GetMapping("/{videoId}/translateSubtitle")
    public ResponseEntity<Resource> translateSubtitle(@PathVariable String videoId) {
        File file = videoGrpcClientService.getVttFile(videoId, false, "translate");
        if (!file.exists()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        Resource resource = new FileSystemResource(file);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; fileId=\"" + videoId + "\"")
                .body(resource);
    }

    @GetMapping("/group/{groupId}/{videoId}/translateSubtitle")
    public ResponseEntity<Resource> translateSubtitleGroup(@PathVariable String videoId, @PathVariable long groupId) {
        File file = groupService.getVttFileVideoGroup(videoId, groupId, "translate");
        if (!file.exists()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        Resource resource = new FileSystemResource(file);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; fileId=\"" + videoId + "\"")
                .body(resource);
    }

    @GetMapping("/{videoId}/dictionary")
    public List<TranslateProposalDTO> dictionary(@PathVariable String videoId) {
        return videoGrpcClientService.getDictionary(videoId, false);
    }

    @GetMapping("/group/{groupId}/{videoId}/dictionary")
    public List<TranslateProposalDTO> dictionaryVideoGroup(@PathVariable String videoId,
                                                           @PathVariable long groupId) {
        return groupService.getDictionaryVideoGroup(videoId, groupId);
    }

    @GetMapping("/{videoId}/watch")
    public ResponseEntity<ResourceRegion> streamVideo(@PathVariable String videoId, @RequestHeader HttpHeaders headers) {
        ResourceRegion region = videoGrpcClientService.getVideoRegion(videoId, false, headers);
        MediaType mediaType = MediaTypeFactory
                .getMediaType(region.getResource())
                .orElse(MediaType.APPLICATION_OCTET_STREAM);

        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                .contentType(mediaType)
                .body(region);
    }

    @GetMapping("/group/{groupId}")
    public List<VideoDTO> videosGroup(@PathVariable("groupId") long groupId) {
        return groupService.getVideos(groupId);
    }

    @GetMapping("/group/{groupId}/{videoId}/watch")
    public ResponseEntity<ResourceRegion> streamVideoGroup(@PathVariable String videoId, @PathVariable long groupId, @RequestHeader HttpHeaders headers) {
        ResourceRegion region = groupService.getVideoRegion(videoId, groupId, headers);
        MediaType mediaType = MediaTypeFactory
                .getMediaType(region.getResource())
                .orElse(MediaType.APPLICATION_OCTET_STREAM);
        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                .contentType(mediaType)
                .body(region);
    }

    @DeleteMapping("/{videoId}")
    public void deleteVideo(@PathVariable("videoId") String videoId) {
        videoGrpcClientService.deleteVideo(videoId);
    }
}
