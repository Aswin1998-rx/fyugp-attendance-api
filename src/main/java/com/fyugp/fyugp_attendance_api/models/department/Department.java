package com.fyugp.fyugp_attendance_api.models.department;


import com.fyugp.fyugp_attendance_api.models.BaseEntity;
import com.fyugp.fyugp_attendance_api.models.user.User;
import com.fyugp.fyugp_attendance_api.utils.annotations.Searchable;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "department")
@Builder
public class Department extends BaseEntity {

    @Searchable
    private String name;

    @Searchable
    private String description;

    @ManyToOne
    @JoinColumn(name = "head_of_department_id")
    private User headOfDepartment;
}
