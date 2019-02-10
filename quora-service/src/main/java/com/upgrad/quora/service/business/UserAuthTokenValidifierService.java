package com.upgrad.quora.service.business;

import com.upgrad.quora.service.common.EndPointIdentifier;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;

/**
 * Method to provide service for validating a user authentication token through a access token
 */
@Service
public class UserAuthTokenValidifierService implements EndPointIdentifier {


    @Autowired
    UserDao userDao;

    boolean userSignOutStatus(String authorizationToken) {
        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserAuthToken(authorizationToken);
        ZonedDateTime loggedOutStatus = userAuthTokenEntity.getLogoutAt();
        ZonedDateTime loggedInStatus = userAuthTokenEntity.getLoginAt();
        if (loggedOutStatus != null && loggedOutStatus.isAfter(loggedInStatus)) {
            return true;
        } else return false;
    }

}