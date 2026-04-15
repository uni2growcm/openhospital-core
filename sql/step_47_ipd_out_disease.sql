alter table disease add column dis_ipd_out_include int(11) not null default '0' after dis_ipd_in_include, change column dis_ipd_include dis_ipd_in_include int(11) not null default '0';

update disease set dis_ipd_out_include = 1 where dis_ipd_in_include = 1;
