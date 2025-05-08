package com.totalelectro.controller;

import com.totalelectro.constants.Pages;
import com.totalelectro.constants.PathConstants;
import com.totalelectro.dto.ProductDTO;
import com.totalelectro.model.Product;
import com.totalelectro.service.AdminService;
import com.totalelectro.service.ProductService;
import com.totalelectro.utils.ControllerUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequiredArgsConstructor
@RequestMapping(PathConstants.ADMIN)
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private ProductService productService;

    @GetMapping(Pages.ADMIN_ADD_PRODUCT)
    public String addProduct(Model model) {
        return Pages.ADMIN_ADD_PRODUCT;
    }

    @PostMapping(Pages.ADMIN_ADD_PRODUCT)
    public ResponseEntity<ProductDTO> addProduct(@Valid ProductDTO productDTO, BindingResult bindingResult, Model model) {
        ResponseEntity<ProductDTO> response = adminService.insert(productDTO, bindingResult, model);
        return response; // DEBUG
    }
}
