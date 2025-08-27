package git.klodhem.backend.model;

import git.klodhem.backend.util.StatusInvite;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "invites")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Invite {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "invites_seq")
    @SequenceGenerator(
            name = "invites_seq",
            sequenceName = "invites_seq",
            allocationSize = 50
    )
    @Column(name = "invite_id")
    private long inviteId;

    @Column(name = "status_invite")
    @Enumerated(EnumType.STRING)
    private StatusInvite statusInvite;

    @ManyToOne
    @JoinColumn(name = "group_id", referencedColumnName = "group_id")
    private Group group;

    @ManyToOne
    @JoinColumn(name = "teacher_id", referencedColumnName = "user_id")
    private Teacher teacher;

    @ManyToOne
    @JoinColumn(name = "student_id", referencedColumnName = "user_id")
    private Student student;
}
