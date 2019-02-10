package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.AnswerEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class AnswerDao {
    @PersistenceContext
    private EntityManager entityManager;

    public AnswerEntity createAnswer(AnswerEntity answerEntity) {
        entityManager.persist(answerEntity);
        return answerEntity;
    }

    public AnswerEntity getAnswerByUuid(String questionId) {
        try {
            return entityManager.createNamedQuery("answerEntityByUuid", AnswerEntity.class).setParameter("uuid", questionId).getSingleResult();

        } catch (NoResultException nre) {

            return null;
        }
    }

    public AnswerEntity editAnswerContent(final AnswerEntity answerEntity) {
        return entityManager.merge(answerEntity);
    }

    public void userAnswerDelete(final String answerId) {
        AnswerEntity answerEntity = getAnswerByUuid(answerId);
        entityManager.remove(answerEntity);
    }

    public List < AnswerEntity > getAllAnswersToQuestion(final String questionId) {
        try {
            return entityManager.createNamedQuery("answersByQuestionId", AnswerEntity.class).setParameter("uuid", questionId).getResultList();
        } catch (NoResultException nre) {
            return null;
        }
    }
}