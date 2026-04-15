alter table opd add column opd_referral_from varchar(1) default null after opd_dis_id_a_3;
alter table opd add column opd_referral_to varchar(1) default null after opd_referral_from;
alter table opd add column opd_pat_id int(11) default null after opd_referral_to;
alter table opd add column opd_pat_fname varchar(50) default null after opd_pat_id;
alter table opd add column opd_pat_sname varchar(50) default null after opd_pat_fname;
alter table opd add column opd_pat_next_kin varchar(50) default null after opd_pat_sname;
alter table opd add column opd_pat_addr varchar(50) default null after opd_pat_next_kin;
alter table opd add column opd_pat_city varchar(50) default null after opd_pat_addr;