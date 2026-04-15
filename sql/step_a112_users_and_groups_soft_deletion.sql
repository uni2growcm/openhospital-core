alter table oh_user
add us_deleted boolean default false;

alter table oh_usergroup
add ug_deleted boolean default false;