drop table if exists qq_binding;

CREATE TABLE qq_binding (
    id int NOT NULL AUTO_INCREMENT,
    qq_id varchar(255) NOT NULL ,
    account_id varchar(255) NOT NULL,
    is_enabled tinyint(1) unsigned NOT NULL,
    PRIMARY KEY (id)
);

insert into qq_binding ( qqId,accountId, is_abled) values ('82596074', 'GAYVP62K6DFSNLOM27JO5VNJVCC5E3NZ6DFGONW3DAATNVHDHLI2BZQP', 1);
