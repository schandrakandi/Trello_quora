package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.UserDeleteResponse;
import com.upgrad.quora.service.business.UserAdminBusinessService;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

//RestController annotation specifies that this class represents a REST API(equivalent of @Controller + @ResponseBody)
@RestController
@RequestMapping("/")
public class AdminController {
    //Required services are autowired to enable access to methods defined in respective Business services
    @Autowired
    private UserAdminBusinessService userAdminBusinessService;

    /**
     * @param  userUuid the first {@code String} to delete the particular user.
     * @param  accessToken the second {@code String} to check if the access is available.
     * @return ResponseEntity is returned with Status OK.
     */
    @RequestMapping(method = RequestMethod.DELETE, path = "/admin/user/{userId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity < UserDeleteResponse > userDelete(@PathVariable("userId") final String userUuid, @RequestHeader("authorization") final String accessToken) throws AuthorizationFailedException, UserNotFoundException {
        // Logic to handle Bearer <accesstoken>
        // User can give only Access token or Bearer <accesstoken> as input.
        String bearerToken = null;
        try {
            bearerToken = accessToken.split("Bearer ")[1];
        } catch (ArrayIndexOutOfBoundsException e) {
            bearerToken = accessToken;
        }
        String uuid = userAdminBusinessService.deleteUser(userUuid, bearerToken);
        UserDeleteResponse userDeleteResponse = new UserDeleteResponse().id(uuid).status("USER SUCCESSFULLY DELETED");
        return new ResponseEntity < UserDeleteResponse > (userDeleteResponse, HttpStatus.OK);
    }
}