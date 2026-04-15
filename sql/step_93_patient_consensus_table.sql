create table oh_patient_consensus (
  ptc_id int(11) unsigned not null auto_increment,
  ptc_pat_id int(11) not null,
  ptc_consensus tinyint(1) not null default 0,
  ptc_service  tinyint(1) not null default 0,
  ptc_created_by varchar(50) null default null,
  ptc_created_date datetime null default null,
  ptc_last_modified_by varchar(50) null default null,
  ptc_last_modified_date datetime null default null,
  ptc_active tinyint(1) not null default 1,
  primary key (ptc_id),
  foreign key (ptc_pat_id) references oh_patient(pat_id),
  unique (ptc_pat_id)
) engine = innodb default character set utf8;

-- update previous data
insert into oh_patient_consensus (ptc_pat_id, ptc_consensus, ptc_service, ptc_created_by, ptc_created_date, ptc_last_modified_by, ptc_last_modified_date)
select pat_id, 1, 0, 'admin', now(), 'admin', now() from oh_patient;
