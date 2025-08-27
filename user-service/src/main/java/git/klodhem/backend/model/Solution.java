package git.klodhem.backend.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "solutions")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Solution {
    @Id
    @Column(name = "solution_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "solutions_seq")
    @SequenceGenerator(
            name = "solutions_seq",
            sequenceName = "solutions_seq",
            allocationSize = 50
    )
    private long solutionId;

    @Column(name = "mark")
    private byte mark;

    @Column(name = "datetime")
    private LocalDateTime dateTime;

    @Column(name = "video_id", nullable = false)
    private String videoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user;

    @OneToMany(mappedBy = "solution", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserAnswerSheet> userAnswerSheetList;
}
