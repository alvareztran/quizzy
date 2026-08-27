package com.quizzy.factory;

import com.quizzy.dao.AnswerDAO;
import com.quizzy.dao.QuestionDAO;
import com.quizzy.dao.QuizDAO;
import com.quizzy.dao.ResultDAO;
import com.quizzy.dao.ResultDetailDAO;
import com.quizzy.dao.TopicDAO;
import com.quizzy.dao.UserDAO;
import com.quizzy.dao.AnswerDAOImpl;
import com.quizzy.dao.QuestionDAOImpl;
import com.quizzy.dao.QuizDAOImpl;
import com.quizzy.dao.ResultDAOImpl;
import com.quizzy.dao.ResultDetailDAOImpl;
import com.quizzy.dao.TopicDAOImpl;
import com.quizzy.dao.UserDAOImpl;

public class DAOFactory {

    private static final AnswerDAO ANSWER_DAO = new AnswerDAOImpl();
    private static final QuestionDAO QUESTION_DAO = new QuestionDAOImpl();
    private static final QuizDAO QUIZ_DAO = new QuizDAOImpl();
    private static final ResultDAO RESULT_DAO = new ResultDAOImpl();
    private static final ResultDetailDAO RESULT_DETAIL_DAO = new ResultDetailDAOImpl();
    private static final TopicDAO TOPIC_DAO = new TopicDAOImpl();
    private static final UserDAO USER_DAO = new UserDAOImpl();

    private DAOFactory() {
    }

    public static AnswerDAO getAnswerDAO() {
        return ANSWER_DAO;
    }

    public static QuestionDAO getQuestionDAO() {
        return QUESTION_DAO;
    }

    public static QuizDAO getQuizDAO() {
        return QUIZ_DAO;
    }

    public static ResultDAO getResultDAO() {
        return RESULT_DAO;
    }

    public static ResultDetailDAO getResultDetailDAO() {
        return RESULT_DETAIL_DAO;
    }

    public static TopicDAO getTopicDAO() {
        return TOPIC_DAO;
    }

    public static UserDAO getUserDAO() {
        return USER_DAO;
    }

}
