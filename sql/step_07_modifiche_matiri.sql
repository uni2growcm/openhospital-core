alter table medicaldsr add column mdsr_code varchar(5) not null after mdsr_mdsrt_id_a;
alter table medicaldsr add column mdsr_pcs_x_pck integer not null after mdsr_ini_stock_qti;

alter table patient add column pat_moth_name varchar(50) not null default '' after pat_tele;
alter table patient add column pat_fath_name varchar(50) not null default '' after pat_moth;
alter table patient add column pat_btype varchar(7) not null default 'Unknown';
alter table patient add column pat_bdate varchar(50) not null default '' after pat_name;
alter table patient modify column pat_age varchar(50) not null;

alter table opd add column opd_pat_fullname varchar(50) not null default '' after opd_pat_id;
alter table opd modify column opd_age varchar(50) not null;
alter table opd add column opd_note text not null after opd_referral_to;

update menuitem set mni_class='org.isf.menu.gui.UserGroupBrowsing' where mni_id_a='groups';