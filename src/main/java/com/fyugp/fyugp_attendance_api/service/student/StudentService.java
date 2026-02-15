package com.fyugp.fyugp_attendance_api.service.student;


import com.fyugp.fyugp_attendance_api.dto.StudentSearch;
import com.fyugp.fyugp_attendance_api.models.student.Student;
import com.fyugp.fyugp_attendance_api.server.model.CreateStudentRequest;
import com.fyugp.fyugp_attendance_api.server.model.UpdateStudentRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StudentService {

    Student getStudent(Long id);

    Student createStudent(CreateStudentRequest student);

    Student updateStudent(UpdateStudentRequest student, Long id);

    void deleteStudent(Long id);

    Page<Student> listAllStudents(Pageable pageable, StudentSearch studentSearch);
}
