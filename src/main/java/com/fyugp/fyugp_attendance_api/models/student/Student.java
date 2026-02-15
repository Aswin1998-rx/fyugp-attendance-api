package com.fyugp.fyugp_attendance_api.models.student;


import com.fyugp.fyugp_attendance_api.models.BaseEntity;
import com.fyugp.fyugp_attendance_api.models.department.Department;
import com.fyugp.fyugp_attendance_api.utils.annotations.Searchable;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "student")
@Builder
@AllArgsConstructor
public class Student extends BaseEntity {

    @Column()
    @Searchable
    private String name;

    @Column()
    @Searchable
    private String registrationNumber;

    @ManyToOne()
    @JoinColumn(name = "student_batch_id", referencedColumnName = "id")
    private StudentBatch studentBatch;

    @ManyToOne()
    @JoinColumn(name = "department_id", referencedColumnName = "id")
    private Department department;

}
