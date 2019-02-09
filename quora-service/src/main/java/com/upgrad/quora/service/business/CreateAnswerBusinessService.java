package com.upgrad.quora.service.business;

import com.upgrad.quora.service.common.EndPointIdentifier;
import com.upgrad.quora.service.dao.AnswerDao;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class CreateAnswerBusinessService implements EndPointIdentifier {

    @Autowired
    AnswerDao answerDao;

    @Autowired
    QuestionValidityCheckService questionValidityCheckService;

    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity createAnswer(final AnswerEntity answerEntity, String questionId) throws InvalidQuestionException {

        if (questionValidityCheckService.checkQuestionIsValid(questionId) != null)
            return answerDao.createAnswer(answerEntity);
        else
            throw new InvalidQuestionException("QUES-001", "The question entered is invalid");

    }

}
