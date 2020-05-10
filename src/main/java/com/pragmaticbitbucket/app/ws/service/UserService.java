package com.pragmaticbitbucket.app.ws.service;

import com.pragmaticbitbucket.app.ws.shared.dto.UserDto;

public interface UserService {
    UserDto createUser(UserDto user);
}
