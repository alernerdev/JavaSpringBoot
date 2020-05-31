package com.pragmaticbitbucket.app.ws.service.impl;

import com.pragmaticbitbucket.app.ws.io.entity.UserEntity;
import com.pragmaticbitbucket.app.ws.io.repositories.UserRepository;
import com.pragmaticbitbucket.app.ws.shared.dto.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class UserServiceImplTest {

    @Mock
    UserRepository userRepository;

    // mockito will inject into the UserService the mock objects it
    // requires instead of the real objects that are autowired.
    // getUser() in UserServiceImpl only makes use of UserRepository
    @InjectMocks
    UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testGetUser() {
        UserEntity userEntity = new UserEntity();
        userEntity.setFirstName("alex");
        userEntity.setLastName("smith");
        userEntity.setId(1L);
        userEntity.setUserId("sdvsdvsdfs");
        userEntity.setEncryptedPassword("sdfvsdvsddfvsdfs");

        when(userRepository.findByEmail(anyString())).thenReturn(userEntity);

        // any email will do
        UserDto userDto = userService.getUser("test@test.com");
        assertNotNull(userDto);
        assertEquals("alex", userDto.getFirstName());
        assertEquals("smith", userDto.getLastName());

    }

    @Test
    void testGetUser_UsernameNotFoundException() {
        when(userRepository.findByEmail(anyString())).thenReturn(null);

        assertThrows(UsernameNotFoundException.class, () -> {
            userService.getUser("test@test.com");
        });
    }
}