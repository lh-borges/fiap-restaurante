package com.restaurantefiap.controller;

import com.restaurantefiap.entities.Usuario;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import com.restaurantefiap.service.UsuarioService;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Operation(summary = "Lista todos os usuários", description = "Retorna uma lista completa de usuários cadastrados")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    @GetMapping()
    public List<Usuario> findAll() {
        return usuarioService.listAll();
    }

    @Operation(summary = "Lista de usuários paginados", description = "Retorna usuários em formato de página")
    @GetMapping("/page")
    public Page<Usuario> findAll(Pageable pageable) {
        return usuarioService.list(pageable);
    }

    @Operation(summary = "Busca usuário por ID", description = "Retorna um usuário específico pelo seu identificador")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuário encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @GetMapping("/{id}")
    public Usuario findById(@PathVariable Long id) {
        return usuarioService.getById(id);
    }

    @Operation(summary = "Busca usuário por e-mail", description = "Retorna um usuário específico pelo e-mail")
    @GetMapping("/email/{email}")
    public Usuario findByEmail(@PathVariable String email) {
        return usuarioService.getByEmail(email);
    }

    @Operation(summary = "Busca usuários pelo nome", description = "Retorna usuários cujo nome contém o texto informado")
    @GetMapping("/buscar")
    public List<Usuario> findByNome(@RequestParam String nome) {
        return usuarioService.findByNomeContaining(nome);
    }

    @Operation(summary = "Cria novo usuário", description = "Cadastra um novo usuário no sistema")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @PostMapping
    public Usuario save(@RequestBody Usuario usuario) {
        return usuarioService.create(usuario);
    }

    @Operation(summary = "Atualiza dados do usuário", description = "Atualiza informações de perfil de um usuário existente")
    @PutMapping
    public Usuario update(@RequestBody Usuario usuario) {
        return usuarioService.update(usuario.getId(), usuario);
    }

    @Operation(summary = "Altera senha do usuário", description = "Atualiza a senha de um usuário específico")
    @PutMapping("/{id}/senha/{senha}")
    public void changePassword(@PathVariable Long id, @PathVariable String senha) {
        usuarioService.changePassword(id, senha);
    }

    @Operation(summary = "Remove usuário", description = "Exclui um usuário pelo seu ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Usuário removido com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        usuarioService.delete(id);
    }
}
