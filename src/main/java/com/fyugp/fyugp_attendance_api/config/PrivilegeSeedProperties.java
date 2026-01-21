package com.fyugp.fyugp_attendance_api.config;


import com.fyugp.fyugp_attendance_api.utils.YamlPropertySourceFactory;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.List;
import java.util.Map;

/**
 * This class loads the privilege-seed.yaml file from classpath.
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "auth-seed")
@PropertySource(value = "classpath:privilege-seed.yaml", factory = YamlPropertySourceFactory.class)
public class PrivilegeSeedProperties {
    private boolean enabled;
    private Data data;

    /**
     * This record represents seed data structure.
     *
     * @param defaultUser  the default User.
     * @param privileges   the privileges to be seeded.
     * @param roles        the roles to be seeded.
     * @param applications the application to be seeded.
     */
    public record Data(
            UserData defaultUser,
            Map<String, String> privileges,
            Map<String, RoleData> roles,
            Map<String, ApplicationData> applications
    ) {
    }

    /**
     * This record represents user seed data structure.
     */
    public record UserData(
            String email,
            String password,
            String role
    ) {
    }

    /**
     * The data structure for role seed.
     *
     * @param description the description of the role.
     * @param privileges  the privileges assigned to the role.
     */
    public record RoleData(
            String description,
            List<String> privileges
    ) {
    }

    /**
     * The data structure for application seed.
     *
     * @param description The description of the application
     * @param features  The privilege list assigned to the application
     */
    public record ApplicationData(
            String description,
            Map<String,FeatureData> features
    ) {
    }

    public record FeatureData(
            List<String> privileges
    ){

    }
}
