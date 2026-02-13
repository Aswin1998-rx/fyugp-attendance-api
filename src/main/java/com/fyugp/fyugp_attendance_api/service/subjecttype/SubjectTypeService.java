package com.fyugp.fyugp_attendance_api.service.subjecttype;

import com.fyugp.fyugp_attendance_api.dto.SubjectSearch;
import com.fyugp.fyugp_attendance_api.dto.SubjectTypeSearch;
import com.fyugp.fyugp_attendance_api.models.subject.Subject;
import com.fyugp.fyugp_attendance_api.models.subject.SubjectType;
import com.fyugp.fyugp_attendance_api.server.model.CreateSubjectRequest;
import com.fyugp.fyugp_attendance_api.server.model.CreateSubjectTypeRequest;
import com.fyugp.fyugp_attendance_api.server.model.UpdateSubjectRequest;
import com.fyugp.fyugp_attendance_api.server.model.UpdateSubjectTypeRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SubjectTypeService {


    SubjectType createNewSubjectType(CreateSubjectTypeRequest request);

    SubjectType getSubjectTypeById(Long id);

    SubjectType updateSubjectTypeById(UpdateSubjectTypeRequest request, Long id);

    Page<SubjectType> listAllSubjectTypes(Pageable pageable, SubjectTypeSearch subjectSearch);

    void deleteSubjectTypeById(Long id);
}
