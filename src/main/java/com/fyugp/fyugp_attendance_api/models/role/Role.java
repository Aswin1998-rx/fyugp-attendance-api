package com.fyugp.fyugp_attendance_api.models.role;


import com.fyugp.fyugp_attendance_api.models.BaseEntity;
import com.fyugp.fyugp_attendance_api.models.privilege.Privilege;
import com.fyugp.fyugp_attendance_api.models.user.User;
import com.fyugp.fyugp_attendance_api.utils.annotations.Searchable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * Role model.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "identity_role")
public class Role extends BaseEntity {
    @Column(nullable = false)
    @Searchable
    private String name;
    @Column()
    private String description;
    @Column()
    private Boolean deleted = Boolean.FALSE;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "identity_role_privilege",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "privilege_id")
    )
    private List<Privilege> privileges;

    @ManyToMany(mappedBy = "roles")
    private Set<User> users = new HashSet<>();
}
