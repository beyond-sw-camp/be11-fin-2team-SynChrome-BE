package com.Synchrome.workspace.calendar.dto;

import com.Synchrome.workspace.calendar.domain.ColorCategory;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ColorCategoryDto {
    private Long id;
    private String name;
    private String color;

    public static ColorCategoryDto from(ColorCategory colorCategory) {
        if(colorCategory == null) return null;

        return ColorCategoryDto.builder()
                .id(colorCategory.getId())
                .name(colorCategory.getName())
                .color(colorCategory.getColor())
                .build();
    }
}
