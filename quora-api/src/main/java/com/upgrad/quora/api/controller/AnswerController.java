package com.upgrad.quora.api.controller;


import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.*;
import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/")
public class AnswerController {

    @Autowired
    CreateAnswerBusinessService createAnswerBusinessService;

    @Autowired
    EditAnswerBusinessService editAnswerBusinessService;

    @Autowired
    DeleteAnswerBusinessService deleteAnswerBusinessService;

    @Autowired
    GetAllAnswerBusinessService getAllAnswerBusinessService;

    @Autowired
    QuestionDao questionDao;

    /**
     * @param  answerRequest the first {@code AnswerRequest} to create a particular answer.
     * @param  questionId the second {@code String} to associate the answer to that question.
     * @param  authorization the third {@code String} to check if the access is available.
     * @return ResponseEntity is returned with Status CREATED
     */
    @PostMapping(path = "/question/{questionId}/answer/create", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerResponse> createAnswer(final AnswerRequest answerRequest,
        @PathVariable("questionId") final String questionId, @RequestHeader("authorization") final String authorization)
        throws AuthorizationFailedException, InvalidQuestionException
    {
        // Logic to handle Bearer <accesstoken>
        // User can give only Access token or Bearer <accesstoken> as input.
        String bearerToken = null;
        try {
            bearerToken = authorization.split("Bearer ")[1];
        } catch (ArrayIndexOutOfBoundsException e) {
            bearerToken = authorization;
        }

        // Create answer entity
        final AnswerEntity answerEntity = new AnswerEntity();
        answerEntity.setAnswer(answerRequest.getAnswer());

        // Return response with created answer entity
        final AnswerEntity createdAnswerEntity =
            createAnswerBusinessService.createAnswer(answerEntity, questionId, bearerToken);
        AnswerResponse answerResponse = new AnswerResponse().id(createdAnswerEntity.getUuid()).status("ANSWER CREATED");
        return new ResponseEntity<AnswerResponse>(answerResponse, HttpStatus.CREATED);
    }

    /**
     * @param  answerEditRequest the first {@code AnswerEditRequest} with answer to edit
     * @param  answerId the second {@code String} to edit the previous answer
     * @param  authorization the third {@code String} to check if the access is available.
     * @return ResponseEntity is returned with Status OK
     */
    @RequestMapping(method = RequestMethod.PUT, path = "/answer/edit/{answerId}",
        consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerEditResponse> editAnswerContent(final AnswerEditRequest answerEditRequest,
        @PathVariable("answerId") final String answerId, @RequestHeader("authorization") final String authorization)
        throws AuthorizationFailedException, AnswerNotFoundException
    {
        // Logic to handle Bearer <accesstoken>
        // User can give only Access token or Bearer <accesstoken> as input.
        String bearerToken = null;
        try {
            bearerToken = authorization.split("Bearer ")[1];
        } catch (ArrayIndexOutOfBoundsException e) {
            bearerToken = authorization;
        }

        // Created answer entity for further update
        AnswerEntity answerEntity = new AnswerEntity();
        answerEntity.setAnswer(answerEditRequest.getContent());
        answerEntity.setUuid(answerId);

        // Return response with updated answer entity
        AnswerEntity updatedAnswerEntity = editAnswerBusinessService.editAnswerContent(answerEntity, bearerToken);
        AnswerEditResponse answerEditResponse = new AnswerEditResponse().id(updatedAnswerEntity.getUuid()).status("ANSWER EDITED");
        return new ResponseEntity<AnswerEditResponse>(answerEditResponse, HttpStatus.OK);
    }

    /**
     * @param  answerId the first {@code AnswerEditRequest} with answer to delete
     * @param  authorization the second {@code String} to check if the access is available.
     * @return ResponseEntity is returned with Status OK if deleted successfully.
     */
    @RequestMapping(method = RequestMethod.DELETE, path = "/answer/delete/{answerId}",
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerDeleteResponse> deleteAnswer(@PathVariable("answerId") final String answerId,
    @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, AnswerNotFoundException
    {
        // Logic to handle Bearer <accesstoken>
        // User can give only Access token or Bearer <accesstoken> as input.
        String bearerToken = null;
        try {
            bearerToken = authorization.split("Bearer ")[1];
        } catch (ArrayIndexOutOfBoundsException e) {
            bearerToken = authorization;
        }

        // Delete requested answer
        deleteAnswerBusinessService.deleteAnswer(answerId, bearerToken);

        // Return response
        AnswerDeleteResponse answerDeleteResponse = new AnswerDeleteResponse().id(answerId).status("ANSWER DELETED");
        return new ResponseEntity<AnswerDeleteResponse>(answerDeleteResponse, HttpStatus.OK);
    }

    /**
     * @param  questionId the first {@code AnswerEditRequest} to fetch all answers for a question.
     * @param  authorization the second {@code String} to check if the access is available.
     * @return ResponseEntity is returned with Status FOUND with all related records.
     */
    @RequestMapping(method = RequestMethod.GET, path = "/answer/all/{questionId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<AnswerDetailsResponse>> getAllAnswersToQuestion (@PathVariable("questionId") final String questionId,
        @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, InvalidQuestionException
    {
        // Logic to handle Bearer <accesstoken>
        // User can give only Access token or Bearer <accesstoken> as input.
        String bearerToken = null;
        try {
            bearerToken = authorization.split("Bearer ")[1];
        } catch (ArrayIndexOutOfBoundsException e) {
            bearerToken = authorization;
        }

        // Get all answers for requested question
        List<AnswerEntity> allAnswers = getAllAnswerBusinessService.getAllAnswersToQuestion(questionId, bearerToken);

        // Create response
        List<AnswerDetailsResponse> allAnswersResponse = new ArrayList<AnswerDetailsResponse>();

        for (int i = 0; i < allAnswers.size(); i++) {
            AnswerDetailsResponse answerDetailsResponse = new AnswerDetailsResponse()
                    .answerContent(allAnswers.get(i).getAnswer())
                    .questionContent(allAnswers.get(i).getQuestion().getContent())
                    .id(allAnswers.get(i).getUuid());
            allAnswersResponse.add(answerDetailsResponse);
        }

        // Return response
        return new ResponseEntity<List<AnswerDetailsResponse>>(allAnswersResponse, HttpStatus.FOUND);
    }
}