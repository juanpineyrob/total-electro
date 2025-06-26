package com.totalelectro.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductQuestionDTO {
    
    private Long id;
    private Long productId;
    private String userName;
    private String question;
    private String answer;
    private String answeredBy;
    private LocalDateTime answeredAt;
    private LocalDateTime createdAt;
    private Boolean isAnswered;
    
    // Para criação de nova pergunta
    private String questionText;
} 