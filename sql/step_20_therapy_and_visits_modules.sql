drop table if exists therapies;
create table  therapies (
  thr_id int(11) not null auto_increment,
  thr_code int(11) not null,
  thr_pat_id int(11) not null,
  thr_startdate datetime not null,
  thr_enddate datetime not null,
  thr_mdsr_id int(11) not null,
  thr_qty double not null,
  thr_unt_id int(11) not null,
  thr_freqinday int(11) not null,
  thr_freqinprd int(11) not null,
  thr_note text,
  thr_notify tinyint(1) not null default '0',
  thr_sms tinyint(1) not null default '0',
  primary key (thr_id)
) engine=MyISAM;

drop table if exists visits;
create table visits (
  vst_id int(11) not null auto_increment,
  vst_pat_id int(11) not null,
  vst_date datetime not null,
  vst_note text,
  primary key (vst_id)
) engine=MyISAM;

insert into menuitem values ('btnadmtherapy','angal.admission.therapy','angal.admission.therapy','x','T','admission','none','N',6);

insert into groupmenu (gm_ug_id_a, gm_mni_id_a,  gm_active) values ('admin','btnadmtherapy','Y');

