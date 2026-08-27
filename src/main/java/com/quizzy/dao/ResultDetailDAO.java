package com.quizzy.dao;

import com.quizzy.model.ResultDetail;
import java.util.List;

public interface ResultDetailDAO {

    ResultDetail findById(int resultDetailId);

    List<ResultDetail> findByResultId(int resultId);

    boolean insert(ResultDetail resultDetail);

    boolean insertBatch(List<ResultDetail> resultDetails);

    boolean deleteByResultId(int resultId);
}
