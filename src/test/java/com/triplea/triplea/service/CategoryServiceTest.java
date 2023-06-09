package com.triplea.triplea.service;

import com.triplea.triplea.core.exception.Exception400;
import com.triplea.triplea.dto.category.CategoryResponse;
import com.triplea.triplea.model.bookmark.BookmarkCategory;
import com.triplea.triplea.model.bookmark.BookmarkCategoryRepository;
import com.triplea.triplea.model.category.MainCategory;
import com.triplea.triplea.model.category.MainCategoryRepository;
import com.triplea.triplea.model.user.User;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class CategoryServiceTest {
    @InjectMocks
    private CategoryService categoryService;
    @Mock
    private MainCategoryRepository mainCategoryRepository;
    @Mock
    private BookmarkCategoryRepository bookmarkCategoryRepository;

    private final User user = User.builder()
            .id(1L)
            .email("test@example.com")
            .password("123456")
            .fullName("tester")
            .newsLetter(true)
            .emailVerified(true)
            .userAgent("Custom User Agent")
            .clientIP("127.0.0.1")
            .profile("profile1")
            .build();

    @Nested
    @DisplayName("전체 카테고리 조회")
    class All {
        @Test
        @DisplayName("성공")
        void test() {
            //given
            MainCategory mainCategory = MainCategory.builder().mainCategoryEng("Finance").build();
            mainCategory.translateMainCategory("금융");
            //when
            when(mainCategoryRepository.findAll()).thenReturn(List.of(mainCategory));
            List<CategoryResponse> result = categoryService.getCategories();
            //then
            verify(mainCategoryRepository, times(1)).findAll();
            Assertions.assertEquals(1, result.size());
            Assertions.assertEquals(mainCategory.getId(), result.get(0).getCategoryId());
            Assertions.assertEquals(mainCategory.getMainCategoryKor(), result.get(0).getCategory());
        }
    }

    @Nested
    @DisplayName("카테고리 검색")
    class Search {
        @Nested
        @DisplayName("성공")
        class Success {
            @Test
            @DisplayName("1: 검색결과 있음")
            void test1() {
                //given
                String category = "융";
                MainCategory mainCategory = MainCategory.builder().mainCategoryEng("Finance").build();
                mainCategory.translateMainCategory("금융");
                //when
                when(mainCategoryRepository.findAll()).thenReturn(List.of(mainCategory));
                List<CategoryResponse> result = categoryService.searchCategories(category);
                //then
                verify(mainCategoryRepository, times(1)).findAll();
                Assertions.assertEquals(1, result.size());
                Assertions.assertEquals(mainCategory.getId(), result.get(0).getCategoryId());
                Assertions.assertEquals(mainCategory.getMainCategoryKor(), result.get(0).getCategory());
                Assertions.assertDoesNotThrow(() -> categoryService.searchCategories(category));
            }

            @Test
            @DisplayName("2: 검색결과 없음")
            void test2() {
                //given
                String category = "카테고리";
                MainCategory mainCategory = MainCategory.builder().mainCategoryEng("Finance").build();
                mainCategory.translateMainCategory("금융");
                //when
                when(mainCategoryRepository.findAll()).thenReturn(List.of(mainCategory));
                List<CategoryResponse> result = categoryService.searchCategories(category);
                //then
                verify(mainCategoryRepository, times(1)).findAll();
                Assertions.assertEquals(0, result.size());
                Assertions.assertDoesNotThrow(() -> categoryService.searchCategories(category));
            }
        }

        @Nested
        @DisplayName("실패")
        class Fail {
            @Test
            @DisplayName("1: 검색어 Null")
            void test1() {
                //given
                String category = null;
                //when
                //then
                Assertions.assertThrows(Exception400.class, () -> categoryService.searchCategories(category));
            }

            @Test
            @DisplayName("2: 검색어 Empty")
            void test2() {
                //given
                String category = "";
                //when
                //then
                Assertions.assertThrows(Exception400.class, () -> categoryService.searchCategories(category));
            }

            @Test
            @DisplayName("3: 검색어 Blank")
            void test3() {
                //given
                String category = " ";
                //when
                //then
                Assertions.assertThrows(Exception400.class, () -> categoryService.searchCategories(category));
            }
        }
    }

    @Nested
    @DisplayName("관심 카테고리 조회")
    class Like {
        @Test
        @DisplayName("성공1: 결과 있음")
        void test1() {
            //given
            MainCategory mainCategory = MainCategory.builder().mainCategoryEng("Finance").build();
            mainCategory.translateMainCategory("금융");
            BookmarkCategory bookmarkCategory = BookmarkCategory.builder()
                    .user(user)
                    .mainCategory(mainCategory)
                    .build();
            //when
            when(bookmarkCategoryRepository.findBookmarkCategoriesByUser(user.getId()))
                    .thenReturn(List.of(bookmarkCategory));
            List<CategoryResponse> result = categoryService.getLikeCategories(user);
            //then
            verify(bookmarkCategoryRepository, times(1)).findBookmarkCategoriesByUser(user.getId());
            Assertions.assertEquals(1, result.size());
            Assertions.assertEquals(mainCategory.getId(), result.get(0).getCategoryId());
            Assertions.assertEquals(mainCategory.getMainCategoryKor(), result.get(0).getCategory());
            Assertions.assertDoesNotThrow(() -> categoryService.getLikeCategories(user));
        }
        @Test
        @DisplayName("성공2: 결과 없음")
        void test2() {
            //given
            //when
            when(bookmarkCategoryRepository.findBookmarkCategoriesByUser(user.getId()))
                    .thenReturn(Collections.emptyList());
            List<CategoryResponse> result = categoryService.getLikeCategories(user);
            //then
            verify(bookmarkCategoryRepository, times(1)).findBookmarkCategoriesByUser(user.getId());
            Assertions.assertEquals(0, result.size());
            Assertions.assertDoesNotThrow(() -> categoryService.getLikeCategories(user));
        }
    }
}