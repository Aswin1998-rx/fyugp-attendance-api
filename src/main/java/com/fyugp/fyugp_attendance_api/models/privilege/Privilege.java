package com.fyugp.fyugp_attendance_api.models.privilege;


import com.fyugp.fyugp_attendance_api.models.BaseEntity;
import com.fyugp.fyugp_attendance_api.utils.annotations.Searchable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.util.List;

/**
 * Privilege model.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "identity_privilege")
public class Privilege extends BaseEntity {
    @Column(nullable = false)
    @Searchable
    private String name;
    @Column()
    private String description;


}
