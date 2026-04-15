alter table user change column us_passwd us_passwd varchar(60) not null default '' ;
update user set us_passwd = "$2a$10$FI/PMO0oSHHosF2PX8l3QuB0DJepVfnynbLZ9Zm2711bF2ch8db2S" where us_id_a like "admin";
update user set us_passwd = "$2a$10$b0WlANdaNV7Ukn/klFGt3.euZ7PaHuJI6TtBSM2vdxkavvkUDbpo2" where us_id_a like "guest";
