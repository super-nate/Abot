drop table if exists binding;

CREATE TABLE binding (
    id int NOT NULL AUTO_INCREMENT,
    im_id varchar(255) NOT NULL ,
    account_id varchar(255) NOT NULL,
    is_enabled tinyint(1) unsigned NOT NULL,
    PRIMARY KEY (id)
);

insert into binding ( im_id,account_id, is_enabled) values ('qq_82596074', 'GAYVP62K6DFSNLOM27JO5VNJVCC5E3NZ6DFGONW3DAATNVHDHLI2BZQP', 1);
