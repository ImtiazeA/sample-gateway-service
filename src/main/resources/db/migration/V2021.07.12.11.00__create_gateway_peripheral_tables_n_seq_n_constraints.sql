CREATE TABLE gateways
(
    id            varchar(255) NOT NULL,
    created_at    datetime(6)  NOT NULL,
    updated_at    datetime(6)  NOT NULL,
    ip_v4_address varchar(255),
    name          varchar(255),
    PRIMARY KEY (id)
)
    ENGINE = InnoDB;

CREATE TABLE peripheral_id_sequence
(
    next_val bigint
)
    ENGINE = InnoDB;

INSERT INTO peripheral_id_sequence
VALUES (1);

CREATE TABLE peripherals
(
    id         bigint       NOT NULL,
    created_at datetime(6)  NOT NULL,
    updated_at datetime(6)  NOT NULL,
    gateway_id varchar(255) NOT NULL,
    name       varchar(255),
    status     varchar(255),
    vendor     varchar(255),
    PRIMARY KEY (id),
    CONSTRAINT fk__peripheral__gateways_id FOREIGN KEY (gateway_id) REFERENCES gateways (id)
)
    ENGINE = InnoDB;