-- Preliminary modifications to the db set sql_mode = no_auto_value_on_zero; 
insert into patient (pat_id,pat_fname,pat_sname,pat_name,pat_bdate,pat_age,pat_agetype,pat_sex,pat_addr,pat_city,pat_next_kin,pat_tele,pat_moth_name,pat_moth,pat_fath_name,pat_fath,pat_ledu,pat_esta,pat_ptoge,pat_note,pat_deleted,pat_lock,pat_btype,pat_photo,pat_taxcode) values (0,'Patient','null','null Patient','-',0,'-','U','-','-','-','-','-','U','-','U',null,'U','U','-','Y',0,'-',null,'-');
alter table patient add column pat_timestamp timeSTAMP default current_timestamp;
alter table admission change column adm_date_adm adm_date_adm datetime not null;
alter table medicaldsrstockmovward
	change column mmvn_pat_id mmvn_pat_id int(11) null,
	change column mmvn_pat_age mmvn_pat_age smallint(6) null,
	change column mmvn_pat_weight mmvn_pat_weight float null;
	
-- Altering tables and creating FKs
-- on Windows system my.cnf should be set as follow:
-- lower_case_file_system = on <-- automatically set by the system
-- lower_case_table_names = 1 <-- if you are using innodb tables, you should set this variable to 1 on all platforms to force names to be converted to lowercase.  

set @old_unique_checks=@@unique_checks, unique_checks=0;
set @old_foreign_key_checks=@@foreign_key_checks, foreign_key_checks=0;
set @old_sql_mode=@@sql_mode, sql_mode='traditional';

alter table hospital engine = innodb, convert to character set utf8;
alter table admission engine = innodb, convert to character set utf8;
alter table dischargetype engine = innodb, convert to character set utf8;
alter table deliverytype engine = innodb, convert to character set utf8;
alter table deliveryresulttype engine = innodb, convert to character set utf8;
alter table admissiontype engine = innodb, convert to character set utf8;
alter table operation engine = innodb, convert to character set utf8;
alter table operationtype engine = innodb, convert to character set utf8;
alter table ward engine = innodb, convert to character set utf8;
alter table pregnanttreatmenttype engine = innodb, convert to character set utf8;
alter table disease engine = innodb, convert to character set utf8;
alter table patient engine = innodb, convert to character set utf8;
alter table pricelists engine = innodb, convert to character set utf8;
alter table bills engine = innodb, convert to character set utf8;
alter table prices engine = innodb, convert to character set utf8;
alter table billitems engine = innodb, convert to character set utf8;
alter table billpayments engine = innodb, convert to character set utf8;
alter table exam engine = innodb, convert to character set utf8;
alter table examtype engine = innodb, convert to character set utf8;
alter table examrow engine = innodb, convert to character set utf8;
alter table laboratory engine = innodb, convert to character set utf8;
alter table laboratoryrow engine = innodb, convert to character set utf8;
alter table medicaldsr engine = innodb, convert to character set utf8;
alter table medicaldsrtype engine = innodb, convert to character set utf8;
alter table medicaldsrstockmov engine = innodb, convert to character set utf8;
alter table medicaldsrlot engine = innodb, convert to character set utf8;
alter table medicaldsrstockmovtype engine = innodb, convert to character set utf8;
alter table medicaldsrstockmovward engine = innodb, convert to character set utf8;
alter table medicaldsrward engine = innodb, convert to character set utf8;
alter table vaccine engine = innodb, convert to character set utf8;
alter table vaccinetype engine = innodb, convert to character set utf8;
alter table patientvaccine engine = innodb, convert to character set utf8;
alter table diseasetype engine = innodb, convert to character set utf8;
alter table malnutritioncontrol engine = innodb, convert to character set utf8;
alter table opd engine = innodb, convert to character set utf8;
alter table therapies engine = innodb, convert to character set utf8;
alter table user engine = innodb, convert to character set utf8;
alter table usergroup engine = innodb, convert to character set utf8;
alter table visits engine = innodb, convert to character set utf8;

alter table admission
	add constraint fk_admission_dischargetype
		foreign key (adm_dist_id_a )
	    references dischargetype (dist_id_a )
	    on delete no action
	    on update no action,
	add constraint fk_admission_deliverytype
	    foreign key (adm_prg_dlt_id_a )
	    references deliverytype (dlt_id_a )
	    on delete no action
	    on update no action,
	add constraint fk_admission_deliveryresulttype
	    foreign key (adm_prg_drt_id_a )
	    references deliveryresulttype (drt_id_a )
	    on delete no action
	    on update no action,
	add constraint fk_admission_admissiontype
	    foreign key (adm_admt_id_a_adm )
	    references admissiontype (admt_id_a )
	    on delete no action
	    on update no action,
	add constraint fk_admission_operation
	    foreign key (adm_ope_id_a )
	    references operation (ope_id_a )
	    on delete no action
	    on update no action,
	add constraint fk_admission_ward
	    foreign key (adm_wrd_id_a )
	    references ward (wrd_id_a )
	    on delete no action
	    on update no action,
	add constraint fk_admission_pregnanttreatmenttype
	    foreign key (adm_prg_ptt_id_a )
	    references pregnanttreatmenttype (ptt_id_a )
	    on delete no action
	    on update no action,
	add constraint fk_admission_in_disease
	    foreign key (adm_in_dis_id_a )
	    references disease (dis_id_a )
	    on delete no action
	    on update no action,
	add constraint fk_admission_out_disease1
	    foreign key (adm_out_dis_id_a )
	    references disease (dis_id_a )
	    on delete no action
	    on update no action,
	add constraint fk_admission_out_disease2
	    foreign key (adm_out_dis_id_a_2 )
	    references disease (dis_id_a )
	    on delete no action
	    on update no action,
	add constraint fk_admission_out_disease3
	    foreign key (adm_out_dis_id_a_3 )
	    references disease (dis_id_a )
	    on delete no action
	    on update no action,
	add constraint fk_admission_patient
	    foreign key (adm_pat_id )
	    references patient (pat_id )
	    on delete cascade
	    on update cascade;
    
alter table bills
	add constraint fk_bills_patient
	    foreign key (bll_id_pat )
	    references patient (pat_id )
	    on delete cascade
	    on update cascade,
  	add constraint fk_bills_pricelists
	    foreign key (bll_id_lst )
	    references pricelists (lst_id )
	    on delete no action
	    on update no action;
	    
alter table prices
	add constraint fk_prices_pricelists
	    foreign key (prc_lst_id )
	    references pricelists (lst_id )
	    on delete cascade
	    on update cascade;
    
alter table billitems
    add constraint fk_billitems_bills
	    foreign key (bli_id_bill )
	    references bills (bll_id )
	    on delete cascade
	    on update cascade;
	    
alter table billpayments
	add constraint fk_billpayments_bills
	    foreign key (blp_id_bill )
	    references bills (bll_id )
	    on delete cascade
	    on update cascade;
	    
alter table exam 
    add constraint fk_exam_examtype
	    foreign key (exa_exc_id_a )
	    references examtype (exc_id_a )
	    on delete no action
	    on update cascade;
    
alter table examrow
    add constraint fk_examrow_exam
	    foreign key (exr_exa_id_a )
	    references exam (exa_id_a )
	    on delete no action
	    on update cascade;
    
alter table laboratory    
    add constraint fk_laboratory_exam
    	foreign key (lab_exa_id_a )
    	references exam (exa_id_a )
    	on delete no action
    	on update no action,
  	add constraint fk_laboratory_patient
	    foreign key (lab_pat_id )
	    references patient (pat_id )
	    on delete cascade
	    on update cascade;
	    
alter table laboratoryrow	    
	add constraint fk_laboratoryrow_laboratory
	    foreign key (labr_lab_id )
	    references laboratory (lab_id )
	    on delete no action
		on update cascade;

alter table medicaldsr 
    add constraint fk_medicaldsr_medicaldsrtype
	    foreign key (mdsr_mdsrt_id_a )
	    references medicaldsrtype (mdsrt_id_a )
	    on delete no action
	    on update cascade;
    
alter table medicaldsrstockmov
    add constraint fk_medicaldsrstockmov_medicaldsr
	    foreign key (mmv_mdsr_id )
	    references medicaldsr (mdsr_id )
	    on delete no action
    	on update no action,
    add constraint fk_medicaldsrstockmov_medicaldsrstockmovtype
	    foreign key (mmv_mmvt_id_a )
	    references medicaldsrstockmovtype (mmvt_id_a )
	    on delete no action
    	on update no action, 
 	add constraint fk_medicaldsrstockmov_ward
	    foreign key (mmv_wrd_id_a )
	    references ward (wrd_id_a )
	    on delete no action
    	on update no action;

alter table medicaldsrstockmovward  	
    add constraint fk_medicaldsrstockmovward_ward
	    foreign key (mmvn_wrd_id_a )
	    references ward (wrd_id_a )
	    on delete no action
	    on update no action,
  	add constraint fk_medicaldsrstockmovward_patient
	    foreign key (mmvn_pat_id )
	    references patient (pat_id )
	    on delete cascade
	    on update cascade;

alter table medicaldsrward  	    
 	add constraint fk_medicaldsrward_ward
	    foreign key (mdsrwrd_wrd_id_a )
	    references ward (wrd_id_a )
	    on delete no action
	    on update no action,
  	add constraint fk_medicaldsrward_medicaldsr
	    foreign key (mdsrwrd_mdsr_id )
	    references medicaldsr (mdsr_id )
	    on delete no action
	    on update no action;
	    
alter table vaccine 
    add constraint fk_vaccine_vaccinetype
	    foreign key (vac_vact_id_a )
	    references vaccinetype (vact_id_a )
	    on delete no action
	    on update cascade;
    
alter table patientvaccine
	add constraint fk_patientvaccine_patient
	    foreign key (pav_pat_id )
	    references patient (pat_id )
	    on delete cascade
    	on update cascade,
  	add constraint fk_patientvaccine_vaccine
	    foreign key (pav_vac_id_a )
	    references vaccine (vac_id_a )
	    on delete no action
    	on update no action;
    	
alter table operation
	add constraint fk_operation_operationtype
	    foreign key (ope_ocl_id_a )
	    references operationtype (ocl_id_a )
	    on delete no action
	    on update cascade;
    
alter table disease 
    add constraint fk_disease_diseasetype
	    foreign key (dis_dcl_id_a )
	    references diseasetype (dcl_id_a )
	    on delete no action
	    on update cascade;
   
-- alter table malnutritioncontrol 
--    add constraint fk_malnutritioncontrol_admission
--	    foreign key (mln_adm_id )
--	    references admission (adm_id )
--	    on delete cascade
--	    on update cascade;
    
alter table opd     
    add constraint fk_opd_disease
	    foreign key (opd_dis_id_a )
	    references disease (dis_id_a )
	    on delete no action
	    on update no action,
  	add constraint fk_opd_disease_2
	    foreign key (opd_dis_id_a_2 )
	    references disease (dis_id_a )
	    on delete no action
	    on update no action,
  	add constraint fk_opd_disease_3
	    foreign key (opd_dis_id_a_3 )
	    references disease (dis_id_a )
	    on delete no action
	    on update no action,
  	add constraint fk_opd_patient
	    foreign key (opd_pat_id )
	    references patient (pat_id )
	    on delete cascade
	    on update cascade;
	    
alter table therapies 
    add constraint fk_therapies_patient
	    foreign key (thr_pat_id )
	    references patient (pat_id )
	    on delete cascade
	    on update cascade,
	add constraint fk_therapies_mdsr
  		foreign key (thr_mdsr_id)
  		references medicaldsr (mdsr_id)
  		on delete no action
  		on update no action;
    
alter table user     
    add constraint fk_user_usergroup
	    foreign key (us_ug_id_a )
	    references usergroup (ug_id_a )
	    on delete no action
	    on update cascade;
    
alter table visits
    add constraint fk_visits_patient
	    foreign key (vst_pat_id )
	    references patient (pat_id )
	    on delete cascade
	    on update cascade;
	    
set sql_mode=@old_sql_mode;
set foreign_key_checks=@old_foreign_key_checks;
set unique_checks=@old_unique_checks;