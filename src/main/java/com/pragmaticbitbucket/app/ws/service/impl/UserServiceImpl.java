package com.pragmaticbitbucket.app.ws.service.impl;

import com.pragmaticbitbucket.app.ws.io.repositories.UserRepository;
import com.pragmaticbitbucket.app.ws.io.entity.UserEntity;
import com.pragmaticbitbucket.app.ws.service.UserService;
import com.pragmaticbitbucket.app.ws.shared.Utils;
import com.pragmaticbitbucket.app.ws.shared.dto.UserDto;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.User;

import java.util.ArrayList;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    Utils utils;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public UserDto createUser(UserDto user)
    {
        UserEntity storedUserDetails = userRepository.findByEmail(user.getEmail());
        if (storedUserDetails != null)
            throw new RuntimeException("Record already exists");

        UserEntity userEntity = new UserEntity();

        // for this copying to work, the field names must be the same
        BeanUtils.copyProperties(user, userEntity);
        String publicUserId = utils.generateUserId(30);
        userEntity.setUserId(publicUserId);
        userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode((user.getPassword())));

        storedUserDetails = userRepository.save(userEntity);
        UserDto returnValue = new UserDto();
        BeanUtils.copyProperties(storedUserDetails, returnValue);

        return returnValue;
    }

    // needed because deriving from UserDetailsService
    // this method helps spring framework to load user details from database
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByEmail(email);
        if (userEntity == null)
            throw new UsernameNotFoundException(email);

        return new User(
                userEntity.getEmail(),
                userEntity.getEncryptedPassword(),
                new ArrayList<>()
        );
    }

    @Override
    public UserDto getUser(String email) {
        UserEntity userEntity = userRepository.findByEmail(email);
        if (userEntity == null)
            throw new UsernameNotFoundException(email);

        UserDto returnValue = new UserDto();
        BeanUtils.copyProperties(userEntity, returnValue);
        return returnValue;
    }

}
