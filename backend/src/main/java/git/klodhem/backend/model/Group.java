package git.klodhem.backend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "groups")
@Getter
@Setter
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_id")
    private long groupId;

    @Column(name = "group_name", nullable = false)
    private String groupName;

    @ManyToOne
    @JoinColumn(name = "owner_id", referencedColumnName = "user_id")
    @JsonBackReference
    private User owner;

    @ManyToMany
    @JoinTable(
            name = "groups_users",
            joinColumns = @JoinColumn(name="group_id"),
            inverseJoinColumns = @JoinColumn(name="user_id")
    )
    private List<User> students;

    @ManyToMany
    @JoinTable(
            name = "groups_videos",
            joinColumns = @JoinColumn(name="group_id"),
            inverseJoinColumns = @JoinColumn(name="video_id")
    )
    private List<Video> videos;
}
