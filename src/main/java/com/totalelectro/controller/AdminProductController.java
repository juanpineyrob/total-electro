package com.totalelectro.controller;

import com.totalelectro.model.Product;
import com.totalelectro.model.Category;
import com.totalelectro.service.ProductService;
import com.totalelectro.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import com.totalelectro.dto.ProductDTO;

@Controller
@RequestMapping("/admin/products")
@PreAuthorize("hasRole('ADMIN')")
public class AdminProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    private final String UPLOAD_DIR = "src/main/resources/static/images/products/";

    @GetMapping
    public String listProducts(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        return "admin/products/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "admin/products/form";
    }

    @PostMapping("/save")
    public String saveProduct(@ModelAttribute("product") Product product, 
                            @RequestParam("image") MultipartFile file,
                            RedirectAttributes redirectAttributes) {
        try {
            if (!file.isEmpty()) {
                // Create directory if it doesn't exist
                Path uploadPath = Paths.get(UPLOAD_DIR);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                // Generate unique filename
                String filename = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
                Path filePath = uploadPath.resolve(filename);
                
                // Save file
                Files.copy(file.getInputStream(), filePath);
                
                // Set image URL in product
                product.setImageUrl("/images/products/" + filename);
            } else if (product.getId() != null) {
                // Se está editando e não enviou nova imagem, mantém a imagem antiga
                Product original = productService.getProductById(product.getId());
                product.setImageUrl(original.getImageUrl());
            }

            productService.saveProduct(product);
            redirectAttributes.addFlashAttribute("success", "Producto guardado exitosamente");
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Error al guardar la imagen: " + e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al guardar el producto: " + e.getMessage());
        }
        return "redirect:/admin/products";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id);
        model.addAttribute("product", product);
        model.addAttribute("categories", categoryService.getAllCategories());
        return "admin/products/form";
    }

    @PostMapping("/update")
    public String updateProduct(@ModelAttribute Product product, RedirectAttributes redirectAttributes) {
        try {
            productService.updateProduct(product);
            redirectAttributes.addFlashAttribute("success", "Producto actualizado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar el producto: " + e.getMessage());
        }
        return "redirect:/admin/products";
    }

    @PostMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Product product = productService.getProductById(id);
            
            // Delete image file if exists
            if (product.getImageUrl() != null) {
                String filename = product.getImageUrl().substring(product.getImageUrl().lastIndexOf("/") + 1);
                Path filePath = Paths.get(UPLOAD_DIR + filename);
                Files.deleteIfExists(filePath);
            }
            
            productService.deleteProduct(id);
            redirectAttributes.addFlashAttribute("success", "Producto eliminado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar el producto: " + e.getMessage());
        }
        return "redirect:/admin/products";
    }

    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<?> getProductApi(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        ProductDTO dto = new ProductDTO(product);
        return ResponseEntity.ok(dto);
    }
} 