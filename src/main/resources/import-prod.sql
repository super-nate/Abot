/*
drop table if exists accounts;

CREATE TABLE accounts (
    id int NOT NULL AUTO_INCREMENT,
    friendly_id varchar(255) NOT NULL UNIQUE ,
    accountId varchar(255) NOT NULL,
    email varchar(255) NOT NULL,
    PRIMARY KEY (id)
);

insert into accounts ( friendly_id,accountId, email) values ('supermanheng21', 'GAIGZHHWK3REZQPLQX5DNUN4A32CSEONTU6CMDBO7GDWLPSXZDSYA4BU', 'supermanheng21@gmail.com');*/