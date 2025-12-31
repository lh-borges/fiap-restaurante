package com.restaurantefiap.dto.request;

public record AuthRequest(
      String email,
      String password
) {
}
