package com.product.products.application.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StockUpdateRequestDTO {
    @NotNull(message = "Le stock ne peut être nul")
    private int stockModification;
}