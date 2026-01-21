package com.fyugp.fyugp_attendance_api.repositories;


import com.fyugp.fyugp_attendance_api.dto.PrivilegeSearch;
import com.fyugp.fyugp_attendance_api.models.privilege.Privilege;
import com.fyugp.fyugp_attendance_api.models.privilege.Privilege_;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Privilege repository.
 */
public interface PrivilegeRepository extends JpaRepository<Privilege, Long>,
        JpaSpecificationExecutor<Privilege>,
        CustomQueryExecutor<Privilege> {
    Optional<Privilege> findByName(String name);

    default Page<Privilege> findAllWithApplications(PrivilegeSearch search, Pageable pageable) {
        Page<Privilege> privileges = queryPage((root, cb, cq) -> {

            cq.select(root);
            cq.where(search.toPredicate(root, cq, cb));
        }, (root, cb, cq) -> {

            cq.where(search.toPredicate(root,cq,cb));
        }, pageable);
        return privileges;
    }

    @Transactional
    @Modifying
    @Query("DELETE FROM Privilege rp WHERE rp = :existingPrivilege")
    void deletePrivilege(Privilege existingPrivilege);

}
