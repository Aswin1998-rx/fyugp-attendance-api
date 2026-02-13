package com.fyugp.fyugp_attendance_api.models.subject;

import com.fyugp.fyugp_attendance_api.models.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "subject_type")
public class SubjectType extends BaseEntity {

    private String name;

}
