package com.upgrad.quora.service.business;


import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

/*
Author - Ishita
 */

@Service
public class SignoutBusinessService {

    @Autowired
    private UserDao userDao;


    @Transactional(propagation = Propagation.REQUIRED)
    public UserAuthTokenEntity signOutService(String accessToken) throws SignOutRestrictedException{
        UserAuthTokenEntity userAuthTokenEntity = null;
        //check user sign in or not
        userAuthTokenEntity = userDao.getUserAuthToken(accessToken);
        if(userAuthTokenEntity!=null){
            final ZonedDateTime now = ZonedDateTime.now();
            userAuthTokenEntity.setLogoutAt(now);
            userAuthTokenEntity = userDao.updateUserLogOut(userAuthTokenEntity);
        }else{
            //if user is not sign in then throws exception
            throw new SignOutRestrictedException("SGR-001","User is not Signed in");
        }
        return userAuthTokenEntity;
    }
}