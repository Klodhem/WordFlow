package git.klodhem.backend.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import git.klodhem.backend.util.TypeTest;
import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "question")
@Getter
@Setter
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_id")
    private long questionId;

    @Column(name = "text")
    private String text;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private TypeTest type;

    @Column(name = "count_correct_answers")
    private int countCorrectAnswers;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Answer> answers;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "video_id", referencedColumnName = "video_id")
    private Video video;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "question")
    private List<UserAnswerSheet> userAnswerSheets;
}
