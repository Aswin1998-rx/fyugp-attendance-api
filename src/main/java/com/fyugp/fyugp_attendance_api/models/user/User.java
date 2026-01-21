package com.fyugp.fyugp_attendance_api.models.user;


import com.fyugp.fyugp_attendance_api.models.BaseEntity;
import com.fyugp.fyugp_attendance_api.models.role.Role;
import com.fyugp.fyugp_attendance_api.utils.annotations.Searchable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.usertype.UserType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;


/**
 * User model.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "user")
public class User extends BaseEntity implements UserDetails {
    @Column(nullable = false)
    @Searchable
    private String name;


    @Searchable
    @Column(nullable = false, unique = true)
    private String userName;

    @Column(nullable = false, unique = true)
    @Searchable
    private String email;

    @Column(nullable = false)
    @Searchable
    private String designation;

    private String password;

    @Column(nullable = false, unique = true)
    @Searchable
    private String epn;

    @Column()
    private Long failedLoginAttempt = 0L;

    @Column
    private Boolean accountLocked = false;



    @Column()
    private Boolean enabled;

    @Column()
    private Boolean deleted = Boolean.FALSE;

    @Searchable
    @Column()
    private String mobileNumber;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "identity_user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private List<Role> roles;



    @Transient
    private Set<? extends GrantedAuthority> authorities;

    @Transient
    private UserType userType;

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return userName;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !accountLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public User(String username, String password) {
        super();
        this.userName = username;
        this.password = password;

    }

}
