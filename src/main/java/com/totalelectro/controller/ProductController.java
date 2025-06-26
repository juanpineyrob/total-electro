package com.totalelectro.controller;

import com.totalelectro.constants.Pages;
import com.totalelectro.constants.PathConstants;
import com.totalelectro.dto.CategoryDTO;
import com.totalelectro.dto.ProductQuestionDTO;
import com.totalelectro.model.Product;
import com.totalelectro.service.ProductService;
import com.totalelectro.service.CategoryService;
import com.totalelectro.service.ProductQuestionService;
import com.totalelectro.utils.ControllerUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.springframework.http.ResponseEntity;

@Controller
@RequiredArgsConstructor
@RequestMapping(PathConstants.PRODUCT)
@Slf4j
public class ProductController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final ProductQuestionService questionService;
    private final ControllerUtils controllerUtils;

    @GetMapping("/{id}")
    public String getProduct(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id);
        List<ProductQuestionDTO> questions = questionService.getQuestionsByProductId(id);
        Long unansweredCount = questionService.getUnansweredQuestionsCount(id);
        
        log.info("Carregando produto {} com {} perguntas", id, questions.size());
        
        model.addAttribute("product", product);
        model.addAttribute("questions", questions);
        model.addAttribute("unansweredCount", unansweredCount);
        return Pages.PRODUCT;
    }

    @PostMapping("/{id}/question")
    @ResponseBody
    public ResponseEntity<String> createQuestion(@PathVariable Long id, @RequestParam String question) {
        log.info("Recebendo pergunta para produto {}: {}", id, question);
        
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            log.info("Autenticação: {}", auth != null ? auth.getName() : "null");
            
            if (auth == null || !auth.isAuthenticated() || auth.getName().equals("anonymousUser")) {
                log.warn("Usuário não autenticado tentando criar pergunta");
                return ResponseEntity.status(401).body("Você precisa estar logado para fazer perguntas");
            }
            
            String userEmail = auth.getName();
            log.info("Criando pergunta para usuário: {}", userEmail);
            
            questionService.createQuestion(id, userEmail, question);
            log.info("Pergunta criada com sucesso");
            
            return ResponseEntity.ok("success:Pergunta criada com sucesso");
        } catch (Exception e) {
            log.error("Erro ao criar pergunta", e);
            return ResponseEntity.badRequest().body("error:" + e.getMessage());
        }
    }

    @PostMapping("/question/{questionId}/answer")
    @ResponseBody
    public ResponseEntity<String> answerQuestion(@PathVariable Long questionId, @RequestParam String answer) {
        log.info("Recebendo resposta para pergunta {}: {}", questionId, answer);
        
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || auth.getName().equals("anonymousUser")) {
                return ResponseEntity.status(401).body("Você precisa estar logado para responder perguntas");
            }
            
            String adminEmail = auth.getName();
            questionService.answerQuestion(questionId, adminEmail, answer);
            return ResponseEntity.ok("success:Resposta enviada com sucesso");
        } catch (Exception e) {
            log.error("Erro ao responder pergunta", e);
            return ResponseEntity.badRequest().body("error:" + e.getMessage());
        }
    }

    @DeleteMapping("/question/{questionId}")
    @ResponseBody
    public ResponseEntity<String> deleteQuestion(@PathVariable Long questionId) {
        log.info("Recebendo solicitação para excluir pergunta: {}", questionId);
        
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || auth.getName().equals("anonymousUser")) {
                return ResponseEntity.status(401).body("Você precisa estar logado para excluir perguntas");
            }
            
            String adminEmail = auth.getName();
            questionService.deleteQuestion(questionId, adminEmail);
            return ResponseEntity.ok("success:Pergunta removida com sucesso");
        } catch (Exception e) {
            log.error("Erro ao excluir pergunta", e);
            return ResponseEntity.badRequest().body("error:" + e.getMessage());
        }
    }

    @GetMapping("/category/{categorySlug}")
    public String getProductsByCategory(
            @PathVariable String categorySlug,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(defaultValue = "relevance") String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            Model model) {
        CategoryDTO category = categoryService.getCategoryBySlug(categorySlug);
        Sort sorting = getSort(sort);
        Pageable pageable = PageRequest.of(page, size, sorting);
        Page<Product> products = productService.getProductsByCategory(categorySlug, minPrice, maxPrice, pageable);
        
        model.addAttribute("page", products);
        model.addAttribute("categorySlug", categorySlug);
        model.addAttribute("categoryName", category.getName());
        model.addAttribute("allCategories", categoryService.getAllCategories());
        model.addAttribute("pagination", products.getTotalPages() > 0 ?
            java.util.stream.IntStream.rangeClosed(0, products.getTotalPages() - 1).boxed().toList() : null);
        
        return "category/products";
    }

    @GetMapping("/search")
    public String searchProducts(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) List<String> categories,
            @RequestParam(defaultValue = "relevance") String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            Model model) {
        Sort sorting = getSort(sort);
        Pageable pageable = PageRequest.of(page, size, sorting);
        Page<Product> products = productService.searchProducts(name, minPrice, maxPrice, categories, pageable);

        model.addAttribute("page", products);
        model.addAttribute("allCategories", categoryService.getAllCategories());
        model.addAttribute("searchQuery", name);
        model.addAttribute("pagination", products.getTotalPages() > 0 ?
            java.util.stream.IntStream.rangeClosed(0, products.getTotalPages() - 1).boxed().toList() : null);

        return Pages.PRODUCTS;
    }

    private Sort getSort(String sort) {
        switch (sort) {
            case "price-asc":
                return Sort.by("price").ascending();
            case "price-desc":
                return Sort.by("price").descending();
            case "name-asc":
                return Sort.by("name").ascending();
            case "name-desc":
                return Sort.by("name").descending();
            default: // relevance
                return Sort.unsorted();
        }
    }

    @GetMapping("/popular")
    public String getPopularProducts(Model model) {
        model.addAttribute("products", productService.getPopularProducts());
        return Pages.PRODUCTS;
    }
}
