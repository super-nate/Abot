drop table if exists binding;

CREATE TABLE binding (
    id int NOT NULL AUTO_INCREMENT,
    im_id varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL ,
    account_id varchar(255) NOT NULL,
    is_enabled tinyint(1) unsigned NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT UC_Person UNIQUE (im_id,account_id)
);

insert into binding ( im_id,account_id, is_enabled) values ('qq_82596074', 'GAYVP62K6DFSNLOM27JO5VNJVCC5E3NZ6DFGONW3DAATNVHDHLI2BZQP', 1);
