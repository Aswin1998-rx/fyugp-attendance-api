package com.fyugp.fyugp_attendance_api.service.privilege;


import com.fyugp.fyugp_attendance_api.dto.PrivilegeSearch;
import com.fyugp.fyugp_attendance_api.exceptions.AppException;
import com.fyugp.fyugp_attendance_api.models.privilege.Privilege;
import com.fyugp.fyugp_attendance_api.repositories.PrivilegeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * Privilege service implementation.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DefaultPrivilegeService implements PrivilegeService {

    private final PrivilegeRepository repository;

    @Override
    public List<Privilege> findAllPrivilegesByIds(List<Long> privilegeIds) {
        log.debug("fetching all privileges using ids");
        try {
            return repository.findAllById(privilegeIds);
        } catch (Exception e) {
            throw new AppException("errors.failedToFetchPrivileges", e);
        }
    }

    @Override
    public Page<Privilege> getAllPrivileges(PrivilegeSearch search, Pageable pageable) {
        log.debug("getting all privileges");
        try {
            return repository.findAllWithApplications(search, pageable);
        } catch (Exception ex) {
            throw new AppException("errors.failedToFetchPrivileges", ex);
        }
    }
}
