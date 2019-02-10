package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SignupBusinessService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private PasswordCryptographyProvider passwordCryptographyProvider;

    /**
     * @param  userEntity the first {@code UserEntity} to signup a user.
     * @return UserEntity objects.
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity signup(UserEntity userEntity) throws SignUpRestrictedException {
        if (!isUserExist(userEntity) && !isUserEmailExist(userEntity)) {
            String[] encryptedText = passwordCryptographyProvider.encrypt(userEntity.getPassword());
            userEntity.setSalt(encryptedText[0]);
            userEntity.setPassword(encryptedText[1]);
            return userDao.createUser(userEntity);
        }
        return null;

    }

    /**
     * @param  userEntity the first {@code UserEntity} to check if the user already exists.
     * @return true or false
     */
    private boolean isUserExist(UserEntity userEntity) throws SignUpRestrictedException {
        UserEntity entity = userDao.getUserByUserName(userEntity.getUserName());
        if (entity != null) {
            throw new SignUpRestrictedException("SGR-001", "Try any other Username, this Username has already been taken");
        } else {
            return false;
        }
    }

    /**
     * @param  userEntity the first {@code UserEntity} to check if the user email already exists.
     * @return true or false
     */
    private boolean isUserEmailExist(UserEntity userEntity) throws SignUpRestrictedException {
        UserEntity emailEntity = userDao.getUserByEmail(userEntity.getEmail());
        if (emailEntity != null) {
            throw new SignUpRestrictedException("SGR-002", "This user has already been registered, try with any other emailId");
        } else {
            return false;
        }
    }
}