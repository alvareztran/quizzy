package com.quizzy.dao;

import com.quizzy.model.Result;
import java.util.List;

public interface ResultDAO {
    
    Result findById(int resultId);
    List<Result> findByUserId(int userId);
    List<Result> findByQuizId(int quizId);
    List<Result> findAll();
    List<Result> findTopResults(int limit);
    boolean insert(Result result);
    boolean delete(int resultID);
     
}
