package com.totalelectro;

import com.totalelectro.controller.CheckoutController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import com.totalelectro.service.ProductService;
import com.totalelectro.service.UserService;
import com.totalelectro.service.OrderService;
import com.totalelectro.service.CartService;
import com.totalelectro.model.Product;
import org.mockito.Mockito;
import java.math.BigDecimal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@WebMvcTest(CheckoutController.class)
class CheckoutControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;
    @MockBean
    private UserService userService;
    @MockBean
    private OrderService orderService;
    @MockBean
    private CartService cartService;
    @MockBean
    private PasswordEncoder passwordEncoder;

    @Test
    @WithMockUser
    @DisplayName("GET /checkout retorna status 200")
    void testShowCheckout() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/checkout"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @WithMockUser
    @DisplayName("POST /checkout/process con datos válidos redirige con éxito")
    void testProcessCheckoutSuccess() throws Exception {
        Product product = new Product();
        product.setId(1L);
        product.setPrice(BigDecimal.valueOf(100));
        Mockito.when(productService.getProductById(1L)).thenReturn(product);

        mockMvc.perform(MockMvcRequestBuilders.post("/checkout/process")
                .param("productId", "1")
                .param("quantity", "2")
                .param("firstName", "Juan")
                .param("lastName", "Pérez")
                .param("email", "juan@mail.com")
                .param("phone", "11999999999")
                .param("address", "Rua Teste, 123")
                .param("city", "São Paulo")
                .param("state", "SP")
                .param("zipCode", "01001-000")
                .with(csrf())
        )
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/"));
    }

    @Test
    @WithMockUser
    @DisplayName("POST /checkout/process sin nombre retorna error")
    void testProcessCheckoutMissingName() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/checkout/process")
                .param("productId", "1")
                .param("quantity", "2")
                .param("firstName", "")
                .param("lastName", "Pérez")
                .param("email", "juan@mail.com")
                .param("phone", "11999999999")
                .param("address", "Rua Teste, 123")
                .param("city", "São Paulo")
                .param("state", "SP")
                .param("zipCode", "01001-000")
                .with(csrf())
        )
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    @WithMockUser
    @DisplayName("POST /checkout/process con email inválido retorna error")
    void testProcessCheckoutInvalidEmail() throws Exception {
        Product product = new Product();
        product.setId(1L);
        product.setPrice(BigDecimal.valueOf(100));
        Mockito.when(productService.getProductById(1L)).thenReturn(product);

        mockMvc.perform(MockMvcRequestBuilders.post("/checkout/process")
                .param("productId", "1")
                .param("quantity", "2")
                .param("firstName", "Juan")
                .param("lastName", "Pérez")
                .param("email", "no-es-un-email")
                .param("phone", "11999999999")
                .param("address", "Rua Teste, 123")
                .param("city", "São Paulo")
                .param("state", "SP")
                .param("zipCode", "01001-000")
                .with(csrf())
        )
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/checkout"));
    }

    @Test
    @WithMockUser
    @DisplayName("POST /checkout/process con CEP inválido retorna error")
    void testProcessCheckoutInvalidCep() throws Exception {
        Product product = new Product();
        product.setId(1L);
        product.setPrice(BigDecimal.valueOf(100));
        Mockito.when(productService.getProductById(1L)).thenReturn(product);

        mockMvc.perform(MockMvcRequestBuilders.post("/checkout/process")
                .param("productId", "1")
                .param("quantity", "2")
                .param("firstName", "Juan")
                .param("lastName", "Pérez")
                .param("email", "juan@mail.com")
                .param("phone", "11999999999")
                .param("address", "Rua Teste, 123")
                .param("city", "São Paulo")
                .param("state", "SP")
                .param("zipCode", "000")
                .with(csrf())
        )
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/checkout"));
    }
} 