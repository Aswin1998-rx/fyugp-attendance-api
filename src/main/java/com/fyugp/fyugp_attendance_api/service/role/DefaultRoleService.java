package com.fyugp.fyugp_attendance_api.service.role;


import com.fyugp.fyugp_attendance_api.dto.RoleSearch;
import com.fyugp.fyugp_attendance_api.exceptions.AppException;
import com.fyugp.fyugp_attendance_api.models.privilege.Privilege;
import com.fyugp.fyugp_attendance_api.models.role.Role;
import com.fyugp.fyugp_attendance_api.repositories.RoleRepository;
import com.fyugp.fyugp_attendance_api.server.model.AddApplicationsToRoleRequest;
import com.fyugp.fyugp_attendance_api.server.model.AssignPrivilegesToRoleRequest;
import com.fyugp.fyugp_attendance_api.server.model.CreateRoleRequest;
import com.fyugp.fyugp_attendance_api.server.model.UpdateRoleRequest;
import com.fyugp.fyugp_attendance_api.service.privilege.PrivilegeService;
import com.fyugp.fyugp_attendance_api.service.user.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Role service implementation.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DefaultRoleService implements RoleService {

    private final RoleRepository repository;
    private final PrivilegeService privilegeService;

    @Lazy
    private final UserService userService;

    @Override
    public Page<Role> getAllRoles(RoleSearch search, Pageable pageable) {
        log.debug("getting all roles");
        try {
            return repository.findAll(search, pageable);
        } catch (Exception e) {
            throw new AppException("errors.failedToFetchRoles", e);
        }
    }

    @Override
    public Role getRoleById(Long roleId) {
        log.debug("getting role by role id");
        try {
            return repository.findById(roleId)
                    .orElseThrow(() -> new EntityNotFoundException("errors.roleNotFound"));
        } catch (Exception e) {
            throw new AppException("errors.failedToFetchRoles", e);
        }

    }

    @Override
    public Role createNewRole(CreateRoleRequest request) {
        log.debug("creating new role {} ", request.getName());
        try {
            Role role = new Role();
            role.setName(request.getName());
            role.setDescription(request.getDescription());
            return repository.save(role);
        } catch (Exception e) {
            throw new AppException("errors.failedToSaveRole", e);
        }
    }

    @Override
    public Role updateRoleDetailsUsingId(Long id, UpdateRoleRequest request) {
        log.debug("updating role details using id {} ", id);
        Role role = this.getRoleDetailsById(id);
        role.setName(Optional.ofNullable(request.getName()).orElse(role.getName()));
        role.setDescription(Optional.ofNullable(request.getDescription()).orElse(role.getDescription()));
        try {
            return repository.save(role);
        } catch (Exception ex) {
            throw new AppException("errors.failedToUpdateRole", ex);
        }
    }

    @Override
    public void assignPrivilegesToRole(Long roleId, AssignPrivilegesToRoleRequest request) {
        log.debug("assigning privileges to role having id {} ", roleId);
        Role role = this.getRoleDetailsById(roleId);
        List<Privilege> privilegesToAdd = privilegeService.findAllPrivilegesByIds(request.getPrivilegeIds());
        try {
            role.setPrivileges(privilegesToAdd);
            repository.save(role);
        } catch (Exception e) {
            throw new AppException("errors.failedToAssignPrivilegesToRole", e);
        }
    }

    @Override
    public void softDeleteRole(Long id) {
        log.debug("soft deleting role by id {} ", id);
        Role role = this.getRoleDetailsById(id);
        if (!role.getUsers().isEmpty()) {
            throw new AppException("errors.roleIsAlreadyInUse");
        }
        role.setDeleted(Boolean.TRUE);
        try {
            repository.save(role);
        } catch (Exception e) {
            throw new AppException("errors.failedToSoftDeleteRole", e);
        }
    }

    @Override
    public void addPrivilegesOfApplicationToRole(Long roleId, AddApplicationsToRoleRequest request) {
        log.debug("adding privileges of application to role having id {} ", roleId);
        Role role = this.getRoleDetailsById(roleId);

//        Set<Privilege> privilegesToAdd = applications.stream()
//                .flatMap(application -> application.getPrivileges().stream())
//                .collect(Collectors.toSet());
        try {
//            role.getPrivileges().addAll(
//                    new ArrayList<>(privilegesToAdd).stream()
//                            .filter(privilegeToAdd -> !role.getPrivileges().contains(privilegeToAdd))
//                            .collect(Collectors.toList())
//            );
            repository.save(role);
        } catch (Exception e) {
            throw new AppException("errors.failedToAddPrivilegesOfApplicationsToRole", e);
        }
    }

    @Override
    public List<Role> findAllRolesByIds(List<Long> roleIds) {
        log.debug("fetching all roles using ids");
        try {
            return repository.findAllById(roleIds);
        } catch (Exception e) {
            throw new AppException("errors.failedToFetchRoles", e);
        }
    }

    private Role getRoleDetailsById(Long id) {
        log.debug("getting role details using id {} ", id);
        Optional<Role> role;
        try {
            role = repository.findById(id);
        } catch (Exception e) {
            throw new AppException("errors.failedToFetchRole", e);
        }
        return role.orElseThrow(() -> new EntityNotFoundException("errors.roleNotFound"));
    }

    @Override
    public Role getRoleDetailsByName(String roleName) {
        log.debug("getting role details using name {} ", roleName);
        Optional<Role> role;
        try {
            role = repository.findByName(roleName);
        } catch (Exception e) {
            throw new AppException("errors.failedToFetchRole", e);
        }
        return role.orElseThrow(() -> new EntityNotFoundException("errors.roleNotFound"));
    }

}
