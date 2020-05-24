package com.pragmaticbitbucket.app.ws.ui.controller;

import com.pragmaticbitbucket.app.ws.service.UserService;
import com.pragmaticbitbucket.app.ws.shared.dto.UserDto;
import com.pragmaticbitbucket.app.ws.ui.model.request.UserDetailsRequestModel;
import com.pragmaticbitbucket.app.ws.ui.model.response.ErrorMessages;
import com.pragmaticbitbucket.app.ws.ui.model.response.UserRest;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("users")    // http://localhost:8080/users
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping(path = "/{id}",
            produces= {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public UserRest getUser(@PathVariable String id) {
        UserRest userRest = new UserRest();
        UserDto userDto = userService.getUserByUserId(id);
        BeanUtils.copyProperties(userDto, userRest);

        return userRest;
    }

    // the body can come as JSON or XML
    @PostMapping(
            consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE},
            produces= {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE}
    )
    // class UserDetailsRequestModel will map incoming body into a Java class
    public UserRest createUser(@RequestBody UserDetailsRequestModel userDetails) throws Exception
    {
        /*
            - from JSON body gets serialized into the model class
            - model class gets copied to DTO
            - sent to service which writes the data into userRepository
            - finally, saved userEntity gets copied to DTO
            - and DTO gets copied out to Response user mode object
         */

        // custom exception text example
        if (userDetails.getFirstName().isEmpty())
            throw new Exception(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage());

        UserRest returnedValue = new UserRest();
        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(userDetails, userDto); // copy from source object to target object

        UserDto createdUser = userService.createUser(userDto);
        BeanUtils.copyProperties(createdUser, returnedValue);

        return returnedValue;
    }

    @PutMapping
    public String updateUser() {
        return "update user was called";
    }

    @DeleteMapping
    public String deleteUser() {
        return "delete user was called";
    }

}
