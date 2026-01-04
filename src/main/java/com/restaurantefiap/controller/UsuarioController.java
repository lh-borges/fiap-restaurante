package com.restaurantefiap.controller;

import com.restaurantefiap.dto.request.UsuarioRequestDTO;
import com.restaurantefiap.dto.request.UsuarioUpdateDTO;
import com.restaurantefiap.dto.response.UsuarioResponseDTO;
import com.restaurantefiap.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // ========= READ =========

    @Operation(summary = "Lista usuários ativos paginados")
    @GetMapping("/page")
    public Page<UsuarioResponseDTO> findAll(Pageable pageable) {
        return usuarioService.listar(pageable);
    }

    @Operation(summary = "Busca usuário ativo por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuário encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @GetMapping("/{id}")
    public UsuarioResponseDTO findById(@PathVariable Long id) {
        return usuarioService.buscarPorId(id);
    }

    @Operation(summary = "Busca usuário ativo por e-mail")
    @GetMapping("/email/{email}")
    public UsuarioResponseDTO findByEmail(@PathVariable String email) {
        return usuarioService.buscarPorEmail(email);
    }

    @Operation(summary = "Busca usuários ativos pelo nome")
    @GetMapping("/buscar")
    public List<UsuarioResponseDTO> findByNome(@RequestParam String nome) {
        return usuarioService.buscarPorNome(nome);
    }

    // ========= CREATE =========

    @Operation(summary = "Cria novo usuário")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> save(
            @Valid @RequestBody UsuarioRequestDTO dto
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(usuarioService.criar(dto));
    }

    // ========= UPDATE =========

    @Operation(summary = "Atualiza dados do usuário")
    @PutMapping("/{id}")
    public UsuarioResponseDTO update(
            @PathVariable Long id,
            @RequestBody UsuarioUpdateDTO dto
    ) {
        return usuarioService.atualizar(id, dto);
    }

    @Operation(summary = "Altera senha do usuário")
    @PutMapping("/{id}/senha")
    public ResponseEntity<Void> changePassword(
            @PathVariable Long id,
            @RequestBody String novaSenha
    ) {
        usuarioService.alterarSenha(id, novaSenha);
        return ResponseEntity.noContent().build();
    }

    // ========= DELETE =========

    @Operation(summary = "Remove usuário (soft delete)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Usuário removido com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        usuarioService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
