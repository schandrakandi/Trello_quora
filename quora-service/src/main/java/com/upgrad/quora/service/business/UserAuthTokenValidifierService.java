package com.upgrad.quora.service.business;

import com.upgrad.quora.service.common.EndPointIdentifier;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Method to provide service for validating a user authentication token through a access token
 */
@Service
public class UserAuthTokenValidifierService implements EndPointIdentifier {


    @Autowired
    UserDao userDao;

    boolean userAuthTokenValidityCheck(String accessToken, String endpointIdentifier) throws AuthorizationFailedException {
        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserAuthToken(accessToken);

        if (userAuthTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");

        } else {
            String logoutAt = String.valueOf(userDao.getUserAuthToken(accessToken)
                    .getLogoutAt());

            if (!logoutAt.equals("null")) {

                String errorMessage = null;
                switch(endpointIdentifier){

                    case GET_ALL_QUESTIONS:
                        errorMessage=GET_ALL_QUESTIONS;
                        break;

                    case CHECK_QUESTION:
                        errorMessage=CHECK_QUESTION;
                        break;

                    case DELETE_QUESTION:
                        errorMessage=DELETE_QUESTION;
                        break;

                    case GET_QUESTION_BY_USER:
                        errorMessage=GET_QUESTION_BY_USER;
                        break;

                    case CHECK_ANSWER:
                        errorMessage = CHECK_ANSWER;
                        break;

                    case DELETE_ANSWER:
                        errorMessage = DELETE_ANSWER;
                        break;

                    case GET_ALL_ANSWERS:
                        errorMessage = GET_ALL_ANSWERS;
                        break;


                }

                throw new AuthorizationFailedException("ATHR-002",
                        errorMessage);
            } else {
                return true;
            }
        }
    }
}
