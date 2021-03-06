package com.pragmaticbitbucket.app.ws.ui.controller;

import com.pragmaticbitbucket.app.ws.exceptions.UserServiceException;
import com.pragmaticbitbucket.app.ws.service.AddressService;
import com.pragmaticbitbucket.app.ws.service.UserService;
import com.pragmaticbitbucket.app.ws.shared.dto.AddressDto;
import com.pragmaticbitbucket.app.ws.shared.dto.UserDto;
import com.pragmaticbitbucket.app.ws.ui.model.request.UserDetailsRequestModel;
import com.pragmaticbitbucket.app.ws.ui.model.response.AddressesRest;
import com.pragmaticbitbucket.app.ws.ui.model.response.ErrorMessages;
import com.pragmaticbitbucket.app.ws.ui.model.response.OperationStatusModel;
import com.pragmaticbitbucket.app.ws.ui.model.response.UserRest;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.apache.tomcat.jni.Address;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("users")    // http://localhost:8080/users
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    AddressService addressService;

    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization",
                    value="${userController.authorizationHeader.description}",
                    paramType = "header")
    }) // this is for Swagger
    @GetMapping(path = "/{id}",
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public UserRest getUser(@PathVariable String id) {
        UserRest userRest = new UserRest();
        UserDto userDto = userService.getUserByUserId(id);
        BeanUtils.copyProperties(userDto, userRest);

        return userRest;
    }

    // the body can come as JSON or XML
    @PostMapping(
            consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE}
    )
    // class UserDetailsRequestModel will map incoming body into a Java class
    public UserRest createUser(@RequestBody UserDetailsRequestModel userDetails) throws Exception {
        /*
            - from JSON body gets serialized into the model class
            - model class gets copied to DTO
            - sent to service which writes the data into userRepository
            - finally, saved userEntity gets copied to DTO
            - and DTO gets copied out to Response user mode object
         */

        // custom exception text example
        if (userDetails.getFirstName().isEmpty())
            throw new UserServiceException(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage());

        UserRest returnedValue = new UserRest();

        // UserDto userDto = new UserDto();
        // BeanUtils.copyProperties(userDetails, userDto); // copy from source object to target object
        ModelMapper modelMapper = new ModelMapper();
        UserDto userDto = modelMapper.map(userDetails, UserDto.class);

        UserDto createdUser = userService.createUser(userDto);
        returnedValue = modelMapper.map(createdUser, UserRest.class);


        return returnedValue;
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization",
                    value="${userController.authorizationHeader.description}",
                    paramType = "header")
    }) // this is for Swagger
    // the body can come as JSON or XML
    @PutMapping(
            path = "/{id}",
            consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE}
    )
    public UserRest updateUser(@PathVariable String id, @RequestBody UserDetailsRequestModel userDetails) {
        UserRest returnedValue = new UserRest();
        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(userDetails, userDto); // copy from source object to target object

        UserDto updatedUser = userService.updateUser(id, userDto);
        BeanUtils.copyProperties(updatedUser, returnedValue);

        return returnedValue;
    }


    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization",
                    value="${userController.authorizationHeader.description}",
                    paramType = "header")
    }) // this is for Swagger
    // there is no body as part of this http call
    @DeleteMapping(
            path = "/{id}",
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE}
    )
    public OperationStatusModel deleteUser(@PathVariable String id) {
        OperationStatusModel statusModel = new OperationStatusModel();

        userService.deleteUser(id);

        statusModel.setOperationName(RequestOperationName.DELETE.name());
        statusModel.setOperationResult(RequestOperationStatus.SUCCESS.name());

        return statusModel;
    }

    // /users?page=1&limit=50
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization",
                    value="${userController.authorizationHeader.description}",
                    paramType = "header")
    }) // this is for Swagger
    @GetMapping(
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE}
    )
    public List<UserRest> getUsers(@RequestParam(value = "page", defaultValue = "0") int page,
                                   @RequestParam(value = "limit", defaultValue = "25") int limit) {
        List<UserRest> returnValue = new ArrayList<UserRest>();

        List<UserDto> users = userService.getUsers(page, limit);
        for (UserDto userDto : users) {
            UserRest userModel = new UserRest();
            BeanUtils.copyProperties(userDto, userModel);
            returnValue.add(userModel);
        }

        return returnValue;
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization",
                    value="${userController.authorizationHeader.description}",
                    paramType = "header")
    }) // this is for Swagger
    // http://localhost:8080/mobile-app-ws/users/dfgbfbf/addresses
    @GetMapping(
            path = "/{id}/addresses",
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE}
    )
    public List<AddressesRest> getUserAddresses(@PathVariable String id) {
        List<AddressesRest> returnValue = new ArrayList<>();

        List<AddressDto> addressesDto = addressService.getAddresses(id);
        if (addressesDto != null && !addressesDto.isEmpty()) {
            ModelMapper modelMapper = new ModelMapper();

            java.lang.reflect.Type listType = new TypeToken<List<AddressesRest>>() {}.getType();
            returnValue = modelMapper.map(addressesDto, listType);
        }

        return returnValue;
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization",
                    value="${userController.authorizationHeader.description}",
                    paramType = "header")
    }) // this is for Swagger
    // http://localhost:8080/mobile-app-ws/users/dfgbfbf/addresses
    @GetMapping(
            path = "/{id}/addresses/{addressId}",
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE}
    )
    public AddressesRest getUserAddress(@PathVariable String addressId) {
        AddressDto addressDto = addressService.getAddress(addressId);
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(addressDto, AddressesRest.class);
    }
}
