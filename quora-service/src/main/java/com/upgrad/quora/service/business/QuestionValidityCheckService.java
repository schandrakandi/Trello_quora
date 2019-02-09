package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Method to provide service for validating a question through a uuid
 */
@Service
public class QuestionValidityCheckService {

    @Autowired
    QuestionDao questionDao;

    QuestionEntity checkQuestionIsValid(String uuid) throws InvalidQuestionException {
        QuestionEntity existingQuestionEntity = questionDao.getQuestionById(uuid);

        if (existingQuestionEntity == null) {
            throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
        } else {
            return existingQuestionEntity;
        }
    }
}
