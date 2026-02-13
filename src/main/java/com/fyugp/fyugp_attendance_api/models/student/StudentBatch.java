package com.fyugp.fyugp_attendance_api.models.student;

import com.fyugp.fyugp_attendance_api.models.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "student_batch")
public class StudentBatch extends BaseEntity {

    private LocalDate startYear;

    private LocalDate endYear;
}
