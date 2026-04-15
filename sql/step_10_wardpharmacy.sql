--
-- Definition of table medicalsrstockmov_n
--

drop table if exists medicalsrstockmov_n;
create table  medicalsrstockmov_n (
  mmvn_id int(10) not null auto_increment,
  mmvn_wrd_id_a char(1) not null,
  mmvn_date datetime not null,
  mmvn_pat_id varchar(100) character set latin1 not null,
  mmvn_mdsr_id varchar(100) character set latin1 not null,
  mmvn_mdsr_qty int(10) not null,
  mmvn_mdsr_units varchar(10) not null,
  primary key  using btree (mmvn_id)
) engine=MyISAM;

insert into menuitem values ('medicalsward', 'angal.menu.btn.medicalsward', 'angal.menu.medicalsward', 'x', 'W', 'pharmacy', 'org.isf.medicalstockward.gui.WardPharmacy','N', 2);

-- Functionality initially disabled. Put 'Y' to activate it

insert into groupmenu (gm_ug_id_a, gm_mni_id_a, gm_active) values ('admin','medicalsward','Y');
