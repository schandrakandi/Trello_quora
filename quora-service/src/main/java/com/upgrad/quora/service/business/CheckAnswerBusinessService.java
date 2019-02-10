package com.upgrad.quora.service.business;

import com.upgrad.quora.service.common.EndPointIdentifier;
import com.upgrad.quora.service.dao.AnswerDao;
import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidAnswerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CheckAnswerBusinessService implements EndPointIdentifier {
    @Autowired
    UserDao userDao;

    @Autowired
    QuestionDao questionDao;

    @Autowired
    AnswerDao answerDao;

    @Autowired
    QuestionValidityCheckService questionValidityCheckService;

    @Autowired
    UserAuthTokenValidifierService userAuthTokenValidifierService;

    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity checkAnswer(String answerId, String accessToken) throws AuthorizationFailedException, InvalidAnswerException {
        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserAuthToken(accessToken);

        AnswerEntity existingAnswerEntity = null;

        if (userAuthTokenValidifierService.userAuthTokenValidityCheck(accessToken, CHECK_ANSWER)) {

            String user_id = userAuthTokenEntity.getUser().getUuid();


            AnswerEntity answerEntity = answerDao.getAnswerByUuid(answerId);

            if (existingAnswerEntity == null) {
                throw new InvalidAnswerException("ANS-001", "Entered answer uuid does not exist");
            } else if (!user_id.equals(existingAnswerEntity.getUuid())) {
                throw new AuthorizationFailedException("ATHR-003", "Only the answer owner can edit the answer");
            } else {
                existingAnswerEntity = answerEntity;
            }
        }
        return existingAnswerEntity;
    }
}
