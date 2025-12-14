package com.order.orders.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderUpdateRequestDTO {

    @NotBlank(message = "L'adresse de livraison ne peut pas être vide")
    @Size(min = 10, max = 500, message = "L'adresse de livraison doit contenir entre 10 et 500 caractères")
    private String shippingAddress;
}
