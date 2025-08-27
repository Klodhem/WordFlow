package git.klodhem.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_answer_sheets")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserAnswerSheet {
    @Id
    @Column(name = "user_answer_sheet_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_answer_sheets_seq")
    @SequenceGenerator(
            name = "user_answer_sheets_seq",
            sequenceName = "user_answer_sheets_seq",
            allocationSize = 50
    )
    private long userAnswerSheetId;

    @Column(name = "mark")
    private byte mark;

    @ManyToOne
    @JoinColumn(name = "solution_id", referencedColumnName = "solution_id")
    private Solution solution;

    @Column(name = "question_id", nullable = false)
    private long questionId;
}
