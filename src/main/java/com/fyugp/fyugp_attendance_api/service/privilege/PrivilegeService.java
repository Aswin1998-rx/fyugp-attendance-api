package com.fyugp.fyugp_attendance_api.service.privilege;


import com.fyugp.fyugp_attendance_api.dto.PrivilegeSearch;
import com.fyugp.fyugp_attendance_api.models.privilege.Privilege;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;


/**
 * Privilege service.
 */
public interface PrivilegeService {
    Page<Privilege> getAllPrivileges(PrivilegeSearch search, Pageable pageable);

    List<Privilege> findAllPrivilegesByIds(List<Long> privilegeIds);

}
