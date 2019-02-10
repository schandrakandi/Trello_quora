package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

@Service
public class UserAdminBusinessService {

    @Autowired
    private UserDao userDao;

    public UserEntity getUser(final String userUuid, final String authorizationToken) throws
            AuthorizationFailedException, UserNotFoundException {

        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserAuthToken(authorizationToken);
        UserEntity userEntity = userDao.getUserByUuid(userUuid);

        //Check if user has signed-in
        if(userAuthTokenEntity == null)
        {
            throw new AuthorizationFailedException("ATHR-001","User has not signed in");
        }

        //Check if user has signed-out
        ZonedDateTime loggedOutStatus = userAuthTokenEntity.getLogoutAt();
        ZonedDateTime loggedInStatus = userAuthTokenEntity.getLoginAt();
        if(loggedOutStatus != null && loggedOutStatus.isAfter(loggedInStatus))
        {
            throw new AuthorizationFailedException("ATHR-002","User is signed out.Sign in first to get user details");
        }
        if(userEntity == null)
        {
            throw new UserNotFoundException("USR-001", "User with entered uuid does not exist");
        }

        return userEntity;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public String deleteUser(final String userUuid, final String authorizationToken) throws
            AuthorizationFailedException, UserNotFoundException
    {
        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserAuthToken(authorizationToken);

        //Check if user has signed-in
        if(userAuthTokenEntity == null)
        {
            throw new AuthorizationFailedException("ATHR-001","User has not signed in");
        }

        //Check if user has signed-out
        ZonedDateTime loggedOutStatus = userAuthTokenEntity.getLogoutAt();
        ZonedDateTime loggedInStatus = userAuthTokenEntity.getLoginAt();
        if(loggedOutStatus != null && loggedOutStatus.isAfter(loggedInStatus))
        {
            throw new AuthorizationFailedException("ATHR-002","User is signed out");
        }

        //Check if the user has admin privilege
        String loggedInUserId = userAuthTokenEntity.getUuid();
        UserEntity loggedUser = userDao.getUserByUuid(loggedInUserId);
        String role = loggedUser.getRole();
        if(role.equals("nonadmin"))
        {
            throw new AuthorizationFailedException("ATHR-003","Unauthorized Access, Entered user is not an admin");
        }
        UserEntity userEntityToDelete = userDao.getUserByUuid(userUuid);

        //Check if user to be deleted is present in repository
        if(userEntityToDelete == null)
        {
            throw new UserNotFoundException("USR-001", "User with entered uuid to be deleted does not exist");
        }else{
            userDao.deleteUser(userUuid);
            return userUuid;
        }
    }
}