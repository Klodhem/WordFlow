package git.klodhem.backend.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@DiscriminatorValue("ROLE_TEACHER")
@Getter
@Setter
public class Teacher extends User {
    @OneToMany(mappedBy = "owner")
    @JsonManagedReference
    private List<Group> managedGroups;

    @OneToMany(mappedBy = "teacher")
    @JsonManagedReference
    private List<Invite> invites;
}
