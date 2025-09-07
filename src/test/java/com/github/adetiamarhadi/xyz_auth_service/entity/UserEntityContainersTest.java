package com.github.adetiamarhadi.xyz_auth_service.entity;

import com.github.adetiamarhadi.xyz_auth_service.config.JpaAuditingConfiguration;
import com.github.adetiamarhadi.xyz_auth_service.config.TestContainersConfig;
import com.github.adetiamarhadi.xyz_auth_service.repository.UserRepository;
import com.github.adetiamarhadi.xyz_auth_service.util.UUID;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Disabled
@DataJpaTest
@Import(JpaAuditingConfiguration.class)
@ActiveProfiles("test-containers")
@Transactional
class UserEntityContainersTest extends TestContainersConfig {

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldInsertUserEntity() {

        String uuid = UUID.generate();

        UserEntity user = UserEntity.builder()
                .uuid(uuid)
                .email("test@example.com")
                .password("secret")
                .status("ACTIVE")
                .build();

        UserEntity saved = userRepository.save(user);

        assertNotNull(saved.getId());
        assertNotNull(saved.getCreatedAt());
        assertNotNull(saved.getUpdatedAt());
        assertEquals(uuid, saved.getUuid());
    }
}
