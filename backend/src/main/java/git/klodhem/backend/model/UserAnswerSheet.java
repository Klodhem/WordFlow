package git.klodhem.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "user_answer_sheets")
@Getter
@Setter
public class UserAnswerSheet {
    @Id
    @Column(name = "user_answer_sheet_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long userAnswerSheetId ;

    @Column(name = "mark")
    private byte mark;

    @ManyToOne
    @JoinColumn(name = "solution_id", referencedColumnName = "solution_id")
    private Solution solution;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", referencedColumnName = "question_id")
    private Question question;
}
