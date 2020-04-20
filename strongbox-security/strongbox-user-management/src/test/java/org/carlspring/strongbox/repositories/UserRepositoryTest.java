package org.carlspring.strongbox.repositories;

import org.carlspring.strongbox.config.DataServiceConfig;
import org.carlspring.strongbox.domain.User;
import org.carlspring.strongbox.domain.UserEntity;
import org.carlspring.strongbox.gremlin.adapters.EntityTraversalUtils;
import org.carlspring.strongbox.users.domain.SystemRole;

import javax.inject.Inject;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import com.google.common.collect.Sets;

/**
 * @author ankit.tomar
 */
@SpringBootTest
@ActiveProfiles(profiles = "test")
@ContextConfiguration(classes = DataServiceConfig.class)
public class UserRepositoryTest
{

    @Inject
    private UserRepository userRepository;

    @BeforeEach
    public void before()
    {
        saveUserWithRoles("test-user1", Sets.newHashSet(SystemRole.ADMIN.name(), SystemRole.ARTIFACTS_MANAGER.name()));
        saveUserWithRoles("test-user2", Sets.newHashSet(SystemRole.ADMIN.name(), "CUSTOM_ROLE"));

    }

    @Test
    public void testFindAllUsers()
    {
        List<User> findAllUsers = userRepository.findAllUsers();
        Assertions.assertNotNull(findAllUsers);
        Assertions.assertEquals(2, findAllUsers.size());
        Assertions.assertEquals(3, findAllUsers.iterator().next().getRoles().size());
        Assertions.assertEquals(3, findAllUsers.iterator().next().getRoles().size());
    }

    @Test
    public void testFindUsersWithRoleNotExist()
    {
        List<User> usersWithRole = userRepository.findUsersWithRole(SystemRole.REPOSITORY_MANAGER.name());
        Assertions.assertNotNull(usersWithRole);
        Assertions.assertEquals(0, usersWithRole.size());
    }

    @Test
    public void testFindUsersWithRoleExist()
    {
        List<User> usersWithRole = userRepository.findUsersWithRole(SystemRole.ADMIN.name());
        Assertions.assertNotNull(usersWithRole);
        Assertions.assertEquals(2, usersWithRole.size());

        usersWithRole = userRepository.findUsersWithRole("CUSTOM_ROLE");
        Assertions.assertNotNull(usersWithRole);
        Assertions.assertEquals(1, usersWithRole.size());
    }

    private void saveUserWithRoles(String username,
                                   Set<String> roles)
    {
        UserEntity user = new UserEntity(username);
        user.setPassword("password");
        user.setSecurityTokenKey("security-token");
        user.setRoles(roles);
        user.setEnabled(true);
        user.setLastUpdated(EntityTraversalUtils.toLocalDateTime(new Date()));
        userRepository.save(user);
    }
}
