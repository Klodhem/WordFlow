package git.klodhem.videoservice.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import git.klodhem.common.util.StatusVideo;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "videos")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Video {
    @Id
    @Column(name = "video_id")
    private UUID videoId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "video_path", nullable = false, unique = true)
    private String videoPath;

    @Column(name = "subtitles_original_path")
    private String subtitlesOriginalPath;

    @Column(name = "subtitles_translate_path")
    private String subtitlesTranslatePath;

    @Type(value = JsonBinaryType.class)
    @Column(columnDefinition = "jsonb")
    private JsonNode proposals;

    @Column(name = "original_text", columnDefinition = "TEXT")
    private String originalText;

    @Column(name = "translate_text", columnDefinition = "TEXT")
    private String translateText;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private StatusVideo status;

    @Column(name = "user_id")
    private Long userId;

    @OneToMany(mappedBy = "video", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Question> questions;
}