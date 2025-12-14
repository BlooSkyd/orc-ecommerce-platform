package com.order.orders.application.dto;

import com.order.orders.infrastructure.validation.ValidStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderStatusRequestDTO {

    @NotBlank(message = "La catégorie ne peut pas être vide")
    @ValidStatus
    private String status;
}
