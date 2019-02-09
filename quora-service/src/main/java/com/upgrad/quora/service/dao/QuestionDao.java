package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.QuestionEntity;
import org.springframework.stereotype.Repository;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class QuestionDao {
    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Method to create a new QuestionEntity
     *
     * @param questionEntity contains the questionEntity to be persisted
     * @return QuestionEntity that has been persisted in the database
     */
    public QuestionEntity createQuestion(QuestionEntity questionEntity) {
        entityManager.persist(questionEntity);
        return questionEntity;
    }

    /**
     * Method to get the List of All the questions
     *
     * @return List<QuestionEntity>
     */
    public List < QuestionEntity > getAllQuestions() {
        try {
            return entityManager.createNamedQuery("allQuestions", QuestionEntity.class).getResultList();
        } catch (NoResultException nre) {

            return null;
        }
    }

    /**
     * Method to get the QuestionEntity by uuid
     *
     * @param questionId
     * @return QuestionEntity
     */

    public QuestionEntity getQuestionById(String questionId) {
        try {
            return entityManager.createNamedQuery("questionById", QuestionEntity.class).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }
}