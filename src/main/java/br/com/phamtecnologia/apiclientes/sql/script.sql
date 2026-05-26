CREATE TABLE clientes(
    id				 SERIAL			PRIMARY KEY,
    nome 			 VARCHAR(150)	NOT NULL,
    email			 VARCHAR(100)	NOT NULL,
    telefone		 VARCHAR(20)	NOT NULL,
    tipo			 VARCHAR(20)	NOT NULL,
    status			 VARCHAR(20)	NOT NULL,
    datahoracadastro TIMESTAMP		DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT ck_tipo
         CHECK(tipo IN('PESSOA_FISICA', 'PESSOA_JURIDICA')),
    CONSTRAINT ck_status
         CHECK(status IN('ATIVO', 'INATIVO', 'BLOQUEADO'))
);