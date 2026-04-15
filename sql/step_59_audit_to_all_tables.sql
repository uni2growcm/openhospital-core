alter table admission 
  add column adm_created_by varchar(50) null default null after adm_deleted, 
  add column adm_created_date datetime null default null, 
  add column adm_last_modified_by varchar(50) null default null, 
  add column adm_last_modified_date datetime null default null, 
  add column adm_active tinyint(1) not null default 1; 
alter table admissiontype 
  add column admt_created_by varchar(50) null default null after admt_desc, 
  add column admt_created_date datetime null default null, 
  add column admt_last_modified_by varchar(50) null default null, 
  add column admt_last_modified_date datetime null default null, 
  add column admt_active tinyint(1) not null default 1; 
alter table agetype 
  add column at_created_by varchar(50) null default null after at_desc, 
  add column at_created_date datetime null default null, 
  add column at_last_modified_by varchar(50) null default null, 
  add column at_last_modified_date datetime null default null, 
  add column at_active tinyint(1) not null default 1; 
alter table billitems 
  add column bli_created_by varchar(50) null default null after bli_qty, 
  add column bli_created_date datetime null default null, 
  add column bli_last_modified_by varchar(50) null default null, 
  add column bli_last_modified_date datetime null default null, 
  add column bli_active tinyint(1) not null default 1; 
alter table billpayments 
  add column blp_created_by varchar(50) null default null after blp_usr_id_a, 
  add column blp_created_date datetime null default null, 
  add column blp_last_modified_by varchar(50) null default null, 
  add column blp_last_modified_date datetime null default null, 
  add column blp_active tinyint(1) not null default 1; 
alter table bills 
  add column bll_created_by varchar(50) null default null after bll_usr_id_a, 
  add column bll_created_date datetime null default null, 
  add column bll_last_modified_by varchar(50) null default null, 
  add column bll_last_modified_date datetime null default null, 
  add column bll_active tinyint(1) not null default 1; 
alter table deliveryresulttype 
  add column drt_created_by varchar(50) null default null after drt_desc, 
  add column drt_created_date datetime null default null, 
  add column drt_last_modified_by varchar(50) null default null, 
  add column drt_last_modified_date datetime null default null, 
  add column drt_active tinyint(1) not null default 1; 
alter table deliverytype 
  add column dlt_created_by varchar(50) null default null after dlt_desc, 
  add column dlt_created_date datetime null default null, 
  add column dlt_last_modified_by varchar(50) null default null, 
  add column dlt_last_modified_date datetime null default null, 
  add column dlt_active tinyint(1) not null default 1; 
alter table dicom 
  add column dm_created_by varchar(50) null default null after dm_thumbnail, 
  add column dm_created_date datetime null default null, 
  add column dm_last_modified_by varchar(50) null default null, 
  add column dm_last_modified_date datetime null default null, 
  add column dm_active tinyint(1) not null default 1; 
alter table dischargetype 
  add column dist_created_by varchar(50) null default null after dist_desc, 
  add column dist_created_date datetime null default null, 
  add column dist_last_modified_by varchar(50) null default null, 
  add column dist_last_modified_date datetime null default null, 
  add column dist_active tinyint(1) not null default 1; 
alter table disease 
  add column dis_created_by varchar(50) null default null after dis_ipd_out_include, 
  add column dis_created_date datetime null default null, 
  add column dis_last_modified_by varchar(50) null default null, 
  add column dis_last_modified_date datetime null default null, 
  add column dis_active tinyint(1) not null default 1; 
alter table diseasetype 
  add column dcl_created_by varchar(50) null default null  after dcl_desc, 
  add column dcl_created_date datetime null default null, 
  add column dcl_last_modified_by varchar(50) null default null, 
  add column dcl_last_modified_date datetime null default null, 
  add column dcl_active tinyint(1) not null default 1; 
alter table exam 
  add column exa_created_by varchar(50) null default null after exa_lock, 
  add column exa_created_date datetime null default null, 
  add column exa_last_modified_by varchar(50) null default null, 
  add column exa_last_modified_date datetime null default null, 
  add column exa_active tinyint(1) not null default 1; 
alter table examrow 
  add column exr_created_by varchar(50) null default null after exr_desc, 
  add column exr_created_date datetime null default null, 
  add column exr_last_modified_by varchar(50) null default null, 
  add column exr_last_modified_date datetime null default null, 
  add column exr_active tinyint(1) not null default 1; 
alter table examtype 
  add column exc_created_by varchar(50) null default null after exc_desc, 
  add column exc_created_date datetime null default null, 
  add column exc_last_modified_by varchar(50) null default null, 
  add column exc_last_modified_date datetime null default null, 
  add column exc_active tinyint(1) not null default 1; 

-- alter group menu
alter table groupmenu 
  add column gm_created_by varchar(50) null default null after gm_active, 
  add column gm_created_date datetime null default null, 
  add column gm_last_modified_by varchar(50) null default null, 
  add column gm_last_modified_date datetime null default null; 
update groupmenu set gm_active = 1 where gm_active = 'Y';
update groupmenu set gm_active = 0 where gm_active = 'N';
alter table groupmenu  
  change column gm_active gm_active tinyint(1) not null default 1;

alter table hospital 
  add column hos_created_by varchar(50) null default null after hos_lock, 
  add column hos_created_date datetime null default null, 
  add column hos_last_modified_by varchar(50) null default null, 
  add column hos_last_modified_date datetime null default null, 
  add column hos_active tinyint(1) not null default 1; 
alter table laboratory 
  add column lab_created_by varchar(50) null default null  after lab_pat_inout, 
  add column lab_created_date datetime null default null, 
  add column lab_last_modified_by varchar(50) null default null, 
  add column lab_last_modified_date datetime null default null, 
  add column lab_active tinyint(1) not null default 1; 
alter table laboratoryrow 
  add column labr_created_by varchar(50) null default null after labr_desc, 
  add column labr_created_date datetime null default null, 
  add column labr_last_modified_by varchar(50) null default null, 
  add column labr_last_modified_date datetime null default null, 
  add column labr_active tinyint(1) not null default 1; 
alter table malnutritioncontrol 
  add column mln_created_by varchar(50) null default null after mln_lock, 
  add column mln_created_date datetime null default null, 
  add column mln_last_modified_by varchar(50) null default null, 
  add column mln_last_modified_date datetime null default null, 
  add column mln_active tinyint(1) not null default 1; 
alter table medicaldsr 
  add column mdsr_created_by varchar(50) null default null after mdsr_lock, 
  add column mdsr_created_date datetime null default null, 
  add column mdsr_last_modified_by varchar(50) null default null, 
  add column mdsr_last_modified_date datetime null default null, 
  add column mdsr_active tinyint(1) not null default 1; 
alter table medicaldsrlot 
  add column lt_created_by varchar(50) null default null after lt_lock, 
  add column lt_created_date datetime null default null, 
  add column lt_last_modified_by varchar(50) null default null, 
  add column lt_last_modified_date datetime null default null, 
  add column lt_active tinyint(1) not null default 1; 
alter table medicaldsrstockmov 
  add column mmv_created_by varchar(50) null default null after mmv_refno , 
  add column mmv_created_date datetime null default null, 
  add column mmv_last_modified_by varchar(50) null default null, 
  add column mmv_last_modified_date datetime null default null, 
  add column mmv_active tinyint(1) not null default 1; 
alter table medicaldsrstockmovtype 
  add column mmvt_created_by varchar(50) null default null after mmvt_type, 
  add column mmvt_created_date datetime null default null, 
  add column mmvt_last_modified_by varchar(50) null default null, 
  add column mmvt_last_modified_date datetime null default null, 
  add column mmvt_active tinyint(1) not null default 1; 
alter table medicaldsrstockmovward 
  add column mmvn_created_by varchar(50) null default null after mmvn_mdsr_units, 
  add column mmvn_created_date datetime null default null, 
  add column mmvn_last_modified_by varchar(50) null default null, 
  add column mmvn_last_modified_date datetime null default null, 
  add column mmvn_active tinyint(1) not null default 1; 
alter table medicaldsrtype 
  add column mdsrt_created_by varchar(50) null default null after mdsrt_desc, 
  add column mdsrt_created_date datetime null default null, 
  add column mdsrt_last_modified_by varchar(50) null default null, 
  add column mdsrt_last_modified_date datetime null default null, 
  add column mdsrt_active tinyint(1) not null default 1; 
alter table medicaldsrward 
  add column mdsrwrd_created_by varchar(50) null default null after mdsrwrd_out_qti, 
  add column mdsrwrd_created_date datetime null default null, 
  add column mdsrwrd_last_modified_by varchar(50) null default null, 
  add column mdsrwrd_last_modified_date datetime null default null, 
  add column mdsrwrd_active tinyint(1) not null default 1;  
alter table opd 
  add column opd_created_by varchar(50) null default null after opd_lock, 
  add column opd_created_date datetime null default null, 
  add column opd_last_modified_by varchar(50) null default null, 
  add column opd_last_modified_date datetime null default null, 
  add column opd_active tinyint(1) not null default 1; 
alter table operation 
  add column ope_created_by varchar(50) null default null after ope_lock, 
  add column ope_created_date datetime null default null, 
  add column ope_last_modified_by varchar(50) null default null, 
  add column ope_last_modified_date datetime null default null, 
  add column ope_active tinyint(1) not null default 1; 
alter table operationrow 
  add column oper_created_by varchar(50) null default null after oper_trans_unit, 
  add column oper_created_date datetime null default null, 
  add column oper_last_modified_by varchar(50) null default null, 
  add column oper_last_modified_date datetime null default null, 
  add column oper_active tinyint(1) not null default 1; 
alter table operationtype 
  add column ocl_created_by varchar(50) null default null after ocl_type, 
  add column ocl_created_date datetime null default null, 
  add column ocl_last_modified_by varchar(50) null default null, 
  add column ocl_last_modified_date datetime null default null, 
  add column ocl_active tinyint(1) not null default 1; 
alter table patient 
  add column pat_created_by varchar(50) null default null after pat_timestamp, 
  add column pat_created_date datetime null default null, 
  add column pat_last_modified_by varchar(50) null default null, 
  add column pat_last_modified_date datetime null default null, 
  add column pat_active tinyint(1) not null default 1; 
alter table patientexamination 
  add column pex_created_by varchar(50) null default null after pex_note, 
  add column pex_created_date datetime null default null, 
  add column pex_last_modified_by varchar(50) null default null, 
  add column pex_last_modified_date datetime null default null, 
  add column pex_active tinyint(1) not null default 1; 
alter table patientvaccine 
  add column pav_created_by varchar(50) null default null after pav_lock, 
  add column pav_created_date datetime null default null, 
  add column pav_last_modified_by varchar(50) null default null, 
  add column pav_last_modified_date datetime null default null, 
  add column pav_active tinyint(1) not null default 1; 
alter table pregnanttreatmenttype 
  add column ptt_created_by varchar(50) null default null after ptt_desc, 
  add column ptt_created_date datetime null default null, 
  add column ptt_last_modified_by varchar(50) null default null, 
  add column ptt_last_modified_date datetime null default null, 
  add column ptt_active tinyint(1) not null default 1; 
alter table pricelists 
  add column lst_created_by varchar(50) null default null after lst_currency, 
  add column lst_created_date datetime null default null, 
  add column lst_last_modified_by varchar(50) null default null, 
  add column lst_last_modified_date datetime null default null, 
  add column lst_active tinyint(1) not null default 1; 
alter table prices 
  add column prc_created_by varchar(50) null default null after prc_price, 
  add column prc_created_date datetime null default null, 
  add column prc_last_modified_by varchar(50) null default null, 
  add column prc_last_modified_date datetime null default null, 
  add column prc_active tinyint(1) not null default 1; 
alter table pricesothers 
  add column oth_created_by varchar(50) null default null after oth_undefined, 
  add column oth_created_date datetime null default null, 
  add column oth_last_modified_by varchar(50) null default null, 
  add column oth_last_modified_date datetime null default null, 
  add column oth_active tinyint(1) not null default 1;  
alter table supplier 
  add column sup_created_by varchar(50) null default null after sup_deleted, 
  add column sup_created_date datetime null default null, 
  add column sup_last_modified_by varchar(50) null default null, 
  add column sup_last_modified_date datetime null default null, 
  add column sup_active tinyint(1) not null default 1; 
alter table therapies 
  add column thr_created_by varchar(50) null default null after thr_sms, 
  add column thr_created_date datetime null default null, 
  add column thr_last_modified_by varchar(50) null default null, 
  add column thr_last_modified_date datetime null default null, 
  add column thr_active tinyint(1) not null default 1; 
alter table user 
  add column us_created_by varchar(50) null default null after us_desc, 
  add column us_created_date datetime null default null, 
  add column us_last_modified_by varchar(50) null default null, 
  add column us_last_modified_date datetime null default null, 
  add column us_active tinyint(1) not null default 1; 
alter table usergroup 
  add column ug_created_by varchar(50) null default null after ug_desc, 
  add column ug_created_date datetime null default null, 
  add column ug_last_modified_by varchar(50) null default null, 
  add column ug_last_modified_date datetime null default null, 
  add column ug_active tinyint(1) not null default 1; 
alter table vaccine 
  add column vac_created_by varchar(50) null default null after vac_lock, 
  add column vac_created_date datetime null default null, 
  add column vac_last_modified_by varchar(50) null default null, 
  add column vac_last_modified_date datetime null default null, 
  add column vac_active tinyint(1) not null default 1; 
alter table vaccinetype 
  add column vact_created_by varchar(50) null default null after vact_desc, 
  add column vact_created_date datetime null default null, 
  add column vact_last_modified_by varchar(50) null default null, 
  add column vact_last_modified_date datetime null default null, 
  add column vact_active tinyint(1) not null default 1;  
alter table visits 
  add column vst_created_by varchar(50) null default null after vst_sms, 
  add column vst_created_date datetime null default null, 
  add column vst_last_modified_by varchar(50) null default null, 
  add column vst_last_modified_date datetime null default null, 
  add column vst_active tinyint(1) not null default 1; 
alter table ward 
  add column wrd_created_by varchar(50) null default null after wrd_lock, 
  add column wrd_created_date datetime null default null, 
  add column wrd_last_modified_by varchar(50) null default null, 
  add column wrd_last_modified_date datetime null default null, 
  add column wrd_active tinyint(1) not null default 1; 


