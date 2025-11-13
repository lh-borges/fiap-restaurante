package com.restaurantefiap.controller;


import com.restaurantefiap.entities.Usuario;
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

    @GetMapping()
    public List<Usuario> findAll() {
        return usuarioService.listAll();
    }

    @GetMapping("/page")
    public Page<Usuario> findAll(Pageable pageable) {
        return usuarioService.list(pageable);
    }

    @GetMapping("/{id}")
    public Usuario findById(@PathVariable Long id) {
        return usuarioService.getById(id);
    }

    @GetMapping("/email/{email}")
    public Usuario findByEmail(@PathVariable String email) {
        return usuarioService.getByEmail(email);
    }

    @PostMapping
    public Usuario save(@RequestBody Usuario usuario) {
        return usuarioService.create(usuario);
    }

    @PutMapping
    public Usuario update(@RequestBody Usuario usuario) {
        return usuarioService.update(usuario.getId(), usuario);
    }

    @PutMapping("/{id}/senha/{senha}")
    public void changePassword(@PathVariable Long id, @PathVariable String senha) {
        usuarioService.changePassword(id, senha);
    }


    @DeleteMapping
    public void delete(@PathVariable Long id) {
        usuarioService.delete(id);
    }
}
