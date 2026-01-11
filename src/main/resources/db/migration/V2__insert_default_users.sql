-- V2__insert_default_users.sql
-- Seeds default para ambiente de desenvolvimento/avaliação.
-- Idempotente: não duplica se rodar novamente.
-- Regras:
-- - Não insere se já existir usuário com o mesmo email OU login.
-- Senha padrão: senhaParaTeste@2025

INSERT INTO usuarios (login, email, nome, telefone, role, password)
SELECT seed.login, seed.email, seed.nome, seed.telefone, seed.role, seed.password
FROM (
         SELECT
             'ti' AS login,
             'ti@restaurantefiap.com' AS email,
             'TI Admin' AS nome,
             '11999999999' AS telefone,
             'MASTER' AS role,
             '$2a$12$2twpPEI7vlXS0ZvolDHoOOSGaGh5U6QqQ0ltKH5IrAYT10l8Ws5sG' AS password
         UNION ALL
         SELECT
             'master.admin',
             'admin@restaurantefiap.com',
             'Master Admin',
             '11999999999',
             'MASTER',
             '$2a$12$2twpPEI7vlXS0ZvolDHoOOSGaGh5U6QqQ0ltKH5IrAYT10l8Ws5sG'
         UNION ALL
         SELECT
             'danilo.silva',
             'danilo@restaurantefiap.com',
             'Danilo Silva',
             '62999999999',
             'MASTER',
             '$2a$12$2twpPEI7vlXS0ZvolDHoOOSGaGh5U6QqQ0ltKH5IrAYT10l8Ws5sG'
         UNION ALL
         SELECT
             'gilmar.moraes',
             'gilmar@restaurantefiap.com',
             'Gilmar Moraes',
             '48988887777',
             'DONO_RESTAURANTE',
             '$2a$12$2twpPEI7vlXS0ZvolDHoOOSGaGh5U6QqQ0ltKH5IrAYT10l8Ws5sG'
         UNION ALL
         SELECT
             'ju.olio',
             'ju.olio@restaurantefiap.com',
             'Ju Olio',
             '21977776666',
             'DONO_RESTAURANTE',
             '$2a$12$2twpPEI7vlXS0ZvolDHoOOSGaGh5U6QqQ0ltKH5IrAYT10l8Ws5sG'
         UNION ALL
         SELECT
             'thiago.jesus',
             'thiago.jesus@restaurantefiap.com',
             'Thiago de Jesus',
             '31966665555',
             'CLIENTE',
             '$2a$12$2twpPEI7vlXS0ZvolDHoOOSGaGh5U6QqQ0ltKH5IrAYT10l8Ws5sG'
         UNION ALL
         SELECT
             'luiz.borges',
             'luiz.borges@restaurantefiap.com',
             'Luiz Borges',
             '31966665555',
             'CLIENTE',
             '$2a$12$2twpPEI7vlXS0ZvolDHoOOSGaGh5U6QqQ0ltKH5IrAYT10l8Ws5sG'
         UNION ALL
         SELECT
             'otavio.fernando',
             'otavio.fernando@restaurantefiap.com',
             'Otavio Fernando',
             '31966665555',
             'CLIENTE',
             '$2a$12$2twpPEI7vlXS0ZvolDHoOOSGaGh5U6QqQ0ltKH5IrAYT10l8Ws5sG'
     ) AS seed
WHERE NOT EXISTS (
    SELECT 1
    FROM usuarios u
    WHERE u.login = seed.login
   OR u.email = seed.email
    );
