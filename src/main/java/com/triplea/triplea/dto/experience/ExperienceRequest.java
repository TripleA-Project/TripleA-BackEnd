package com.triplea.triplea.dto.experience;

import lombok.Getter;

import javax.validation.constraints.NotBlank;
import java.util.Date;

public class ExperienceRequest {
    @Getter
    public static class Insert {
        @NotBlank
        private Long id;
        @NotBlank
        private Date freeTierStartDate;
        @NotBlank
        private Date freeTierEndDate;
        private String memo;
    }

    @Getter
    public static class Update {
        @NotBlank
        private Long id;
        @NotBlank
        private Date freeTierStartDate;
        @NotBlank
        private Date freeTierEndDate;
        private String memo;
    }

    @Getter
    public static class Search {
        private String type;
        private String content;
        private Boolean freeTier;
    }
}
