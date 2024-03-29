//package com.triplea.triplea.controller;
//
//import com.triplea.triplea.core.auth.jwt.BlackListFilter;
//import com.triplea.triplea.core.auth.jwt.MyJwtProvider;
//import com.triplea.triplea.core.config.MySecurityConfig;
//import com.triplea.triplea.core.config.RedisConfig;
//import com.triplea.triplea.core.dummy.DummyEntity;
//import com.triplea.triplea.dto.category.CategoryResponse;
//import com.triplea.triplea.model.user.User;
//import com.triplea.triplea.service.CategoryService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.context.annotation.Import;
//import org.springframework.data.redis.connection.RedisConnection;
//import org.springframework.data.redis.connection.RedisConnectionFactory;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
//
//import java.util.List;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.when;
//import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//
//@Import({MySecurityConfig.class, MyJwtProvider.class, BlackListFilter.class, RedisConfig.class})
//@WebMvcTest(CategoryController.class)
//class CategoryControllerTest extends DummyEntity {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private CategoryService categoryService;
//
//    @MockBean
//    RedisConnectionFactory redisConnectionFactory;
//
//    @BeforeEach
//    public void setUp() {
//        when(redisConnectionFactory.getConnection()).thenReturn(mock(RedisConnection.class));
//    }
//
//    private final User user = newMockUser(1L, "test@example.com", "tester");
//
//    @Test
//    @DisplayName("전체 카테고리 조회")
//    void getCategories() throws Exception {
//        //given
//        //when
//        when(categoryService.getCategories()).thenReturn(List.of(CategoryResponse.builder().build()));
//        //then
//        mockMvc.perform(get("/api/categories"))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andReturn();
//    }
//
//    @Test
//    @DisplayName("카테고리 검색")
//    void searchCategories() throws Exception {
//        //given
//        String category = "카테고리";
//        //when
//        when(categoryService.searchCategories(anyString())).thenReturn(List.of(CategoryResponse.builder().build()));
//        //then
//        mockMvc.perform(get("/api/category?search=" + category))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andReturn();
//    }
//
//    @Test
//    @DisplayName("관심 카테고리 조회")
//    void getLikeCategories() throws Exception {
//        //given
//        String accessToken = MyJwtProvider.createAccessToken(user);
//        //when
//        when(categoryService.getLikeCategories(any(User.class))).thenReturn(List.of(CategoryResponse.builder().build()));
//        //then
//        mockMvc.perform(get("/api/auth/category/like")
//                        .with(csrf())
//                        .header(MyJwtProvider.HEADER, accessToken))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andReturn();
//    }
//
//    @Test
//    @DisplayName("관심 카테고리 생성")
//    void saveLikeCategory() throws Exception {
//        //given
//        String accessToken = MyJwtProvider.createAccessToken(user);
//        //when
//
//        //then
//        mockMvc.perform(post("/api/auth/category/{id}", 1L)
//                        .with(csrf())
//                        .header(MyJwtProvider.HEADER, accessToken))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andReturn();
//    }
//
//    @Test
//    @DisplayName("관심 카테고리 삭제")
//    void deleteLikeCategory() throws Exception {
//        //given
//        String accessToken = MyJwtProvider.createAccessToken(user);
//        //when
//
//        //then
//        mockMvc.perform(delete("/api/auth/category/{id}", 1L)
//                        .with(csrf())
//                        .header(MyJwtProvider.HEADER, accessToken))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andReturn();
//    }
//}