package com.quizzy.factory;

import com.quizzy.service.AnswerService;
import com.quizzy.service.AuthService;
import com.quizzy.service.QuestionService;
import com.quizzy.service.QuizService;
import com.quizzy.service.ResultService;
import com.quizzy.service.TopicService;
import com.quizzy.service.UserService;

public class ServiceFactory {
    
    private static final AnswerService ANSWER_SERVICE = new AnswerService(DAOFactory.getAnswerDAO());
    private static final AuthService AUTH_SERVICE = new AuthService(DAOFactory.getUserDAO());
    private static final UserService USER_SERVICE = new UserService(DAOFactory.getUserDAO());
    private static final QuestionService QUESTION_SERVICE = new QuestionService(DAOFactory.getQuestionDAO());
    private static final QuizService QUIZ_SERVICE = new QuizService(DAOFactory.getQuizDAO());
    private static final ResultService RESULT_SERVICE = new ResultService(DAOFactory.getResultDAO());
    private static final TopicService TOPIC_SERVICE = new TopicService(DAOFactory.getTopicDAO());
    
    private ServiceFactory() {
    }
    
    public static AnswerService getAnswerService() {
        return ANSWER_SERVICE;
    }
    
    public static AuthService getAuthService() {
        return AUTH_SERVICE;
    }

    public static UserService getUserService() {
        return USER_SERVICE;
    }
    
    public static QuestionService getQuestionService() {
        return QUESTION_SERVICE;
    }
    
    public static QuizService getQuizService() {
        return QUIZ_SERVICE;
    }
    
    public static ResultService getResultService() {
        return RESULT_SERVICE;
    }
    
    public static TopicService getTopicService() {
        return TOPIC_SERVICE;
    }
    
}
