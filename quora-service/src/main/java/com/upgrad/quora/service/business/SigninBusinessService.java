package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

@Service
public class SigninBusinessService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private PasswordCryptographyProvider cryptographyProvider;

    @Transactional(propagation = Propagation.REQUIRED)
    public UserAuthTokenEntity authenticate(final String username, final String password) throws AuthenticationFailedException {
        UserAuthTokenEntity userAuthTokenEntity = null;
        UserEntity userEntity = userDao.getUserByUserName(username);
        //check userName not exist
        if (userEntity == null) {
            throw new AuthenticationFailedException("ATH-001", "This username does not exist");
        }
        //encrypt password
        final String encryptedPassword = cryptographyProvider.encrypt(password, userEntity.getSalt());

        //send username and encrypted password to userDao
        userEntity = userDao.authenticateUser(username, encryptedPassword);

        if (userEntity != null) {
            //if userName and password match
            String uuid = userEntity.getUuid();
            //Geneate authention token
            JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(encryptedPassword);
            userAuthTokenEntity = new UserAuthTokenEntity();
            userAuthTokenEntity.setUser(userEntity);
            final ZonedDateTime now = ZonedDateTime.now();
            final ZonedDateTime expiresAt = now.plusHours(8);
            userAuthTokenEntity.setAccessToken(jwtTokenProvider.generateToken(userEntity.getUuid(), now, expiresAt));
            userAuthTokenEntity.setLoginAt(now);
            userAuthTokenEntity.setExpiresAt(expiresAt);
            userAuthTokenEntity.setUuid(userEntity.getUuid());
            userDao.createAuthToken(userAuthTokenEntity);
        } else {
            //throw exception
            throw new AuthenticationFailedException("ATH-002", "Password Failed");
        }
        return userAuthTokenEntity;
    }
}