package com.example.ecsite.controller;

import com.example.ecsite.dto.ProductResponse;
import com.example.ecsite.service.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** 既存の商品 API のテスト。受け入れ基準をテストで固定する形の見本になる。 */
@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    private ProductResponse response(Long id, String name, int price, String category, int stock) {
        return new ProductResponse(id, name, name + " description", price,
                "images/dummy.jpg", category, stock);
    }

    @Test
    @DisplayName("GET /api/products は 200 と一覧を返す")
    void getAll_returns200() throws Exception {
        when(productService.searchProducts(null, null))
            .thenReturn(List.of(response(1L, "ボールペン", 120, "writing", 120)));

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("ボールペン"))
                .andExpect(jsonPath("$[0].category").value("writing"));
    }

            @Test
            @DisplayName("キーワードとカテゴリで商品を検索できる")
            void search_returnsFilteredProducts() throws Exception {
            when(productService.searchProducts("pen", "writing"))
                .thenReturn(List.of(response(1L, "Pen", 120, "writing", 120)));

            mockMvc.perform(get("/api/products")
                    .param("keyword", "pen")
                    .param("category", "writing"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Pen"))
                .andExpect(jsonPath("$[0].category").value("writing"))
                .andExpect(jsonPath("$[0].description").value("Pen description"));
            }

            @Test
            @DisplayName("検索結果がない場合は空配列を返す")
            void search_returnsEmptyArray() throws Exception {
            when(productService.searchProducts("missing", null)).thenReturn(List.of());

            mockMvc.perform(get("/api/products").param("keyword", "missing"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
            }

    @Test
    @DisplayName("存在しない id は 404 を返す")
    void getById_returns404() throws Exception {
        when(productService.getProductById(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/products/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("商品詳細は表示項目を返す")
    void getById_returnsDetails() throws Exception {
        when(productService.getProductById(1L))
                .thenReturn(Optional.of(response(1L, "Pen", 120, "writing", 120)));

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Pen"))
                .andExpect(jsonPath("$.description").value("Pen description"))
                .andExpect(jsonPath("$.price").value(120))
                .andExpect(jsonPath("$.category").value("writing"));
    }
}
