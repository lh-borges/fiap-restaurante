CREATE TABLE IF NOT EXISTS usuarios (

    id              BIGINT          NOT NULL AUTO_INCREMENT PRIMARY KEY,
    login           VARCHAR(100)    NOT NULL UNIQUE,
    email           VARCHAR(255)    NOT NULL UNIQUE,
    nome            VARCHAR(255)    NOT NULL,
    telefone        VARCHAR(20)     NOT NULL,
    role            VARCHAR(30)     NOT NULL,
    password        VARCHAR(255)    NOT NULL,

    -- Endereço (Embedded)
    logradouro      VARCHAR(255),
    numero          VARCHAR(20),
    complemento     VARCHAR(100),
    bairro          VARCHAR(100),
    cep             VARCHAR(8),
    cidade          VARCHAR(100),
    uf              VARCHAR(2),

    -- Auditoria
    criado_em       DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deletado_em     DATETIME        NULL DEFAULT NULL
);

-- Índice para queries filtrando ativos (WHERE deletado_em IS NULL)
CREATE INDEX idx_usuarios_deletado_em ON usuarios(deletado_em);