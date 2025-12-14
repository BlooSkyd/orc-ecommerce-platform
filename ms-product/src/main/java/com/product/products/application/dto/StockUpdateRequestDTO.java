package com.product.products.application.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StockUpdateRequestDTO {
    @NotNull(message = "Le stock ne peut être nul")
    @Min(value = 0, message = "Le stock doit être positif ou zéro")
    private Integer stockModification;
}