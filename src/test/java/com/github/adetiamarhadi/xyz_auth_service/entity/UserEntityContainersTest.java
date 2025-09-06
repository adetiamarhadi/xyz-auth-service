package com.github.adetiamarhadi.xyz_auth_service.entity;

import com.github.adetiamarhadi.xyz_auth_service.config.TestContainersConfig;
import com.github.adetiamarhadi.xyz_auth_service.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test-containers")
@Transactional
class UserEntityContainersTest extends TestContainersConfig {

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldInsertUserEntity() {

        String uuid = UUID.randomUUID().toString();

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
