package com.restaurantefiap.controller;

import com.restaurantefiap.dto.request.AlterarSenhaRequestDTO;
import com.restaurantefiap.dto.request.UsuarioRequestDTO;
import com.restaurantefiap.dto.request.UsuarioUpdateDTO;
import com.restaurantefiap.dto.response.UsuarioResponseDTO;
import com.restaurantefiap.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.core.Authentication;

import java.util.List;

/**
 * Controller REST para operações de usuários.
 *
 * <p>Implementa controle de acesso baseado em roles e ownership.</p>
 * <p> Perfil ADMIN são: DONO_RESTAURANTE e MASTER (Admin do Sistema)</p>
 *
 * @author Juliana Olio
 * @author Danilo Fernando
 */

@RestController
@RequestMapping("/v1/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // ========= READ =========


    /**
     * Recupera uma lista paginada de todos os usuários ativos no sistema.
     * * <p><strong>Restrição:</strong> Operação exclusiva para usuários com perfil ADMIN.</p>
     *
     * @param pageable Objeto contendo as informações de paginação (página, tamanho, ordenação).
     * @return Um {@link Page} contendo os DTOs dos usuários encontrados.
     */
    @Operation(summary = "Lista usuários ativos paginados")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
            @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    @PreAuthorize("@autorizacaoService.isAdmin()")
    @GetMapping("/page")
    public Page<UsuarioResponseDTO> listarPaginado(Pageable pageable) {
        return usuarioService.listar(pageable);
    }


    /**
     * Busca os detalhes de um usuário específico através do seu ID.
     * * <p><strong>Regra de Autorização (Ownership/Role):</strong>
     * Permite o acesso se o usuário logado for um ADMIN OU se o ID solicitado
     * for o do próprio usuário autenticado.</p>
     *
     * @param id O identificador único do usuário.
     * @return O DTO com as informações do usuário encontrado.
     * @throws EntityNotFoundException Caso o ID não corresponda a nenhum usuário ativo.
     */
    @Operation(summary = "Busca usuário ativo por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuário encontrado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @PreAuthorize("@autorizacaoService.isAdminOuProprio(#id)")
    @GetMapping("/{id:\\d+}")
    public UsuarioResponseDTO buscarPorId(@PathVariable Long id) {
        return usuarioService.buscarPorId(id);
    }


    /**
     * Localiza um usuário ativo utilizando o endereço de e-mail como critério.
     * * <p><strong>Restrição:</strong> Por questões de privacidade, esta busca é restrita
     * a ADMIN.</p>
     *
     * @param email O e-mail do usuário a ser consultado.
     * @return O DTO do usuário correspondente ao e-mail.
     */
    @Operation(summary = "Busca usuário ativo por e-mail")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuário encontrado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @PreAuthorize("@autorizacaoService.isAdmin()")
    @GetMapping("/email/{email}")
    public UsuarioResponseDTO buscarPorEmail(@PathVariable String email) {
        return usuarioService.buscarPorEmail(email);
    }


    /**
     * Realiza uma busca por usuários ativos cujos nomes contenham o termo pesquisado.
     * * <p><strong>Restrição:</strong> Acesso permitido apenas para administradores.</p>
     *
     * @param nome Parte do nome ou nome completo para filtro.
     * @return Uma lista de usuários que atendem ao critério de busca.
     */
    @Operation(summary = "Busca usuários ativos pelo nome")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
            @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    @PreAuthorize("@autorizacaoService.isAdmin()")
    @GetMapping("/buscar")
    public List<UsuarioResponseDTO> buscarPorNome(@RequestParam String nome) {
        return usuarioService.buscarPorNome(nome);
    }

    // ========= CREATE =========

    /**
     * Registra um novo usuário no sistema.
     * <p><strong>Restrição:</strong> Operação permitida apenas para administradores.</p>
     *
     * @param dto Objeto contendo os dados necessários para a criação do usuário (nome, login, e-mail, senha, etc).
     * @return {@link ResponseEntity} contendo o DTO do usuário criado e o status 201 (Created).
     */
    @Operation(summary = "Cria novo usuário")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "409", description = "Login ou e-mail já existente")
    })
    @PreAuthorize("@autorizacaoService.isAdmin()")
    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> criar(@Valid @RequestBody UsuarioRequestDTO dto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(usuarioService.criar(dto));
    }

    // ========= UPDATE =========

    /**
     * Atualiza os dados cadastrais de um usuário existente.
     * <p><strong>Regra de Autorização:</strong> Permitido para administradores ou para o
     * próprio usuário proprietário da conta.</p>
     *
     * @param id  ID do usuário a ser atualizado.
     * @param dto Objeto contendo os novos dados do usuário.
     * @return O DTO do usuário com as informações atualizadas.
     */
    @Operation(summary = "Atualiza dados do usuário")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @PreAuthorize("@autorizacaoService.isAdminOuProprio(#id)")
    @PutMapping("/{id:\\d+}")
    public UsuarioResponseDTO atualizar(
            @PathVariable Long id,
            @RequestBody UsuarioUpdateDTO dto
    ) {
        return usuarioService.atualizar(id, dto);
    }


    /**
     * Realiza a alteração da senha de acesso.
     * <p><strong>Regra de Ownership:</strong> Por segurança, apenas o próprio usuário autenticado
     * pode realizar esta operação. Administradores não possuem permissão direta aqui.</p>
     *
     * @param id  ID do usuário que deseja alterar a senha.
     * @param dto Objeto contendo a senha atual (para validação) e a nova senha.
     * @return {@link ResponseEntity} com status 204 (No Content) em caso de sucesso.
     */
    @Operation(summary = "Altera senha do usuário")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Senha alterada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Senha atual incorreta ou nova senha inválida"),
            @ApiResponse(responseCode = "403", description = "Acesso negado - apenas o próprio usuário pode alterar sua senha"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @PreAuthorize("@autorizacaoService.isProprioUsuario(#id)")
    @PutMapping("/{id:\\d+}/senha")
    public ResponseEntity<Void> alterarSenha(
            @PathVariable Long id,
            @Valid @RequestBody AlterarSenhaRequestDTO dto
    ) {
        usuarioService.alterarSenha(id, dto);
        return ResponseEntity.noContent().build();
    }

    // ========= DELETE =========

    /**
     * Remove um usuário do sistema através de exclusão lógica (soft delete).
     * <p><strong>Restrição:</strong> Operação de alta sensibilidade, permitida apenas para
     * usuários com perfil MASTER.</p>
     *
     * @param id ID do usuário a ser removido.
     * @return {@link ResponseEntity} com status 204 (No Content).
     */
    @Operation(summary = "Remove usuário (soft delete)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Usuário removido com sucesso"),
            @ApiResponse(responseCode = "403", description = "Acesso negado - apenas MASTER pode excluir"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @PreAuthorize("@autorizacaoService.isMaster()")
    @DeleteMapping("/{id:\\d+}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        usuarioService.excluir(id);
        return ResponseEntity.noContent().build();
    }

    // ========= ME =========

    /**
     * Recupera as informações resumidas do usuário que está atualmente autenticado.
     * <p>Utiliza o contexto de segurança do Spring (JWT) para identificar o usuário.</p>
     *
     * @param authentication Objeto contendo os detalhes do usuário autenticado no contexto.
     * @return O DTO com os dados do usuário logado.
     */
    @Operation(summary = "Retorna dados do usuário logado")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Dados retornados com sucesso")
    })
    @GetMapping("/me")
    public UsuarioResponseDTO me(Authentication authentication) {
        String login = authentication.getName();
        return usuarioService.buscarPorLogin(login);
    }

}
