package com.order.orders.application.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRequestDTO {

    @NotNull(message = "L'identifiant de l'utilisateur ne peut pas être null")
    private Long userId;

    @NotBlank(message = "L'adresse de livraison ne peut pas être vide")
    @Size(min = 10, max = 500, message = "L'adresse de livraison doit contenir entre 10 et 500 caractères")
    private String shippingAddress;

    @NotEmpty(message = "La commande doit contenir au moins un produit")
    @Valid
    private List<OrderItemRequestDTO> items;

}
