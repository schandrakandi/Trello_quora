package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.ResourceNotFoundException;
import com.upgrad.quora.service.exception.UnauthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class QuestionAdminBusinessService {

    @Autowired
    private QuestionDao questionDao;

    @Autowired
    private PasswordCryptographyProvider cryptographyProvider;

    public UserEntity getUser(final String userUuid, final String authorizationToken) throws ResourceNotFoundException,
            UnauthorizedException {


        UserAuthTokenEntity userAuthTokenEntity = questionDao.getUserAuthToken(authorizationToken);
        /*
        UserEntity role = userAuthTokenEntity.getUser().getRole();
        if (role != null && role.getUuid() == 101) {
            UserEntity userEntity = userDao.getUser(userUuid);
            if (userEntity == null) {
                throw new ResourceNotFoundException("USR-001", "User not found");
            }
            return userEntity;
        }
        */
        throw new UnauthorizedException("ATH-002", "you are not authorized to fetch user details");
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity createQuestion(final QuestionEntity questionEntity) {
        return questionDao.createQuestion(questionEntity);
    }
}
