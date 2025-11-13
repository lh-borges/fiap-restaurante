package com.restaurantefiap.started;

import com.restaurantefiap.entities.Usuario;
import com.restaurantefiap.enums.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import com.restaurantefiap.repository.UsuarioRepository;

@Component
public class LoaderUser implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public void run(String... args) throws Exception {

        if (usuarioRepository.count() == 0) {

            Usuario admin = new Usuario();
            admin.setNome("Administrador");
            admin.setEmail("admin@email.com");
            admin.setTelefone("12345678901");
            admin.setPassword("$2a$12$2twpPEI7vlXS0ZvolDHoOOSGaGh5U6QqQ0ltKH5IrAYT10l8Ws5sG");
            admin.setRole(Role.ROLE_ADMIN);

            Usuario user = new Usuario();
            user.setNome("Usuário");
            user.setEmail("user@email.com");
            user.setTelefone("12345678901");
            user.setPassword("$2a$12$2twpPEI7vlXS0ZvolDHoOOSGaGh5U6QqQ0ltKH5IrAYT10l8Ws5sG");
            user.setRole(Role.ROLE_USER);

            Usuario adminMaster = new Usuario();
            adminMaster.setNome("adminMaster");
            adminMaster.setEmail("adminMaster@email.com");
            adminMaster.setTelefone("12345678901");
            adminMaster.setPassword("$2a$12$2twpPEI7vlXS0ZvolDHoOOSGaGh5U6QqQ0ltKH5IrAYT10l8Ws5sG");
            adminMaster.setRole(Role.ROLE_MASTER);

            usuarioRepository.save(admin);
            usuarioRepository.save(user);
            usuarioRepository.save(adminMaster);

            System.out.println(" Seed criado ");
            } else {
            System.out.println(" Usuários já existem, seed ignorado.");
        }

    }
}
