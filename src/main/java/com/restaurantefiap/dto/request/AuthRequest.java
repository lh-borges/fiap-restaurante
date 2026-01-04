package com.restaurantefiap.dto.request;

public record AuthRequest(
      String login,
      String password
) {
}
