package com.quizzy.service;

import com.quizzy.dao.ResultDetailDAO;
import com.quizzy.factory.DAOFactory;
import com.quizzy.model.ResultDetail;
import java.util.List;
import java.util.Objects;

public class ResultDetailService {

    private final ResultDetailDAO resultDetailDAO;

    public ResultDetailService() {
        this(DAOFactory.getResultDetailDAO());
    }

    public ResultDetailService(ResultDetailDAO resultDetailDAO) {
        this.resultDetailDAO = Objects.requireNonNull(resultDetailDAO);
    }

    public ResultDetail getResultDetailById(int resultDetailId) {
        return resultDetailDAO.findById(resultDetailId);
    }

    public List<ResultDetail> getResultDetailsByResultId(int resultId) {
        if (resultId <= 0) {
            return List.of();
        }
        return resultDetailDAO.findByResultId(resultId);
    }

    public boolean createResultDetail(ResultDetail resultDetail) {
        if (resultDetail == null) {
            return false;
        }
        if (resultDetail.getOrderOfQuestion() <= 0 || resultDetail.getQuestionId() <= 0
                || resultDetail.getAnswerId() <= 0 || resultDetail.getResultId() <= 0) {
            return false;
        }
        return resultDetailDAO.insert(resultDetail);
    }

    public boolean saveResultDetails(List<ResultDetail> resultDetails) {
        if (resultDetails == null || resultDetails.isEmpty()) {
            return true;
        }
        return resultDetailDAO.insertBatch(resultDetails);
    }

    public boolean deleteResultDetailsByResultId(int resultId) {
        if (resultId <= 0) {
            return false;
        }
        return resultDetailDAO.deleteByResultId(resultId);
    }
}
