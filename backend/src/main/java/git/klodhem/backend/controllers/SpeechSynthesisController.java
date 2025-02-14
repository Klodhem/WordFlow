package git.klodhem.backend.controllers;

import git.klodhem.backend.services.SpeechSynthesisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

@RestController
@RequestMapping("/speech")
@RequiredArgsConstructor
@Log4j2
public class SpeechSynthesisController {
    private final SpeechSynthesisService speechSynthesisService;

    @GetMapping("/synthesize")
    public ResponseEntity<StreamingResponseBody> speechSynthesis(@RequestParam String text) {
        File audioFile  = new File(speechSynthesisService.synthesizeSpeech(text));

        StreamingResponseBody responseBody = out -> {
            try(InputStream in = new FileInputStream(audioFile)) {
                byte[] buffer = new byte[4096];
                int byteRead;
                while ((byteRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, byteRead);
                }
                out.flush();
            }
            catch (Exception e) {
                log.error(e.getMessage());
            }
        };
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("audio/wav"));
        headers.setContentLength(audioFile.length());
        return new ResponseEntity<>(responseBody, headers, HttpStatus.OK);
    }
}
