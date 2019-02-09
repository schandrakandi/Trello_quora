package com.upgrad.quora.api.controller;


import com.upgrad.quora.api.model.AnswerEditRequest;
import com.upgrad.quora.api.model.AnswerEditResponse;
import com.upgrad.quora.api.model.AnswerRequest;
import com.upgrad.quora.api.model.AnswerResponse;
import com.upgrad.quora.service.business.AuthorizationService;
import com.upgrad.quora.service.business.CheckAnswerBusinessService;
import com.upgrad.quora.service.business.CreateAnswerBusinessService;
import com.upgrad.quora.service.business.UpdateAnswerBusinessService;
import com.upgrad.quora.service.common.EndPointIdentifier;
import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidAnswerException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;

@RestController
@RequestMapping("/")
public class AnswerController implements EndPointIdentifier {

    @Autowired
    AuthorizationService authorizationService;

    @Autowired
    CreateAnswerBusinessService createAnswerBusinessService;

    @Autowired
    CheckAnswerBusinessService checkAnswerBusinessService;

    @Autowired
    UpdateAnswerBusinessService updateAnswerBusinessService;

    @Autowired
    QuestionDao questionDao;


    @PostMapping(path = "/question/{questionId}/answer/create", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerResponse> createAnswer(@RequestHeader("authorization") String accessToken, @PathVariable String questionId,
                                                       final AnswerRequest answerRequest) throws
            AuthorizationFailedException, InvalidQuestionException {

        final AnswerEntity answerEntity = new AnswerEntity();
        UserAuthTokenEntity userAuthTokenEntity = authorizationService.verifyAuthToken(accessToken, ANSWER_ENDPOINT);
        answerEntity.setUser(userAuthTokenEntity.getUser());
        answerEntity.setAns(answerRequest.getAnswer());
        answerEntity.setDate(ZonedDateTime.now());


        final AnswerEntity createdAnswerEntity = createAnswerBusinessService.createAnswer(answerEntity, questionId);
        AnswerResponse answerResponse = new AnswerResponse().id(createdAnswerEntity.getUuid())
                .status("ANSWER CREATED");

        return new ResponseEntity<AnswerResponse>(answerResponse, HttpStatus.OK);
    }

    @PutMapping(path = "/answer/edit/{answerId}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerEditResponse> editAnswerContent(AnswerEditRequest answerEditRequest, @RequestHeader("authorization") String accessToken, @PathVariable String answerId)
            throws AuthorizationFailedException, InvalidAnswerException {


        AnswerEntity answerEntity = checkAnswerBusinessService.checkAnswer(answerId, accessToken);
        answerEntity.setAns(answerEditRequest.getContent());
        AnswerEntity updatedAnswerEntity = updateAnswerBusinessService.updateAnswer(answerEntity);

        AnswerEditResponse answerEditResponse = new AnswerEditResponse().id(updatedAnswerEntity.getUuid()).status("ANSWER EDITED");

        return new ResponseEntity<AnswerEditResponse>(answerEditResponse, HttpStatus.OK);
    }
}