-- version 7-4-2006
-- version 27-8-2006
-- version 29-8-2006  exam,laboratory
-- version 17-9-2006  admission, menu , menuitem
-- version 17-11-2006 opd (new) surgery (deleted), disease, exam, laboratory modified
-- 	disease	(added dis_opd_include, dis_ipd_include)
-- 	exam	(exa_desc from 50 to 100)
-- 	laboratory (added lab_age, lab_sex, lab_material, lab_exam_date, lab_pat_inout)
-- version 17-6-2008
-- 	opd (added opd_referral_from, opd_referral_to, opd_pat_id, opd_pat_fname, opd_pat_sname, opd_pat_next_kin, opd_pat_addr, opd_pat_city)
-- version 10-12-2012
-- 	important mysql setTINGS:
--   if you are using innodb tables, you should set this variable to 1 on all platforms 
--   to force names to be converted to lowercase.
--   lower_case_table_names = 1   

--
-- tables creation
--
create table admission  (
	adm_id int not null auto_increment,
	adm_in int not null default '0',
	adm_type char(1) not null default 'N',
	adm_wrd_id_a char(1) not null default '',
	adm_yprog int not null default '0',
	adm_pat_id int not null default '0',
	adm_date_adm datetime not null,
	adm_admt_id_a_adm varchar(10) not null default '',
	adm_fhu varchar(50) default null,
	adm_in_dis_id_a varchar(10) default null,
	adm_out_dis_id_a varchar(10) default null,
	adm_ope_id_a varchar(10) default null,
	adm_date_op datetime null ,			
	adm_resop varchar(10) default null,
	adm_date_dis datetime default null,
	adm_dist_id_a varchar(10) default null,
	adm_note text null,
	adm_trans float null default 0,	
	adm_prg_date_vis datetime default null,
	adm_prg_ptt_id_a varchar(10) default null,
	adm_prg_date_del datetime default null,
	adm_prg_dlt_id_a char(1) default null,
	adm_prg_drt_id_a char(1) default null,
	adm_prg_weight float default null,
	adm_prg_date_ctrl1 datetime default null,
	adm_prg_date_ctrl2 datetime default null,
	adm_prg_date_abort datetime default null,
	adm_lock int not null default '0',
	adm_deleted char(1) not null default 'N',
	primary key  ( adm_id )
) engine=MyISAM;

create table admissiontype (
	admt_id_a varchar (10)  not null ,
	admt_desc varchar (50)  not null ,
	primary key ( admt_id_a )
) engine=MyISAM;

create table dischargetype (
	dist_id_a varchar (10)  not null ,
	dist_desc varchar (50)  not null ,
	primary key ( dist_id_a )
) engine=MyISAM;


create table deliveryresulttype (
	drt_id_a char (1)  not null ,
	drt_desc varchar (50)  not null ,
	primary key ( drt_id_a )
) engine=MyISAM;


create table deliverytype (
	dlt_id_a char (1)  not null ,
	dlt_desc varchar (50)  not null ,
	primary key ( dlt_id_a )
) engine=MyISAM;

create table disease (
	dis_id_a varchar (10)  not null ,
	dis_desc varchar (160)  not null ,
	dis_dcl_id_a char (2)  not null ,
	dis_lock int not null default 0,
	dis_opd_include int(11) not null  default 0, 
	dis_ipd_include int(11) not null  default 0,
	primary key ( dis_id_a )
) engine=MyISAM;

create table diseasetype (
	dcl_id_a char (2)  not null ,
	dcl_desc varchar (110)  not null ,
	primary key ( dcl_id_a )
) engine=MyISAM;

create table exam (
	exa_id_a varchar (10)  not null ,
	exa_desc varchar (100)  not null ,
	exa_exc_id_a char (2)  not null ,
	exa_proc int not null,					
	exa_default varchar(50) ,				
	exa_lock int not null default 0,
	primary key ( exa_id_a )
) engine=MyISAM;

create table examrow (
	exr_id int not null auto_increment,
	exr_exa_id_a varchar (10)  not null ,
	exr_desc varchar (50)  not null ,
	primary key ( exr_id )
) engine=MyISAM;

create table examtype (
	exc_id_a char (2)  not null ,
	exc_desc varchar (50)  not null ,
	primary key ( exc_id_a )
) engine=MyISAM;


create table hospital (
	hos_id_a varchar (10)  not null ,
	hos_name varchar (255)  not null ,
	hos_addr varchar (255)  not null ,
	hos_city varchar (255)  not null ,
	hos_tele varchar (50)  null ,
	hos_fax varchar (50)  null ,
	hos_email varchar (50)  null ,
	hos_lock int not null default 0,
	primary key ( hos_id_a )
) engine=MyISAM;

create table help(
	hl_id int not null auto_increment,
	hl_mask int not null,
	hl_field int not null,
	hl_lang char(2),
	hl_msg varchar(255),
	primary key (hl_id)
) engine=MyISAM;


create table laboratory (
	lab_id int not null auto_increment ,
	lab_exa_id_a varchar (10)  not null ,
	lab_date datetime not null  , 
	lab_res varchar (50)  not null ,
	lab_note varchar (255) null ,
	lab_pat_id int null,					
	lab_pat_name varchar(100) null ,		
	lab_cross1 int null ,				
	lab_cross2 int null ,
	lab_cross3 int null ,
	lab_cross4 int null ,
	lab_cross5 int null ,
	lab_cross6 int null ,
	lab_cross7 int null ,
	lab_cross8 int null ,
	lab_cross9 int null ,
	lab_cross10 int null ,
	lab_cross11 int null ,
	lab_cross12 int null ,
	lab_cross13 int null ,	
	lab_lock int not null default 0,		
	lab_age int(11) null, 
	lab_sex char(1) null,
	lab_material varchar(25) null,
	lab_exam_date date null,
	lab_pat_inout char(1) null,
	primary key ( lab_id )
) engine=MyISAM;


create table laboratoryrow (					
	labr_id int not null auto_increment ,
	labr_lab_id int  not null ,
	labr_desc varchar (50)  not null ,
	primary key ( labr_id )
) engine=MyISAM;


create table log (
	log_id int not null auto_increment ,
	log_type int  not null ,
	log_class varchar (100)  null ,
	log_tyme datetime not null,
	log_mess varchar (255) null , 
	primary key ( log_id )
) engine=MyISAM;

create table malnutritioncontrol (
	mln_id int not null auto_increment ,
	mln_date_supp datetime not null ,		
	mnl_date_conf datetime null ,
	mln_adm_id int not null ,
	mln_height float not null ,
	mln_weight float not null ,
	mln_lock int not null default 0,
	primary key ( mln_id )
) engine=MyISAM;


create table medicaldsr (
	mdsr_id int not null auto_increment ,
	mdsr_mdsrt_id_a char (1)  not null ,
	mdsr_desc varchar (100)  not null ,
	mdsr_min_stock_qti float not null default 0,
	mdsr_ini_stock_qti float not null default 0,
	mdsr_in_qti float  not null default 0,
	mdsr_out_qti float  not null default 0,
	mdsr_lock int not null default 0,
	unique index ( mdsr_mdsrt_id_a,mdsr_desc) ,
	primary key (mdsr_id )
) engine=MyISAM;

create table medicaldsrlot(
	lt_id_a varchar(50) not null,
	lt_prep_date datetime not null ,
	lt_due_date datetime not null ,
	lt_lock int not null default 0,
	primary key ( lt_id_a )
) engine=MyISAM;

create table medicaldsrstockmov (
	mmv_id int not null auto_increment ,
	mmv_mdsr_id int  not null ,
	mmv_wrd_id_a char(1) null ,
	mmv_mmvt_id_a varchar (10) not null ,
	mmv_lt_id_a varchar (50)   null ,
	mmv_date datetime not null ,
	mmv_qty float not null default 0,
	mmv_from varchar(30) null default 'jms' ,
	mmv_lock int not null default 0,
	primary key ( mmv_id )
) engine=MyISAM;

create table medicaldsrstockmovtype (
	mmvt_id_a varchar(10) not null ,
	mmvt_desc varchar (50)  not null ,
	mmvt_type char (2)  not null ,
	primary key ( mmvt_id_a )
) engine=MyISAM;

create table medicaldsrtype(
	mdsrt_id_a char(1) not null,
	mdsrt_desc varchar(30),
	primary key (mdsrt_id_a)
) engine=MyISAM;


create table opd ( 
	opd_id int(11) auto_increment not null,
	opd_date datetime not null,
	opd_new_pat char(1) not null default 'N',
	opd_date_vis date not null,
	opd_prog_year int(11) not null,
	opd_sex char(1) not null,
	opd_age int(11) not null default 0,
	opd_dis_id_a varchar(10) null,
	opd_dis_id_a_2 varchar(10) null,	
	opd_dis_id_a_3 varchar(10) null,
	opd_lock int(11) not null default '0',
	primary key(opd_id)
) engine=MyISAM;

create table operation (
	ope_id_a varchar (10)  not null ,
	ope_ocl_id_a char (2)  not null ,
	ope_desc varchar (50)  not null ,
	ope_stat int not null default 0,			
	ope_lock int not null default 0,
	primary key ( ope_id_a )
) engine=MyISAM;

create table operationtype (
	ocl_id_a char (2)  not null ,
	ocl_desc varchar (50)  not null ,
	ocl_type varchar (20) not null default 'major', 
	primary key ( ocl_id_a )
) engine=MyISAM;


create table patient (
	pat_id int not null auto_increment ,
	pat_fname varchar(50) not null,
	pat_sname varchar(50) not null,
	pat_name varchar(100) null,
	pat_age int not null ,
	pat_sex char (1)  not null ,
	pat_addr varchar (50)  null ,
	pat_city varchar (50)  not null ,
	pat_next_kin varchar (50)  null ,
	pat_tele varchar (50)  null ,
	pat_moth char (1)  null ,
	pat_fath char (1)  null ,
	pat_ledu char (1)  null ,
	pat_esta char (1)  null ,
	pat_ptoge char (1)  null ,
	pat_note text null,
	pat_deleted char(1) not null default 'N',
	pat_lock int not null default 0,
	primary key ( pat_id )
) engine=MyISAM;

create table patientvaccine (
	pav_id int not null auto_increment ,
	pav_yprog int not null ,
	pav_date datetime not null ,
	pav_pat_id int not null ,
	pav_vac_id_a varchar (10)  not null ,
	pav_lock int not null default 0,
	primary key ( pav_id )
) engine=MyISAM;


create table pregnanttreatmenttype (
	ptt_id_a varchar (10)  not null ,
	ptt_desc varchar (50)  not null ,
	primary key ( ptt_id_a )
) engine=MyISAM;


create table  user (
	us_id_a varchar(50) not null default '' ,
	us_ug_id_a varchar(50) not null default '' ,
	us_passwd varchar(50) not null default '' ,
	us_desc varchar(128) ,
	primary key (us_id_a)
) engine=MyISAM;

create table  usergroup (
	ug_id_a varchar(50) not null default '' ,
	ug_desc varchar(128) ,
	primary key (ug_id_a)
) engine=MyISAM;


create table vaccine (
	vac_id_a varchar (10)  not null ,
	vac_desc varchar (50)  not null ,
	vac_pati char (1)  not null ,
	vac_lock int not null default 0,
	primary key ( vac_id_a )
) engine=MyISAM;


create table version (
	ver_major int  not null ,
	ver_minor int not  null ,
	ver_source longblob  null, 
	ver_date datetime not null,
	ver_current char (1) default 'N' not null,
	primary key ( ver_major,ver_minor )
) engine=MyISAM;


create table ward (
	wrd_id_a char (1)  not null ,
	wrd_name varchar (50)  not null ,
	wrd_tele varchar (50)  null ,
	wrd_fax varchar (50)  null ,
	wrd_email varchar (50)  null ,
	wrd_nbeds int not null ,
	wrd_nqua_nurs int not null ,
	wrd_ndoc int not null ,
	wrd_lock int not null default 0,
	primary key ( wrd_id_a )
) engine=MyISAM;

-- menu area

create table groupmenu (
  	gm_id int not null auto_increment ,
  	gm_ug_id_a varchar(50) not null default '',
  	gm_mni_id_a varchar(50) not null default '',
  	gm_active char(1) not null default '',
  	primary key  (gm_id)
) engine=MyISAM;

create table menuitem (
  	mni_id_a varchar(50) not null default '',
  	mni_btn_label varchar(50) not null default '',
  	mni_label varchar(50) not null default '',
  	mni_tooltip varchar(100) default null,
  	mni_shortcut char(1) default null,
  	mni_submenu varchar(50) not null default '',
  	mni_class varchar(100) not null default '',
  	mni_is_submenu char(1) not null default 'N',
  	mni_position int(10) unsigned not null default '0',
  	primary key  (mni_id_a)
) engine=MyISAM;
