package com.fyugp.fyugp_attendance_api.service.role;



import com.fyugp.fyugp_attendance_api.dto.RoleSearch;
import com.fyugp.fyugp_attendance_api.models.role.Role;
import com.fyugp.fyugp_attendance_api.server.model.AddApplicationsToRoleRequest;
import com.fyugp.fyugp_attendance_api.server.model.AssignPrivilegesToRoleRequest;
import com.fyugp.fyugp_attendance_api.server.model.CreateRoleRequest;
import com.fyugp.fyugp_attendance_api.server.model.UpdateRoleRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;


/**
 * Role service.
 */
public interface RoleService {
    Page<Role> getAllRoles(RoleSearch search, Pageable pageable);

    Role getRoleById(Long roleId);

    Role createNewRole(CreateRoleRequest createRoleRequest);

    Role updateRoleDetailsUsingId(Long id, UpdateRoleRequest updateRoleRequest);

    List<Role> findAllRolesByIds(List<Long> roleIds);

    void assignPrivilegesToRole(Long roleId, AssignPrivilegesToRoleRequest assignPrivilegesToRoleRequest);

    void addPrivilegesOfApplicationToRole(Long roleId, AddApplicationsToRoleRequest addApplicationsToRoleRequest);

    void softDeleteRole(Long id);

    Role getRoleDetailsByName(String roleName);
}
