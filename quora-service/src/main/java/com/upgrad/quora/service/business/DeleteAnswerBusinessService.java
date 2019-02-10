package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.AnswerDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
@Service
public class DeleteAnswerBusinessService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private AnswerDao answerDao;

    /**
     * @param  answerId the first {@code String} id of the answer to be deleted
     * @param  authorization the second {@code String} to check if the access is available.
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteAnswer(final String answerId, final String authorization) throws AuthorizationFailedException, AnswerNotFoundException {
        UserAuthTokenEntity userAuthEntity = userDao.getUserAuthToken(authorization);

        // Validate if user is signed in or not
        if (userAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }

        // Validate if user has signed out
        if (userAuthEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to delete an answer");
        }

        // Validate if requested answer exist or not
        if (answerDao.getAnswerByUuid(answerId) == null) {
            throw new AnswerNotFoundException("ANS-001","Entered answer uuid does not exist");
        }

        // Validate if current user is the owner of requested answer or the role of user is not nonadmin
        if(!userAuthEntity.getUser().getUuid().equals(answerDao.getAnswerByUuid(answerId).getUser().getUuid())){
            if (userAuthEntity.getUser().getRole().equals("nonadmin")) {
                throw new AuthorizationFailedException("ATHR-003", "Only the answer owner or admin can delete the answer");
            }
        }

        answerDao.userAnswerDelete(answerId);
    }
}
