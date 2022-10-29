package io.davidabejirin.bvnvalidationassessment.service.impl;

import io.davidabejirin.bvnvalidationassessment.payload.BVNInfoDTO;
import io.davidabejirin.bvnvalidationassessment.service.BVNCache;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Service
public class BVNCacheImpl implements BVNCache {

    public static final String DEFAULT_IMAGE_DETAILS = "/9j/4AAQSkZJRgABAQEBLAEsAAD/4QBkRXhpZgAASUkqAAgAAAACAA4BAgAtAAAAJgAAAJiCAgAJAAAAUwAAAAAAAABPcmFuZGEgZ29sZCBmaXNoIGlzb2xhdGVkIG9uIGJsYWNrIGJhY2tncm91bmRwcmFpc2Flbmf/4QViaHR0cDovL25zLmFkb2JlLmNvbS94YXAvMS4wLwA8P3hwYWNrZXQgYmVnaW49Iu+7vyIgaWQ9Ilc1TTBNcENlaGlIenJlU3pOVGN6a2M5ZCI/Pgo8eDp4bXBtZXRhIHhtbG5zOng9ImFkb2JlOm5zOm1ldGEvIj4KCTxyZGY6UkRGIHhtbG5zOnJkZj0iaHR0cDovL3d3dy53My5vcmcvMTk5OS8wMi8yMi1yZGYtc3ludGF4LW5zIyI+CgkJPHJkZjpEZXNjcmlwdGlvbiByZGY6YWJvdXQ9IiIgeG1sbnM6cGhvdG9zaG9wPSJodHRwOi8vbnMuYWRvYmUuY29tL3Bob3Rvc2hvcC8xLjAvIiB4bWxuczpJcHRjNHhtcENvcmU9Imh0dHA6Ly9pcHRjLm9yZy9zdGQvSXB0YzR4bXBDb3JlLzEuMC94bWxucy8iICAgeG1sbnM6R2V0dHlJbWFnZXNHSUZUPSJodHRwOi8veG1wLmdldHR5aW1hZ2VzLmNvbS9naWZ0LzEuMC8iIHhtbG5zOmRjPSJodHRwOi8vcHVybC5vcmcvZGMvZWxlbWVudHMvMS4xLyIgeG1sbnM6cGx1cz0iaHR0cDovL25zLnVzZXBsdXMub3JnL2xkZi94bXAvMS4wLyIgIHhtbG5zOmlwdGNFeHQ9Imh0dHA6Ly9pcHRjLm9yZy9zdGQvSXB0YzR4bXBFeHQvMjAwOC0wMi0yOS8iIHhtbG5zOnhtcFJpZ2h0cz0iaHR0cDovL25zLmFkb2JlLmNvbS94YXAvMS4wL3JpZ2h0cy8iIGRjOlJpZ2h0cz0icHJhaXNhZW5nIiBwaG90b3Nob3A6Q3JlZGl0PSJHZXR0eSBJbWFnZXMvaVN0b2NrcGhvdG8iIEdldHR5SW1hZ2VzR0lGVDpBc3NldElEPSI1ODQ4NDUwMzAiIHhtcFJpZ2h0czpXZWJTdGF0ZW1lbnQ9Imh0dHBzOi8vd3d3LmlzdG9ja3Bob3RvLmNvbS9sZWdhbC9saWNlbnNl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;";
    public static final String DEFAULT_BASIC_DETAILS = "Zmlyc3QgbmFtZTogRGF2aWQgCmxhc3QgbmFtZTogQWJlamlyaW4KYWRkcmVzczogQWphaA==";
    public static final List<BVNInfoDTO> BVN_DETAILS = List.of(
            buildBvnDetails("11111111111"),
            buildBvnDetails("22222222222"),
            buildBvnDetails("33333333333"),
            buildBvnDetails("44444444444"),
            buildBvnDetails("55555555555"),
            buildBvnDetails("66666666666"),
            buildBvnDetails("77777777777"),
            buildBvnDetails("88888888888"),
            buildBvnDetails("99999999999")
    );
    private static BVNInfoDTO buildBvnDetails(String bvn) {
        return BVNInfoDTO.builder()
                .bvn(bvn)
                .imageDetail(getDefaultBase64Image())
                .basicDetail(getDefaultBasicDetails())
                .build();
    }

    private static String getDefaultBase64Image() {
        return DEFAULT_IMAGE_DETAILS;
    }
    private static String getDefaultBasicDetails(){
        return DEFAULT_BASIC_DETAILS;
    }

    @Override
    public Optional<BVNInfoDTO> findByBvn(String bvn) {
        return BVN_DETAILS.stream()
                .filter(bvnDetail -> bvnDetail.getBvn().equals(bvn))
                .findFirst();
    }
}