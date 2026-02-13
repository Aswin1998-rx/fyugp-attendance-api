package com.fyugp.fyugp_attendance_api.service.subject;

import com.fyugp.fyugp_attendance_api.dto.SubjectSearch;
import com.fyugp.fyugp_attendance_api.dto.SubjectTypeSearch;
import com.fyugp.fyugp_attendance_api.exceptions.AppException;
import com.fyugp.fyugp_attendance_api.exceptions.HttpStatusException;
import com.fyugp.fyugp_attendance_api.models.subject.Subject;
import com.fyugp.fyugp_attendance_api.models.subject.SubjectType;
import com.fyugp.fyugp_attendance_api.repositories.SubjectRepository;
import com.fyugp.fyugp_attendance_api.server.model.CreateSubjectRequest;
import com.fyugp.fyugp_attendance_api.server.model.UpdateSubjectRequest;
import com.fyugp.fyugp_attendance_api.service.subjecttype.SubjectTypeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class DefaultSubjectService implements SubjectService {

    private final SubjectRepository repository;
    private final SubjectTypeService subjectTypeService;

    @Override
    public Subject createNewSubject(CreateSubjectRequest request) {
        log.info("creating new subject type");
        Subject subjectType = new Subject();
        subjectType.setName(request.getName());
        subjectType.setSubjectType(subjectTypeService.getSubjectTypeById(request.getSubjectTypeId()));
        try {
            return repository.save(subjectType);
        }catch (Exception e){
            throw new AppException("errors.failedToCreateSubject",e);
        }
    }

    @Override
    public Subject getSubjectById(Long id) {
         log.info("fetch subject  by id");
        Optional<Subject> subjectType;
        try {
            subjectType = repository.findById(id);
        }catch (Exception e){
            throw new AppException("errors.failedToFetchSubject",e);
        }
        return subjectType.orElseThrow(
                () -> new HttpStatusException(HttpStatus.NOT_FOUND,"errors.subjectNotFound"));
    }

    @Override
    public Subject updateSubjectById(UpdateSubjectRequest request, Long id) {
        log.info("update subject  by id:{}", id);
        var subject = getSubjectById(id);
        subject.setName(request.getName());
        subject.setSubjectType(subjectTypeService.getSubjectTypeById(request.getSubjectTypeId()));
        try {
            return repository.save(subject);
        }catch (Exception e){
            throw new AppException("errors.failedToUpdateSubject",e);
        }
    }

    @Override
    public Page<Subject> listAllSubjects(Pageable pageable, SubjectSearch subjectSearch) {
        Specification<Subject> spec = Specification.where(subjectSearch);
        try {
            return repository.findAll(spec, pageable);
        }catch (Exception e){
            throw new AppException("errors.failedToListSubject",e);
        }
    }

    @Override
    public void deleteSubjectById(Long id) {
        log.info("delete subject type by id :{} ", id);
        var subjectType = getSubjectById(id);
        subjectType.setDeleted(true);
        try {
            repository.save(subjectType);
        }catch (Exception e){
            throw new AppException("errors.failedToDeleteSubject",e);
        }
    }
}
