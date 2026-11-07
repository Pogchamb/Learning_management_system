package pa.chan.learning_management_system.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "student_groups")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Group extends BaseEntity {
    @Column(name = "group_name", nullable = false)
    private String groupName;

    @Builder.Default
    @BatchSize(size = 50)
    @ManyToMany(mappedBy = "groups")
    private Set<Student> studentsInGroup = new HashSet<>();

    @Builder.Default
    @BatchSize(size = 50)
    @ManyToMany(mappedBy = "groups")
    private Set<Course> courses = new HashSet<>();
}
