package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.AnswerDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EditAnswerBusinessService {

    @Autowired
    private AnswerDao answerDao;

    @Autowired
    private UserDao userDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity editAnswerContent(final AnswerEntity answerEntity, final String authorization) throws AuthorizationFailedException, AnswerNotFoundException {
        UserAuthTokenEntity userAuthEntity = userDao.getUserAuthToken(authorization);

        // Validate if user is signed in or not
        if (userAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }

        // Validate if user has signed out
        if (userAuthEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to edit an answer");
        }

        // Validate if requested answer exist or not
        AnswerEntity existingAnswerEntity = answerDao.getAnswerByUuid(answerEntity.getUuid());
        if (existingAnswerEntity == null) {
            throw new AnswerNotFoundException("ANS-001", "Entered answer uuid does not exist");
        }

        // Validate if current user is the owner of requested answer
        UserEntity currentUser = userAuthEntity.getUser();
        UserEntity answerOwner = answerDao.getAnswerByUuid(answerEntity.getUuid()).getUser();
        if (currentUser.getId() != answerOwner.getId()) {
            throw new AuthorizationFailedException("ATHR-003", "Only the answer owner can edit the answer");
        }

        answerEntity.setId(existingAnswerEntity.getId());
        answerEntity.setDate(existingAnswerEntity.getDate());
        answerEntity.setUser(existingAnswerEntity.getUser());
        answerEntity.setQuestion(existingAnswerEntity.getQuestion());
        return answerDao.editAnswerContent(answerEntity);
    }
}
