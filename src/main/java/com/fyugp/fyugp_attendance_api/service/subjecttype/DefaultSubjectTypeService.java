package com.fyugp.fyugp_attendance_api.service.subjecttype;

import com.fyugp.fyugp_attendance_api.dto.SubjectTypeSearch;
import com.fyugp.fyugp_attendance_api.exceptions.AppException;
import com.fyugp.fyugp_attendance_api.exceptions.HttpStatusException;
import com.fyugp.fyugp_attendance_api.models.subject.SubjectType;
import com.fyugp.fyugp_attendance_api.repositories.SubjectTypeRepository;
import com.fyugp.fyugp_attendance_api.server.model.CreateSubjectTypeRequest;
import com.fyugp.fyugp_attendance_api.server.model.UpdateSubjectTypeRequest;
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
public class DefaultSubjectTypeService implements SubjectTypeService {

    private final SubjectTypeRepository subjectTypeRepository;

    @Override
    public SubjectType createNewSubjectType(CreateSubjectTypeRequest request) {
        log.info("creating new subject type");
        SubjectType subjectType = new SubjectType();
        subjectType.setName(request.getName());
        try {
            return subjectTypeRepository.save(subjectType);
        }catch (Exception e){
            throw new AppException("errors.failedToCreateSubject",e);
        }
    }

    @Override
    public SubjectType getSubjectTypeById(Long id) {
         log.info("fetch subject type by id");
        Optional<SubjectType> subjectType;
        try {
            subjectType = subjectTypeRepository.findById(id);
        }catch (Exception e){
            throw new AppException("errors.failedToFetchSubject",e);
        }
        return subjectType.orElseThrow(
                () -> new HttpStatusException(HttpStatus.NOT_FOUND,"errors.subjectNotFound"));
    }

    @Override
    public SubjectType updateSubjectTypeById(UpdateSubjectTypeRequest request, Long id) {
        log.info("update subject type by id:{}", id);
        var subjectType = getSubjectTypeById(id);
        subjectType.setName(request.getName());
        try {
            return subjectTypeRepository.save(subjectType);
        }catch (Exception e){
            throw new AppException("errors.failedToUpdateSubject",e);
        }
    }

    @Override
    public Page<SubjectType> listAllSubjectTypes(Pageable pageable, SubjectTypeSearch subjectSearch) {
        Specification<SubjectType> spec = Specification.where(subjectSearch);
        try {
            return subjectTypeRepository.findAll(spec, pageable);
        }catch (Exception e){
            throw new AppException("errors.failedToListSubject",e);
        }
    }

    @Override
    public void deleteSubjectTypeById(Long id) {
        log.info("delete subject type by id :{} ", id);
        var subjectType = getSubjectTypeById(id);
        subjectType.setDeleted(true);
        try {
            subjectTypeRepository.save(subjectType);
        }catch (Exception e){
            throw new AppException("errors.failedToDeleteSubject",e);
        }
    }
}
