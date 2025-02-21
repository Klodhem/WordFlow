package git.klodhem.backend.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import git.klodhem.backend.util.StatusVideo;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "videos")
@Getter
@Setter
public class Video {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "video_id")
    private long videoId;

    @Column(name = "title")
    private String title;

    @Column(name = "video_path")
    private String videoPath;

    @Column(name = "subtitles_original_path")
    private String subtitlesOriginalPath;

    @Column(name = "subtitles_translate_path")
    private String subtitlesTranslatePath;

    @Type(value = JsonBinaryType.class)
    @Column(columnDefinition = "jsonb")
    private JsonNode proposals;

    @Column(name = "original_text")
    private String originalText;

    @Column(name = "translate_text")
    private String translateText;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private StatusVideo status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User owner;
}
