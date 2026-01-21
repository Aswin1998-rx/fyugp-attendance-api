package com.fyugp.fyugp_attendance_api.bootstrap;


import com.fyugp.fyugp_attendance_api.config.PrivilegeSeedProperties;
import com.fyugp.fyugp_attendance_api.models.privilege.Privilege;
import com.fyugp.fyugp_attendance_api.models.role.Role;
import com.fyugp.fyugp_attendance_api.models.user.User;
import com.fyugp.fyugp_attendance_api.repositories.PrivilegeRepository;
import com.fyugp.fyugp_attendance_api.repositories.RoleRepository;
import com.fyugp.fyugp_attendance_api.repositories.UserRepository;
import com.fyugp.fyugp_attendance_api.utils.EmailUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class seeds the privilege data into database.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public final class PrivilegeBootstrap implements CommandLineRunner {

    private final PrivilegeRepository privilegeRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PrivilegeSeedProperties properties;
    private final PasswordEncoder passwordEncoder;

    private HashMap<String, Privilege> cache = new HashMap<>();


    @Override
    public void run(String... args) throws Exception {
        if (!properties.isEnabled()) {
            return;
        }
        log.info("seeding privilege data into database");
        seedPrivileges();
        seedRoles();
        seedDefaultUser();
        cleanUp();
    }

    private void seedPrivileges() {
        properties.getData().privileges()
                .keySet()
                .forEach(privilegeName -> {
                    var privilege = privilegeRepository.findByName(privilegeName)
                            .orElse(new Privilege());
                    privilege.setName(privilegeName);
                    privilege.setDescription(properties.getData().privileges().get(privilegeName));
                    privilege = privilegeRepository.save(privilege);
                    cache.put(privilegeName, privilege);
                });
        syncPrivileges();
    }

    private void syncPrivileges() {
        List<Privilege> existingPrivileges = privilegeRepository.findAll();

        for (Privilege existingPrivilege : existingPrivileges) {
            String privilegeName = existingPrivilege.getName();
            if (!cache.containsKey(privilegeName)) {
                privilegeRepository.deletePrivilege(existingPrivilege);
            }
        }
    }



    private void seedRoles() {
        properties.getData().roles()
                .keySet()
                .forEach(roleName -> {
                    final var roleData = properties.getData().roles().get(roleName);
                    final var role = roleRepository.findByName(roleName)
                            .orElse(new Role());
                    role.setName(roleName);
                    role.setDescription(roleData.description());
                    role.setPrivileges(loadRolePrivileges(roleData));
                    roleRepository.save(role);
                });
    }

    private void seedDefaultUser() {
        final var defaultUser = properties.getData().defaultUser();
        if (userRepository.findByEmail(defaultUser.email()).isPresent()) {
            return;
        }
        final var role = roleRepository.findByName(defaultUser.role())
                .orElseThrow(() -> new IllegalStateException("default user role %s not found.".formatted(defaultUser.role())));
        final var user = new User();
        user.setEmail(defaultUser.email());
        user.setName(defaultUser.email());
        user.setUserName(EmailUtil.extractUserName(defaultUser.email()));
        user.setDesignation("ADMIN");
        user.setEpn("0");
        user.setMobileNumber("0000000000");
        user.setPassword(passwordEncoder.encode("password"));
        user.setAccountLocked(false);
        user.setEnabled(true);
        user.setRoles(List.of(role));
        userRepository.save(user);
    }

    private List<Privilege> loadRolePrivileges(PrivilegeSeedProperties.RoleData roleData) {
        return roleData.privileges().stream()
                .map(privilegeName -> {
                    final var privilege = cache.get(privilegeName);
                    if (privilege == null) {
                        throw new IllegalStateException("privilege %s not found".formatted(privilegeName));
                    }
                    return privilege;
                })
                .distinct()
                .collect(Collectors.toList());
    }

    private List<Privilege> loadApplicationPrivileges(PrivilegeSeedProperties.FeatureData featureData) {

        return featureData.privileges().stream()
                .map(privilegeName -> {
                    final var privilege = cache.get(privilegeName);
                    if (privilege == null) {
                        throw new IllegalStateException("privilege %s not found".formatted(privilegeName));
                    }
                    return privilege;
                })
                .distinct()
                .collect(Collectors.toList());
    }
    private List<Privilege> loadPrivileges(PrivilegeSeedProperties.ApplicationData applicationData) {
        // Load privileges associated with each feature within the application
        List<Privilege> featurePrivileges = applicationData.features().values().stream()
                .flatMap(featureData -> loadApplicationPrivileges(featureData).stream())
                .distinct()
                .collect(Collectors.toList());
        return featurePrivileges;
    }


    private void cleanUp() {
        cache.clear();
        cache = null;
        properties.setData(null);
    }
}
