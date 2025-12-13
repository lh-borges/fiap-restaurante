package com.restaurantefiap.controller;

import com.restaurantefiap.dto.request.UsuarioRequestDTO;
import com.restaurantefiap.dto.request.UsuarioUpdateDTO;
import com.restaurantefiap.dto.response.UsuarioResponseDTO;
import com.restaurantefiap.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Operation(summary = "Lista todos os usuários", description = "Retorna uma lista completa de usuários cadastrados")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    @GetMapping()
    public List<UsuarioResponseDTO> findAll() {
        return usuarioService.listAll();
    }

    @Operation(summary = "Lista de usuários paginados", description = "Retorna usuários em formato de página")
    @GetMapping("/page")
    public Page<UsuarioResponseDTO> findAll(Pageable pageable) {
        return usuarioService.list(pageable);
    }

    @Operation(summary = "Busca usuário por ID", description = "Retorna um usuário específico pelo seu identificador")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuário encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @GetMapping("/{id}")
    public UsuarioResponseDTO findById(@PathVariable Long id) {
        return usuarioService.getByIdDTO(id);
    }

    @Operation(summary = "Busca usuário por e-mail", description = "Retorna um usuário específico pelo e-mail")
    @GetMapping("/email/{email}")
    public UsuarioResponseDTO findByEmail(@PathVariable String email) {
        return usuarioService.getByEmailDTO(email);
    }

    @Operation(summary = "Busca usuários pelo nome", description = "Retorna usuários cujo nome contém o texto informado")
    @GetMapping("/buscar")
    public List<UsuarioResponseDTO> findByNome(@RequestParam String nome) {
        return usuarioService.findByNomeContaining(nome);
    }

    @Operation(summary = "Cria novo usuário", description = "Cadastra um novo usuário no sistema")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> save(@Valid @RequestBody UsuarioRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.create(dto));
    }

    // ========= UPDATE =========
    @Operation(summary = "Atualiza dados do usuário", description = "Atualiza informações de perfil de um usuário existente")
    @PutMapping("/{id}")
    public UsuarioResponseDTO update(@PathVariable Long id, @RequestBody UsuarioUpdateDTO dto) {
        return usuarioService.update(id, dto);
    }

    // ========= CHANGE PASSWORD =========
    @Operation(summary = "Altera senha do usuário", description = "Atualiza a senha de um usuário específico")
    @PutMapping("/{id}/senha")
    public ResponseEntity<Void> changePassword(@PathVariable Long id, @RequestBody String novaSenha) {
        usuarioService.changePassword(id, novaSenha);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Remove usuário", description = "Exclui um usuário pelo seu ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Usuário removido com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        usuarioService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
