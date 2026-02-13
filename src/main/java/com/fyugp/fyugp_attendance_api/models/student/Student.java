package com.fyugp.fyugp_attendance_api.models.student;


import com.fyugp.fyugp_attendance_api.models.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "student")
public class Student extends BaseEntity {

    @Column()
    private String name;

    @Column()
    private String rollNumber;

    @ManyToOne()
    @JoinColumn(name = "student_batch_id", referencedColumnName = "id")
    private StudentBatch studentBatch;

}
