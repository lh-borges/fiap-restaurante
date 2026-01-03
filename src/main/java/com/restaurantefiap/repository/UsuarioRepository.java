package com.restaurantefiap.repository;

import com.restaurantefiap.entities.usuario.Usuario;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import java.util.List;
import java.util.Optional;

/**
 * Repository para operações de persistência da entidade {@link Usuario}.
 *
 * @author Thiago de Jesus
 * @author Juliana Olio
 * @author Danilo Fernando
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // ========== Busca por Login (Autenticação) ==========

    /**
     * Busca usuário ativo pelo login (case insensitive).
     * Usado para: Login (auth)
     *
     * <p>Usado na autenticação — ignora usuários deletados.</p>
     *
     * @param login identificador de login
     * @return usuário encontrado ou empty
     */
    /**
     * Busca usuário ativo pelo login (auth).
     */
    @Query("SELECT u FROM Usuario u WHERE LOWER(u.login) = LOWER(:login) AND u.deletadoEm IS NULL")
    Optional<Usuario> findByLoginIgnoreCase(@Param("login") String login);

    /**
     * Verifica se existe usuário com o login informado.
     * Usado para: Login (cadastro)
     *
     * @param login identificador de login
     * @return true se existe
     */
    boolean existsByLoginIgnoreCase(String login);

    // ========== Busca por Email ==========

    /**
     * Busca usuário pelo email (case insensitive).
     * Usado para: Email (auth/uso do sistema)
     *
     * @param email email do usuário
     * @return usuário encontrado ou empty
     */
    @Query("SELECT u FROM Usuario u WHERE LOWER(u.email) = LOWER(:email) AND u.deletadoEm IS NULL")
    Optional<Usuario> findByEmailIgnoreCase(@Param("email") String email);

    /**
     * Verifica se existe usuário com o email informado.
     * Usado para: Email (cadastro)
     *
     * @param email email do usuário
     * @return true se existe
     */
    boolean existsByEmailIgnoreCase( String email);

    // ========== Buscas por Nome ==========

    /**
     * Busca usuários ativos pelo nome (busca parcial, case insensitive).
     *
     * @param nome termo de busca
     * @return lista de usuários encontrados
     */
    @Query("SELECT u FROM Usuario u WHERE LOWER(u.nome) LIKE LOWER(CONCAT('%', :nome, '%')) AND u.deletadoEm IS NULL")
    List<Usuario> findByNomeContainingIgnoreCase(String nome);

    // ========== Buscas todos usuarios ==========

    /**
     * Busca todos os usuários ativos (não deletados).
     *
     * @return lista de usuários ativos
     */
    @Query("SELECT u FROM Usuario u WHERE u.deletadoEm IS NULL")
    List<Usuario> findAllAtivos();


    /**
     * Busca todos os usuários ativos com paginação.
     *
     * @param pageable configuração de paginação
     * @return página de usuários ativos
     */
    @Query("SELECT u FROM Usuario u WHERE u.deletadoEm IS NULL")
    Page<Usuario> findAllAtivos(Pageable pageable);

    /**
     * Busca usuário ativo por ID.
     *
     * @param id identificador do usuário
     * @return usuário encontrado ou empty
     */
    @Query("SELECT u FROM Usuario u WHERE u.id = :id AND u.deletadoEm IS NULL")
    Optional<Usuario> findAtivoById(@Param("id") Long id);
}