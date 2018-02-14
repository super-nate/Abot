drop table if exists accounts;

CREATE TABLE qq_binding (
    id int NOT NULL AUTO_INCREMENT,
    qqId varchar(255) NOT NULL UNIQUE ,
    accountId varchar(255) NOT NULL,
    is_abled tinyint(1) unsigned NOT NULL,
    PRIMARY KEY (id)
);

insert into qq_binding ( qqId,accountId, is_abled) values ('82596074', 'GAYVP62K6DFSNLOM27JO5VNJVCC5E3NZ6DFGONW3DAATNVHDHLI2BZQP', 1);
