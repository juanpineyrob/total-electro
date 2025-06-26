package com.totalelectro.service.impl;

import com.totalelectro.dto.ProductQuestionDTO;
import com.totalelectro.model.ProductQuestion;
import com.totalelectro.model.Product;
import com.totalelectro.model.User;
import com.totalelectro.repository.ProductQuestionRepository;
import com.totalelectro.service.ProductQuestionService;
import com.totalelectro.service.ProductService;
import com.totalelectro.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductQuestionServiceImpl implements ProductQuestionService {

    private final ProductQuestionRepository questionRepository;
    private final ProductService productService;
    private final UserService userService;

    @Override
    public List<ProductQuestionDTO> getQuestionsByProductId(Long productId) {
        log.info("Buscando perguntas para produto: {}", productId);
        List<ProductQuestion> questions = questionRepository.findByProductIdOrderByAnsweredAndDate(productId);
        log.info("Encontradas {} perguntas para produto {}", questions.size(), productId);
        return questions.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ProductQuestionDTO createQuestion(Long productId, String userEmail, String question) {
        log.info("Criando pergunta para produto {} do usuário {}: {}", productId, userEmail, question);
        
        try {
            User user = userService.findByEmail(userEmail);
            log.info("Usuário encontrado: {} {}", user.getFirstName(), user.getLastName());
            
            Product product = productService.getProductById(productId);
            log.info("Produto encontrado: {}", product.getName());
            
            ProductQuestion productQuestion = new ProductQuestion();
            productQuestion.setProduct(product);
            productQuestion.setUser(user);
            productQuestion.setQuestion(question);
            productQuestion.setIsAnswered(false);
            
            ProductQuestion savedQuestion = questionRepository.save(productQuestion);
            log.info("Pergunta salva com ID: {}", savedQuestion.getId());
            
            return convertToDTO(savedQuestion);
        } catch (Exception e) {
            log.error("Erro ao criar pergunta", e);
            throw e;
        }
    }

    @Override
    @Transactional
    public ProductQuestionDTO answerQuestion(Long questionId, String adminEmail, String answer) {
        log.info("Respondendo pergunta {} do admin {}: {}", questionId, adminEmail, answer);
        
        try {
            ProductQuestion question = questionRepository.findById(questionId)
                    .orElseThrow(() -> new RuntimeException("Pergunta não encontrada"));
            
            User admin = userService.findByEmail(adminEmail);
            
            question.setAnswer(answer);
            question.setAnsweredBy(admin);
            question.setAnsweredAt(LocalDateTime.now());
            question.setIsAnswered(true);
            
            ProductQuestion savedQuestion = questionRepository.save(question);
            log.info("Resposta salva para pergunta: {}", questionId);
            
            return convertToDTO(savedQuestion);
        } catch (Exception e) {
            log.error("Erro ao responder pergunta", e);
            throw e;
        }
    }

    @Override
    @Transactional
    public void deleteQuestion(Long questionId, String adminEmail) {
        log.info("Excluindo pergunta {} pelo admin {}", questionId, adminEmail);
        
        try {
            ProductQuestion question = questionRepository.findById(questionId)
                    .orElseThrow(() -> new RuntimeException("Pergunta não encontrada"));
            
            // Verificar se o usuário é admin (você pode implementar sua própria lógica de verificação)
            User admin = userService.findByEmail(adminEmail);
            
            questionRepository.delete(question);
            log.info("Pergunta excluída: {}", questionId);
        } catch (Exception e) {
            log.error("Erro ao excluir pergunta", e);
            throw e;
        }
    }

    @Override
    public List<ProductQuestionDTO> getQuestionsByUser(String userEmail) {
        log.info("Buscando perguntas do usuário: {}", userEmail);
        User user = userService.findByEmail(userEmail);
        List<ProductQuestion> questions = questionRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
        return questions.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Long getUnansweredQuestionsCount(Long productId) {
        log.info("Contando perguntas não respondidas para produto: {}", productId);
        Long count = questionRepository.countUnansweredQuestionsByProductId(productId);
        log.info("Perguntas não respondidas: {}", count);
        return count;
    }

    private ProductQuestionDTO convertToDTO(ProductQuestion question) {
        ProductQuestionDTO dto = new ProductQuestionDTO();
        dto.setId(question.getId());
        dto.setProductId(question.getProduct().getId());
        dto.setUserName(question.getUser().getFirstName() + " " + question.getUser().getLastName());
        dto.setQuestion(question.getQuestion());
        dto.setAnswer(question.getAnswer());
        dto.setAnsweredBy(question.getAnsweredBy() != null ? 
            question.getAnsweredBy().getFirstName() + " " + question.getAnsweredBy().getLastName() : null);
        dto.setAnsweredAt(question.getAnsweredAt());
        dto.setCreatedAt(question.getCreatedAt());
        dto.setIsAnswered(question.getIsAnswered());
        return dto;
    }
} 