package com.pragmaticbitbucket.app.ws.service;

import com.pragmaticbitbucket.app.ws.shared.dto.UserDto;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {
    UserDto createUser(UserDto user);
    UserDto updateUser(String id, UserDto user);
    void deleteUser(String userId);
    UserDto getUser(String email);
    UserDto getUserByUserId(String publicUserId);
    List<UserDto> getUsers(int page, int limit);
}
