package com.quizzy.service;

import com.quizzy.dao.QuestionDAO;
import com.quizzy.dao.QuizDAO;
import com.quizzy.model.Question;
import com.quizzy.model.Quiz;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class QuizServiceTest {

    private QuizDAO mockQuizDAO;
    private QuestionDAO mockQuestionDAO;
    private QuizService quizService;

    @BeforeEach
    void setUp() {
        mockQuizDAO = new QuizDAO() {
            @Override
            public Quiz findById(int quizId) {
                if (quizId == 1) return new Quiz(1, 1, "Java Basics", 5, 10, null);
                if (quizId == 2) return new Quiz(2, 1, "Java Advanced", 10, 20, null);
                if (quizId == 3) return new Quiz(3, 1, "Java Zero Questions", 0, 10, null);
                return null;
            }

            @Override
            public List<Quiz> findByTopicId(int topicId) {
                return List.of(
                        new Quiz(1, 1, "Java Basics", 5, 10, null),
                        new Quiz(2, 1, "Java Advanced", 10, 20, null),
                        new Quiz(3, 1, "Java Zero Questions", 0, 10, null)
                );
            }

            @Override
            public Quiz findByName(String quizName) { return null; }

            @Override
            public List<Quiz> findAll() {
                return findByTopicId(1);
            }

            @Override
            public boolean insert(Quiz quiz) { return true; }

            @Override
            public boolean update(Quiz quiz) { return true; }

            @Override
            public boolean delete(int quizId) { return true; }
        };

        mockQuestionDAO = new QuestionDAO() {
            @Override
            public Question findById(int questionId) { return null; }

            @Override
            public List<Question> findAll() { return List.of(); }

            @Override
            public List<Question> findByQuizId(int quizId) { return List.of(); }

            @Override
            public int countByQuizId(int quizId) {
                if (quizId == 1) return 5; // Has 5 questions, requires 5 -> ENOUGH
                if (quizId == 2) return 3; // Has 3 questions, requires 10 -> NOT ENOUGH
                if (quizId == 3) return 0; // Requires 0 -> NOT ENOUGH (numberOfQuestions <= 0)
                return 0;
            }

            @Override
            public List<Question> findRandomByQuizId(int quizId, int numberOfQuestions) { return List.of(); }

            @Override
            public boolean insert(Question question) { return true; }

            @Override
            public boolean update(Question question) { return true; }

            @Override
            public boolean delete(int questionId) { return true; }
        };

        quizService = new QuizService(mockQuizDAO, mockQuestionDAO);
    }

    @Test
    @DisplayName("hasEnoughQuestions should return true only when available questions >= required numberOfQuestions (> 0)")
    void testHasEnoughQuestions() {
        Quiz enoughQuiz = new Quiz(1, 1, "Java Basics", 5, 10, null);
        Quiz notEnoughQuiz = new Quiz(2, 1, "Java Advanced", 10, 20, null);
        Quiz zeroQQuiz = new Quiz(3, 1, "Java Zero Questions", 0, 10, null);

        assertTrue(quizService.hasEnoughQuestions(enoughQuiz));
        assertFalse(quizService.hasEnoughQuestions(notEnoughQuiz));
        assertFalse(quizService.hasEnoughQuestions(zeroQQuiz));
        assertFalse(quizService.hasEnoughQuestions(null));
    }

    @Test
    @DisplayName("getPlayableQuizzesByTopicId should only return quizzes with enough questions")
    void testGetPlayableQuizzesByTopicId() {
        List<Quiz> playableQuizzes = quizService.getPlayableQuizzesByTopicId(1);
        assertEquals(1, playableQuizzes.size());
        assertEquals("Java Basics", playableQuizzes.get(0).getQuizName());
    }

    @Test
    @DisplayName("getAllPlayableQuizzes should only return playable quizzes")
    void testGetAllPlayableQuizzes() {
        List<Quiz> playableQuizzes = quizService.getAllPlayableQuizzes();
        assertEquals(1, playableQuizzes.size());
        assertEquals(1, playableQuizzes.get(0).getQuizId());
    }
}
