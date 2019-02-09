package com.upgrad.quora.service.business;

import com.upgrad.quora.service.common.EndPointIdentifier;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class AuthorizationService implements EndPointIdentifier {

    @Autowired
    private UserDao userDao;

    /**
     * Method to authorize a user based on the given access token
     *
     * @param accessToken assigned to the User
     * @return UserAuthTokenEntity which has the authorisation details of the user
     * @throws AuthorizationFailedException
     */
    public UserAuthTokenEntity verifyAuthToken(String accessToken, String endpointIdentifier) throws AuthorizationFailedException {

        if (userDao.getUserAuthToken(accessToken) == null) {

            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        } else {

            String logoutAt = String.valueOf(userDao.getUserAuthToken(accessToken)
                    .getLogoutAt());

            if (!logoutAt.equals("null")) {
                String error = null;

                if (endpointIdentifier.equals(QUESTION_ENDPOINT)) {
                    error = QUESTION_ENDPOINT;
                } else if (endpointIdentifier.equals(ANSWER_ENDPOINT)) {
                    error = ANSWER_ENDPOINT;
                } else if (endpointIdentifier.equals(USER_ENDPOINT)) {
                    error = USER_ENDPOINT;
                }
                throw new AuthorizationFailedException("ATHR-002", error);
            } else {

                return userDao.getUserAuthToken(accessToken);
            }

        }
    }

}
