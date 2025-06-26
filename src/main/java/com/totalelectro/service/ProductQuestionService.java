package com.totalelectro.service;

import com.totalelectro.dto.ProductQuestionDTO;
import java.util.List;

public interface ProductQuestionService {
    
    List<ProductQuestionDTO> getQuestionsByProductId(Long productId);
    
    ProductQuestionDTO createQuestion(Long productId, String userEmail, String question);
    
    ProductQuestionDTO answerQuestion(Long questionId, String adminEmail, String answer);
    
    void deleteQuestion(Long questionId, String adminEmail);
    
    List<ProductQuestionDTO> getQuestionsByUser(String userEmail);
    
    Long getUnansweredQuestionsCount(Long productId);
} 