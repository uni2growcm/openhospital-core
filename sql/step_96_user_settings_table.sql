create table oh_user_settings(
 uss_id int(11) not null auto_increment, 
 uss_us_id_a varchar(50) not null, 
 uss_config_name varchar(50) not null,  
 uss_config_value text not null,
 primary key (uss_id),
 foreign key (uss_us_id_a) references oh_user(us_id_a) on delete cascade,
 unique (uss_us_id_a)
) engine = innodb default character set utf8;
