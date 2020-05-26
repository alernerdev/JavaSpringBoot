package com.pragmaticbitbucket.app.ws.service.impl;

import com.pragmaticbitbucket.app.ws.exceptions.UserServiceException;
import com.pragmaticbitbucket.app.ws.io.repositories.UserRepository;
import com.pragmaticbitbucket.app.ws.io.entity.UserEntity;
import com.pragmaticbitbucket.app.ws.service.UserService;
import com.pragmaticbitbucket.app.ws.shared.Utils;
import com.pragmaticbitbucket.app.ws.shared.dto.AddressDto;
import com.pragmaticbitbucket.app.ws.shared.dto.UserDto;
import com.pragmaticbitbucket.app.ws.ui.model.response.ErrorMessages;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.User;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

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

        for (int i=0; i< user.getAddresses().size(); i++) {
            AddressDto address = user.getAddresses().get(i);
            address.setUserDetails(user);
            address.setAddressId(utils.generateAddressId(30));
            user.getAddresses().set(i, address);
        }

        ModelMapper modelMapper = new ModelMapper();
        UserEntity userEntity = modelMapper.map(user, UserEntity.class);

        String publicUserId = utils.generateUserId(30);
        userEntity.setUserId(publicUserId);
        userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode((user.getPassword())));

        storedUserDetails = userRepository.save(userEntity);
        UserDto returnValue = modelMapper.map(storedUserDetails, UserDto.class);

        return returnValue;
    }

    @Override
    public UserDto updateUser(String userId, UserDto user)
    {
        UserEntity storedUserDetails = userRepository.findByUserId(userId);
        if (storedUserDetails == null)
            throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());

        storedUserDetails.setFirstName(user.getFirstName());
        storedUserDetails.setLastName(user.getLastName());

        UserEntity updatedUserDetails = userRepository.save(storedUserDetails);

        UserDto returnValue = new UserDto();
        BeanUtils.copyProperties(storedUserDetails, returnValue);

        return returnValue;
    }

    // needed beca
    // use deriving from UserDetailsService
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

    @Override
    public UserDto getUserByUserId(String publicUserId) {
        UserDto returnValue = new UserDto();

        UserEntity userEntity = userRepository.findByUserId(publicUserId);
        if (userEntity == null)
            throw new UsernameNotFoundException("User with ID " + publicUserId + " not found");

        BeanUtils.copyProperties(userEntity, returnValue);

        return returnValue;
    }

    @Override
    public void deleteUser(String userId) {
        UserEntity userEntity = userRepository.findByUserId(userId);
        if (userEntity == null)
            throw new UsernameNotFoundException(userId);

        userRepository.delete(userEntity);
    }

    @Override
    public List<UserDto> getUsers(int page, int limit) {
        List<UserDto> returnValue = new ArrayList<>();

        Pageable pageableRequest = PageRequest.of(page, limit);

        Page<UserEntity>  usersPage = userRepository.findAll(pageableRequest);
        List<UserEntity> users = usersPage.getContent();

        for (UserEntity userEntity : users) {
            UserDto userDto = new UserDto();
            BeanUtils.copyProperties(userEntity, userDto);
            returnValue.add(userDto);
        }

        return returnValue;
    }
}
