package com.triplea.triplea.service;

import com.triplea.triplea.core.dummy.DummyEntity;
import com.triplea.triplea.core.exception.Exception400;
import com.triplea.triplea.dto.category.CategoryResponse;
import com.triplea.triplea.model.bookmark.BookmarkCategory;
import com.triplea.triplea.model.bookmark.BookmarkCategoryRepository;
import com.triplea.triplea.model.category.MainCategory;
import com.triplea.triplea.model.category.MainCategoryRepository;
import com.triplea.triplea.model.user.User;
import com.triplea.triplea.model.user.UserRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class CategoryServiceTest extends DummyEntity {
    @InjectMocks
    private CategoryService categoryService;
    @Mock
    private MainCategoryRepository mainCategoryRepository;
    @Mock
    private BookmarkCategoryRepository bookmarkCategoryRepository;

    @Mock
    private UserRepository userRepository;

    private final User user = newMockUser(1L, "test@example.com", "tester");

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

    @Nested
    @DisplayName("관심 카테고리 생성")
    class SaveLikeCategory {
        @Test
        @DisplayName("성공")
        void test1() {
            //given
            MainCategory mainCategory = MainCategory.builder().mainCategoryEng("Finance").build();

            //when
            when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
            when(mainCategoryRepository.findById(anyLong())).thenReturn(Optional.ofNullable(mainCategory));

            //then
            Assertions.assertDoesNotThrow(() -> categoryService.saveLikeCategory(1L, 1L));
        }

        @Test
        @DisplayName("실패1: 잘못된 userId")
        void test2() {
            //given
            MainCategory mainCategory = MainCategory.builder().mainCategoryEng("Finance").build();

            //when

            //then
            Assertions.assertThrows(Exception400.class, () -> categoryService.saveLikeCategory(2L, 1L));

        }

        @Test
        @DisplayName("실패2: 잘못된 categoryId")
        void test3() {
            //given
            MainCategory mainCategory = MainCategory.builder().mainCategoryEng("Finance").build();

            //when

            //then
            Assertions.assertThrows(Exception400.class, () -> categoryService.saveLikeCategory(2L, 1L));

        }

        @Test
        @DisplayName("실패3: 중복된 mainCategory")
        void test4() {
            //given
            MainCategory mainCategory = MainCategory.builder().mainCategoryEng("Finance").build();
            BookmarkCategory bookmarkCategory = BookmarkCategory.builder().mainCategory(mainCategory).user(user).build();

            //when
            when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
            when(mainCategoryRepository.findById(anyLong())).thenReturn(Optional.ofNullable(mainCategory));
            when(bookmarkCategoryRepository.findBookmarkCategoryByMainCategory(anyLong(), anyLong())).thenReturn(bookmarkCategory);

            //then
            Assertions.assertThrows(Exception400.class, () -> categoryService.saveLikeCategory(2L, 1L));
        }

        @Test
        @DisplayName("성공2: 삭제된 카테고리")
        void test5() {
            //given
            MainCategory mainCategory = MainCategory.builder().mainCategoryEng("Finance").build();
            BookmarkCategory bookmarkCategory = BookmarkCategory.builder().mainCategory(mainCategory).user(user).build();
            bookmarkCategory.deleteBookmark();

            //when
            when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
            when(mainCategoryRepository.findById(anyLong())).thenReturn(Optional.ofNullable(mainCategory));
            when(bookmarkCategoryRepository.findBookmarkCategoryByMainCategory(anyLong(), anyLong())).thenReturn(bookmarkCategory);

            //then
            Assertions.assertDoesNotThrow(() -> categoryService.saveLikeCategory(2L, 1L));
        }
    }

    @Nested
    @DisplayName("관심 카테고리 삭제")
    class DeleteLikeCategory {
        @Test
        @DisplayName("성공")
        void test1() {
            //given
            MainCategory mainCategory = MainCategory.builder().mainCategoryEng("Finance").build();
            BookmarkCategory bookmarkCategory = BookmarkCategory.builder()
                    .user(user)
                    .mainCategory(mainCategory)
                    .build();

            //when
            when(bookmarkCategoryRepository.findById(anyLong())).thenReturn(Optional.ofNullable(bookmarkCategory));

            //then
            Assertions.assertDoesNotThrow(() -> categoryService.deleteLikeCategory(1L));
        }

        @Test
        @DisplayName("실패1: 잘못된 categoryId")
        void test2() {
            //given
            MainCategory mainCategory = MainCategory.builder().mainCategoryEng("Finance").build();
            BookmarkCategory bookmarkCategory = BookmarkCategory.builder()
                    .user(user)
                    .mainCategory(mainCategory)
                    .build();

            //when

            //then
            Assertions.assertThrows(Exception400.class, () -> categoryService.deleteLikeCategory(1L));
        }
    }

}