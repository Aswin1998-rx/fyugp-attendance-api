package com.fyugp.fyugp_attendance_api.service.subject;


import com.fyugp.fyugp_attendance_api.dto.SubjectSearch;
import com.fyugp.fyugp_attendance_api.models.subject.Subject;
import com.fyugp.fyugp_attendance_api.server.model.CreateSubjectRequest;
import com.fyugp.fyugp_attendance_api.server.model.UpdateSubjectRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SubjectService {

    Subject createNewSubject(CreateSubjectRequest request);

    Subject getSubjectById(Long id);

    Subject updateSubjectById(UpdateSubjectRequest request, Long id);

    Page<Subject> listAllSubjects(Pageable pageable, SubjectSearch subjectSearch);

    void deleteSubjectById(Long id);
}
