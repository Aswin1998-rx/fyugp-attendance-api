package com.fyugp.fyugp_attendance_api.models.subject;

import com.fyugp.fyugp_attendance_api.models.BaseEntity;
import com.fyugp.fyugp_attendance_api.models.student.StudentBatch;
import com.fyugp.fyugp_attendance_api.utils.annotations.Searchable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "subject")
public class Subject extends BaseEntity {

    @Column(unique = true)
    @Searchable
    private String name;

    @ManyToOne()
    @JoinColumn(name = "subject_type_id", referencedColumnName = "id")
    private SubjectType subjectType;

}
