package com.triplea.triplea.core.dummy;

import com.triplea.triplea.service.CategoryService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
public class DataInit extends DummyEntity {

    @Profile("dev")
    @Bean
    CommandLineRunner init(CategoryService categoryService) {
        return args -> {
            categoryService.insertMainCategories();
            categoryService.updateMainCategories();
            categoryService.insertSubCategories();
        };
    }
}
