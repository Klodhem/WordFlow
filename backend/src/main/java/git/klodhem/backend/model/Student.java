package git.klodhem.backend.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@DiscriminatorValue("ROLE_STUDENT")
@Getter
@Setter
public class Student extends User{
    @ManyToMany(mappedBy = "users")
    private List<Group> groups;

    @OneToMany(mappedBy = "student")
    @JsonManagedReference
    private List<Invite> invites;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Solution> solutions;
}
