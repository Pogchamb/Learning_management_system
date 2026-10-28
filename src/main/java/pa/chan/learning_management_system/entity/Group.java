package pa.chan.learning_management_system.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "groups")
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "group_name", nullable = false)
    private String groupName;

    @ManyToMany(mappedBy = "studentGroups")
    private Set<Student> studentsInGroup = new HashSet<>();

    public void addStudents(Collection<Student> studentsToAdd) {
        this.studentsInGroup.addAll(studentsToAdd);
        studentsToAdd.forEach(student -> student.getStudentGroups().add(this));
    }
}
