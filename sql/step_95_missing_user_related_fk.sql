set @old_unique_checks=@@unique_checks, unique_checks=0;
set @old_foreign_key_checks=@@foreign_key_checks, foreign_key_checks=0;
set @old_sql_mode=@@sql_mode, sql_mode='traditional';

alter table oh_agetype engine = innodb, convert to character set utf8;
alter table oh_groupmenu engine = innodb, convert to character set utf8;
alter table oh_grouppermission engine = innodb, convert to character set utf8;
alter table oh_help engine = innodb, convert to character set utf8;
alter table oh_log engine = innodb, convert to character set utf8;
alter table oh_malnutritioncontrol engine = innodb, convert to character set utf8;
alter table oh_menuitem engine = innodb, convert to character set utf8;
alter table oh_patientexamination engine = innodb, convert to character set utf8;
alter table oh_patienthistory engine = innodb, convert to character set utf8;
alter table oh_patient_profile_photo engine = innodb, convert to character set utf8;
alter table oh_permissions engine = innodb, convert to character set utf8;
alter table oh_pricesothers engine = innodb, convert to character set utf8;
alter table oh_supplier engine = innodb, convert to character set utf8;
alter table oh_version engine = innodb, convert to character set utf8;

alter table oh_admission 
add index fk_admission_user_idx (adm_usr_id_a asc) visible;

alter table oh_admission 
add constraint fk_admission_user
  foreign key (adm_usr_id_a)
  references oh_user (us_id_a)
  on delete no action
  on update no action;

alter table oh_billpayments 
add index fk_billpayments_user_idx (blp_usr_id_a asc) visible;

alter table oh_billpayments 
add constraint fk_billpayments_user
  foreign key (blp_usr_id_a)
  references oh_user (us_id_a)
  on delete no action
  on update no action;

alter table oh_bills 
add index fk_bills_user_idx (bll_usr_id_a asc) visible;

alter table oh_bills 
add constraint fk_bills_user
  foreign key (bll_usr_id_a)
  references oh_user (us_id_a)
  on delete no action
  on update no action;
  
alter table oh_opd 
add index fk_opd_user_idx (opd_usr_id_a asc) visible;

alter table oh_opd 
add constraint fk_opd_user
  foreign key (opd_usr_id_a)
  references oh_user (us_id_a)
  on delete no action
  on update no action;
  
alter table oh_session_audit
drop foreign key oh_session_audit_ibfk_1;

alter table oh_session_audit
add key fk_session_audit_user_idx (sea_us_id_a),
add constraint fk_session_audit_user
    foreign key (sea_us_id_a)
    references oh_user (us_id_a)
    on delete cascade
    on update cascade;
    
alter table oh_admission 
add index fk_admission_created_by_idx (adm_created_by asc) visible;

alter table oh_admission 
add constraint fk_admission_created_by
  foreign key (adm_created_by)
  references oh_user (us_id_a)
  on delete no action
  on update no action;

alter table oh_admissiontype 
add index fk_admissiontype_created_by_idx (admt_created_by asc) visible;

alter table oh_admissiontype 
add constraint fk_admissiontype_created_by
  foreign key (admt_created_by)
  references oh_user (us_id_a)
  on delete no action
  on update no action;

alter table oh_agetype 
add index fk_agetype_created_by_idx (at_created_by asc) visible;

alter table oh_agetype 
add constraint fk_agetype_created_by
  foreign key (at_created_by)
  references oh_user (us_id_a)
  on delete no action
  on update no action;

alter table oh_billitems 
add index fk_billitems_created_by_idx (bli_created_by asc) visible;

alter table oh_billitems 
add constraint fk_billitems_created_by
  foreign key (bli_created_by)
  references oh_user (us_id_a)
  on delete no action
  on update no action;

alter table oh_billpayments 
add index fk_billpayments_created_by_idx (blp_created_by asc) visible;

alter table oh_billpayments 
add constraint fk_billpayments_created_by
  foreign key (blp_created_by)
  references oh_user (us_id_a)
  on delete no action
  on update no action;

alter table oh_bills 
add index fk_bills_created_by_idx (bll_created_by asc) visible;

alter table oh_bills 
add constraint fk_bills_created_by
  foreign key (bll_created_by)
  references oh_user (us_id_a)
  on delete no action
  on update no action;

alter table oh_deliveryresulttype 
add index fk_deliveryresulttype_created_by_idx (drt_created_by asc) visible;

alter table oh_deliveryresulttype 
add constraint fk_deliveryresulttype_created_by
  foreign key (drt_created_by)
  references oh_user (us_id_a)
  on delete no action
  on update no action;

alter table oh_deliverytype 
add index fk_deliverytype_created_by_idx (dlt_created_by asc) visible;

alter table oh_deliverytype 
add constraint fk_deliverytype_created_by
  foreign key (dlt_created_by)
  references oh_user (us_id_a)
  on delete no action
  on update no action;

alter table oh_dicom 
add index fk_dicom_created_by_idx (dm_created_by asc) visible;

alter table oh_dicom 
add constraint fk_dicom_created_by
  foreign key (dm_created_by)
  references oh_user (us_id_a)
  on delete no action
  on update no action;

alter table oh_dischargetype 
add index fk_dischargetype_created_by_idx (dist_created_by asc) visible;

alter table oh_dischargetype 
add constraint fk_dischargetype_created_by
  foreign key (dist_created_by)
  references oh_user (us_id_a)
  on delete no action
  on update no action;

alter table oh_disease 
add index fk_disease_created_by_idx (dis_created_by asc) visible;

alter table oh_disease 
add constraint fk_disease_created_by
  foreign key (dis_created_by)
  references oh_user (us_id_a)
  on delete no action
  on update no action;

alter table oh_diseasetype 
add index fk_diseasetype_created_by_idx (dcl_created_by asc) visible;

alter table oh_diseasetype 
add constraint fk_diseasetype_created_by
  foreign key (dcl_created_by)
  references oh_user (us_id_a)
  on delete no action
  on update no action;

alter table oh_exam 
add index fk_exam_created_by_idx (exa_created_by asc) visible;

alter table oh_exam 
add constraint fk_exam_created_by
  foreign key (exa_created_by)
  references oh_user (us_id_a)
  on delete no action
  on update no action;

alter table oh_examrow 
add index fk_examrow_created_by_idx (exr_created_by asc) visible;

alter table oh_examrow 
add constraint fk_examrow_created_by
  foreign key (exr_created_by)
  references oh_user (us_id_a)
  on delete no action
  on update no action;

alter table oh_examtype 
add index fk_examtype_created_by_idx (exc_created_by asc) visible;

alter table oh_examtype 
add constraint fk_examtype_created_by
  foreign key (exc_created_by)
  references oh_user (us_id_a)
  on delete no action
  on update no action;

alter table oh_groupmenu 
add index fk_groupmenu_created_by_idx (gm_created_by asc) visible;

alter table oh_groupmenu 
add constraint fk_groupmenu_created_by
  foreign key (gm_created_by)
  references oh_user (us_id_a)
  on delete no action
  on update no action;

alter table oh_grouppermission 
add index fk_grouppermission_created_by_idx (gp_created_by asc) visible;

alter table oh_grouppermission 
add constraint fk_grouppermission_created_by
  foreign key (gp_created_by)
  references oh_user (us_id_a)
  on delete no action
  on update no action;

alter table oh_hospital 
add index fk_hospital_created_by_idx (hos_created_by asc) visible;

alter table oh_hospital 
add constraint fk_hospital_created_by
  foreign key (hos_created_by)
  references oh_user (us_id_a)
  on delete no action
  on update no action;

alter table oh_laboratory 
add index fk_laboratory_created_by_idx (lab_created_by asc) visible;

alter table oh_laboratory 
add constraint fk_laboratory_created_by
  foreign key (lab_created_by)
  references oh_user (us_id_a)
  on delete no action
  on update no action;

alter table oh_laboratoryrow 
add index fk_laboratoryrow_created_by_idx (labr_created_by asc) visible;

alter table oh_laboratoryrow 
add constraint fk_laboratoryrow_created_by
  foreign key (labr_created_by)
  references oh_user (us_id_a)
  on delete no action
  on update no action;

alter table oh_malnutritioncontrol 
add index fk_malnutritioncontrol_created_by_idx (mln_created_by asc) visible;

alter table oh_malnutritioncontrol 
add constraint fk_malnutritioncontrol_created_by
  foreign key (mln_created_by)
  references oh_user (us_id_a)
  on delete no action
  on update no action;

alter table oh_medicaldsr 
add index fk_medicaldsr_created_by_idx (mdsr_created_by asc) visible;

alter table oh_medicaldsr 
add constraint fk_medicaldsr_created_by
  foreign key (mdsr_created_by)
  references oh_user (us_id_a)
  on delete no action
  on update no action;

alter table oh_medicaldsrlot 
add index fk_medicaldsrlot_created_by_idx (lt_created_by asc) visible;

alter table oh_medicaldsrlot 
add constraint fk_medicaldsrlot_created_by
  foreign key (lt_created_by)
  references oh_user (us_id_a)
  on delete no action
  on update no action;

alter table oh_medicaldsrstockmov 
add index fk_medicaldsrstockmov_created_by_idx (mmv_created_by asc) visible;

alter table oh_medicaldsrstockmov 
add constraint fk_medicaldsrstockmov_created_by
  foreign key (mmv_created_by)
  references oh_user (us_id_a)
  on delete no action
  on update no action;

alter table oh_medicaldsrstockmovtype 
add index fk_medicaldsrstockmovtype_created_by_idx (mmvt_created_by asc) visible;

alter table oh_medicaldsrstockmovtype 
add constraint fk_medicaldsrstockmovtype_created_by
  foreign key (mmvt_created_by)
  references oh_user (us_id_a)
  on delete no action
  on update no action;

alter table oh_medicaldsrstockmovward 
add index fk_medicaldsrstockmovward_created_by_idx (mmvn_created_by asc) visible;

alter table oh_medicaldsrstockmovward 
add constraint fk_medicaldsrstockmovward_created_by
  foreign key (mmvn_created_by)
  references oh_user (us_id_a)
  on delete no action
  on update no action;

alter table oh_medicaldsrtype 
add index fk_medicaldsrtype_created_by_idx (mdsrt_created_by asc) visible;

alter table oh_medicaldsrtype 
add constraint fk_medicaldsrtype_created_by
  foreign key (mdsrt_created_by)
  references oh_user (us_id_a)
  on delete no action
  on update no action;

alter table oh_medicaldsrward 
add index fk_medicaldsrward_created_by_idx (mdsrwrd_created_by asc) visible;

alter table oh_medicaldsrward 
add constraint fk_medicaldsrward_created_by
  foreign key (mdsrwrd_created_by)
  references oh_user (us_id_a)
  on delete no action
  on update no action;

alter table oh_opd 
add index fk_opd_created_by_idx (opd_created_by asc) visible;

alter table oh_opd 
add constraint fk_opd_created_by
  foreign key (opd_created_by)
  references oh_user (us_id_a)
  on delete no action
  on update no action;

alter table oh_operation 
add index fk_operation_created_by_idx (ope_created_by asc) visible;

alter table oh_operation 
add constraint fk_operation_created_by
  foreign key (ope_created_by)
  references oh_user (us_id_a)
  on delete no action
  on update no action;

alter table oh_operationrow 
add index fk_operationrow_created_by_idx (oper_created_by asc) visible;

alter table oh_operationrow 
add constraint fk_operationrow_created_by
  foreign key (oper_created_by)
  references oh_user (us_id_a)
  on delete no action
  on update no action;

alter table oh_operationtype 
add index fk_operationtype_created_by_idx (ocl_created_by asc) visible;

alter table oh_operationtype 
add constraint fk_operationtype_created_by
  foreign key (ocl_created_by)
  references oh_user (us_id_a)
  on delete no action
  on update no action;

alter table oh_patient 
add index fk_patient_created_by_idx (pat_created_by asc) visible;

alter table oh_patient 
add constraint fk_patient_created_by
  foreign key (pat_created_by)
  references oh_user (us_id_a)
  on delete no action
  on update no action;

alter table oh_patientexamination 
add index fk_patientexamination_created_by_idx (pex_created_by asc) visible;

alter table oh_patientexamination 
add constraint fk_patientexamination_created_by
  foreign key (pex_created_by)
  references oh_user (us_id_a)
  on delete no action
  on update no action;

alter table oh_patienthistory 
add index fk_patienthistory_created_by_idx (pah_created_by asc) visible;

alter table oh_patienthistory 
add constraint fk_patienthistory_created_by
  foreign key (pah_created_by)
  references oh_user (us_id_a)
  on delete no action
  on update no action;

alter table oh_patientvaccine 
add index fk_patientvaccine_created_by_idx (pav_created_by asc) visible;

alter table oh_patientvaccine 
add constraint fk_patientvaccine_created_by
  foreign key (pav_created_by)
  references oh_user (us_id_a)
  on delete no action
  on update no action;

alter table oh_permissions 
add index fk_permissions_created_by_idx (p_created_by asc) visible;

alter table oh_permissions 
add constraint fk_permissions_created_by
  foreign key (p_created_by)
  references oh_user (us_id_a)
  on delete no action
  on update no action;

alter table oh_pregnanttreatmenttype 
add index fk_pregnanttreatmenttype_created_by_idx (ptt_created_by asc) visible;

alter table oh_pregnanttreatmenttype 
add constraint fk_pregnanttreatmenttype_created_by
  foreign key (ptt_created_by)
  references oh_user (us_id_a)
  on delete no action
  on update no action;

alter table oh_pricelists 
add index fk_pricelists_created_by_idx (lst_created_by asc) visible;

alter table oh_pricelists 
add constraint fk_pricelists_created_by
  foreign key (lst_created_by)
  references oh_user (us_id_a)
  on delete no action
  on update no action;

alter table oh_prices 
add index fk_prices_created_by_idx (prc_created_by asc) visible;

alter table oh_prices 
add constraint fk_prices_created_by
  foreign key (prc_created_by)
  references oh_user (us_id_a)
  on delete no action
  on update no action;

alter table oh_pricesothers 
add index fk_pricesothers_created_by_idx (oth_created_by asc) visible;

alter table oh_pricesothers 
add constraint fk_pricesothers_created_by
  foreign key (oth_created_by)
  references oh_user (us_id_a)
  on delete no action
  on update no action;

alter table oh_session_audit 
add index fk_session_audit_created_by_idx (sea_created_by asc) visible;

alter table oh_session_audit 
add constraint fk_session_audit_created_by
  foreign key (sea_created_by)
  references oh_user (us_id_a)
  on delete cascade
  on update cascade;

alter table oh_supplier 
add index fk_supplier_created_by_idx (sup_created_by asc) visible;

alter table oh_supplier 
add constraint fk_supplier_created_by
  foreign key (sup_created_by)
  references oh_user (us_id_a)
  on delete no action
  on update no action;

alter table oh_therapies 
add index fk_therapies_created_by_idx (thr_created_by asc) visible;

alter table oh_therapies 
add constraint fk_therapies_created_by
  foreign key (thr_created_by)
  references oh_user (us_id_a)
  on delete no action
  on update no action;

alter table oh_user 
add index fk_user_created_by_idx (us_created_by asc) visible;

alter table oh_user 
add constraint fk_user_created_by
  foreign key (us_created_by)
  references oh_user (us_id_a)
  on delete no action
  on update no action;

alter table oh_usergroup 
add index fk_usergroup_created_by_idx (ug_created_by asc) visible;

alter table oh_usergroup 
add constraint fk_usergroup_created_by
  foreign key (ug_created_by)
  references oh_user (us_id_a)
  on delete no action
  on update no action;

alter table oh_vaccine 
add index fk_vaccine_created_by_idx (vac_created_by asc) visible;

alter table oh_vaccine 
add constraint fk_vaccine_created_by
  foreign key (vac_created_by)
  references oh_user (us_id_a)
  on delete no action
  on update no action;

alter table oh_vaccinetype 
add index fk_vaccinetype_created_by_idx (vact_created_by asc) visible;

alter table oh_vaccinetype 
add constraint fk_vaccinetype_created_by
  foreign key (vact_created_by)
  references oh_user (us_id_a)
  on delete no action
  on update no action;

alter table oh_visits 
add index fk_visits_created_by_idx (vst_created_by asc) visible;

alter table oh_visits 
add constraint fk_visits_created_by
  foreign key (vst_created_by)
  references oh_user (us_id_a)
  on delete no action
  on update no action;

alter table oh_ward 
add index fk_ward_created_by_idx (wrd_created_by asc) visible;

alter table oh_ward 
add constraint fk_ward_created_by
  foreign key (wrd_created_by)
  references oh_user (us_id_a)
  on delete no action
  on update no action;
  
alter table oh_admission 
add index fk_admission_last_modified_by_idx (adm_last_modified_by asc) visible;

alter table oh_admission 
add constraint fk_admission_last_modified_by
  foreign key (adm_last_modified_by)
  references oh_user (us_id_a)
  on delete no action
  on update no action;

alter table oh_admissiontype 
add index fk_admissiontype_last_modified_by_idx (admt_last_modified_by asc) visible;

alter table oh_admissiontype 
add constraint fk_admissiontype_last_modified_by
  foreign key (admt_last_modified_by)
  references oh_user (us_id_a)
  on delete no action
  on update no action;

alter table oh_agetype 
add index fk_agetype_last_modified_by_idx (at_last_modified_by asc) visible;

alter table oh_agetype 
add constraint fk_agetype_last_modified_by
  foreign key (at_last_modified_by)
  references oh_user (us_id_a)
  on delete no action
  on update no action;

alter table oh_billitems 
add index fk_billitems_last_modified_by_idx (bli_last_modified_by asc) visible;

alter table oh_billitems 
add constraint fk_billitems_last_modified_by
  foreign key (bli_last_modified_by)
  references oh_user (us_id_a)
  on delete no action
  on update no action;

alter table oh_billpayments 
add index fk_billpayments_last_modified_by_idx (blp_last_modified_by asc) visible;

alter table oh_billpayments 
add constraint fk_billpayments_last_modified_by
  foreign key (blp_last_modified_by)
  references oh_user (us_id_a)
  on delete no action
  on update no action;

alter table oh_bills 
add index fk_bills_last_modified_by_idx (bll_last_modified_by asc) visible;

alter table oh_bills 
add constraint fk_bills_last_modified_by
  foreign key (bll_last_modified_by)
  references oh_user (us_id_a)
  on delete no action
  on update no action;

alter table oh_deliveryresulttype 
add index fk_deliveryresulttype_last_modified_by_idx (drt_last_modified_by asc) visible;

alter table oh_deliveryresulttype 
add constraint fk_deliveryresulttype_last_modified_by
  foreign key (drt_last_modified_by)
  references oh_user (us_id_a)
  on delete no action
  on update no action;

alter table oh_deliverytype 
add index fk_deliverytype_last_modified_by_idx (dlt_last_modified_by asc) visible;

alter table oh_deliverytype 
add constraint fk_deliverytype_last_modified_by
  foreign key (dlt_last_modified_by)
  references oh_user (us_id_a)
  on delete no action
  on update no action;

alter table oh_dicom 
add index fk_dicom_last_modified_by_idx (dm_last_modified_by asc) visible;

alter table oh_dicom 
add constraint fk_dicom_last_modified_by
  foreign key (dm_last_modified_by)
  references oh_user (us_id_a)
  on delete no action
  on update no action;

alter table oh_dischargetype 
add index fk_dischargetype_last_modified_by_idx (dist_last_modified_by asc) visible;

alter table oh_dischargetype 
add constraint fk_dischargetype_last_modified_by
  foreign key (dist_last_modified_by)
  references oh_user (us_id_a)
  on delete no action
  on update no action;

alter table oh_disease 
add index fk_disease_last_modified_by_idx (dis_last_modified_by asc) visible;

alter table oh_disease 
add constraint fk_disease_last_modified_by
  foreign key (dis_last_modified_by)
  references oh_user (us_id_a)
  on delete no action
  on update no action;

alter table oh_diseasetype 
add index fk_diseasetype_last_modified_by_idx (dcl_last_modified_by asc) visible;

alter table oh_diseasetype 
add constraint fk_diseasetype_last_modified_by
  foreign key (dcl_last_modified_by)
  references oh_user (us_id_a)
  on delete no action
  on update no action;

alter table oh_exam 
add index fk_exam_last_modified_by_idx (exa_last_modified_by asc) visible;

alter table oh_exam 
add constraint fk_exam_last_modified_by
  foreign key (exa_last_modified_by)
  references oh_user (us_id_a)
  on delete no action
  on update no action;

alter table oh_examrow 
add index fk_examrow_last_modified_by_idx (exr_last_modified_by asc) visible;

alter table oh_examrow 
add constraint fk_examrow_last_modified_by
  foreign key (exr_last_modified_by)
  references oh_user (us_id_a)
  on delete no action
  on update no action;

alter table oh_examtype 
add index fk_examtype_last_modified_by_idx (exc_last_modified_by asc) visible;

alter table oh_examtype 
add constraint fk_examtype_last_modified_by
  foreign key (exc_last_modified_by)
  references oh_user (us_id_a)
  on delete no action
  on update no action;

alter table oh_groupmenu 
add index fk_groupmenu_last_modified_by_idx (gm_last_modified_by asc) visible;

alter table oh_groupmenu 
add constraint fk_groupmenu_last_modified_by
  foreign key (gm_last_modified_by)
  references oh_user (us_id_a)
  on delete no action
  on update no action;

alter table oh_grouppermission 
add index fk_grouppermission_last_modified_by_idx (gp_last_modified_by asc) visible;

alter table oh_grouppermission 
add constraint fk_grouppermission_last_modified_by
  foreign key (gp_last_modified_by)
  references oh_user (us_id_a)
  on delete no action
  on update no action;

alter table oh_hospital 
add index fk_hospital_last_modified_by_idx (hos_last_modified_by asc) visible;

alter table oh_hospital 
add constraint fk_hospital_last_modified_by
  foreign key (hos_last_modified_by)
  references oh_user (us_id_a)
  on delete no action
  on update no action;

alter table oh_laboratory 
add index fk_laboratory_last_modified_by_idx (lab_last_modified_by asc) visible;

alter table oh_laboratory 
add constraint fk_laboratory_last_modified_by
  foreign key (lab_last_modified_by)
  references oh_user (us_id_a)
  on delete no action
  on update no action;

alter table oh_laboratoryrow 
add index fk_laboratoryrow_last_modified_by_idx (labr_last_modified_by asc) visible;

alter table oh_laboratoryrow 
add constraint fk_laboratoryrow_last_modified_by
  foreign key (labr_last_modified_by)
  references oh_user (us_id_a)
  on delete no action
  on update no action;

alter table oh_malnutritioncontrol 
add index fk_malnutritioncontrol_last_modified_by_idx (mln_last_modified_by asc) visible;

alter table oh_malnutritioncontrol 
add constraint fk_malnutritioncontrol_last_modified_by
  foreign key (mln_last_modified_by)
  references oh_user (us_id_a)
  on delete no action
  on update no action;

alter table oh_medicaldsr 
add index fk_medicaldsr_last_modified_by_idx (mdsr_last_modified_by asc) visible;

alter table oh_medicaldsr 
add constraint fk_medicaldsr_last_modified_by
  foreign key (mdsr_last_modified_by)
  references oh_user (us_id_a)
  on delete no action
  on update no action;

alter table oh_medicaldsrlot 
add index fk_medicaldsrlot_last_modified_by_idx (lt_last_modified_by asc) visible;

alter table oh_medicaldsrlot 
add constraint fk_medicaldsrlot_last_modified_by
  foreign key (lt_last_modified_by)
  references oh_user (us_id_a)
  on delete no action
  on update no action;

alter table oh_medicaldsrstockmov 
add index fk_medicaldsrstockmov_last_modified_by_idx (mmv_last_modified_by asc) visible;

alter table oh_medicaldsrstockmov 
add constraint fk_medicaldsrstockmov_last_modified_by
  foreign key (mmv_last_modified_by)
  references oh_user (us_id_a)
  on delete no action
  on update no action;

alter table oh_medicaldsrstockmovtype 
add index fk_medicaldsrstockmovtype_last_modified_by_idx (mmvt_last_modified_by asc) visible;

alter table oh_medicaldsrstockmovtype 
add constraint fk_medicaldsrstockmovtype_last_modified_by
  foreign key (mmvt_last_modified_by)
  references oh_user (us_id_a)
  on delete no action
  on update no action;

alter table oh_medicaldsrstockmovward 
add index fk_medicaldsrstockmovward_last_modified_by_idx (mmvn_last_modified_by asc) visible;

alter table oh_medicaldsrstockmovward 
add constraint fk_medicaldsrstockmovward_last_modified_by
  foreign key (mmvn_last_modified_by)
  references oh_user (us_id_a)
  on delete no action
  on update no action;

alter table oh_medicaldsrtype 
add index fk_medicaldsrtype_last_modified_by_idx (mdsrt_last_modified_by asc) visible;

alter table oh_medicaldsrtype 
add constraint fk_medicaldsrtype_last_modified_by
  foreign key (mdsrt_last_modified_by)
  references oh_user (us_id_a)
  on delete no action
  on update no action;

alter table oh_medicaldsrward 
add index fk_medicaldsrward_last_modified_by_idx (mdsrwrd_last_modified_by asc) visible;

alter table oh_medicaldsrward 
add constraint fk_medicaldsrward_last_modified_by
  foreign key (mdsrwrd_last_modified_by)
  references oh_user (us_id_a)
  on delete no action
  on update no action;

alter table oh_opd 
add index fk_opd_last_modified_by_idx (opd_last_modified_by asc) visible;

alter table oh_opd 
add constraint fk_opd_last_modified_by
  foreign key (opd_last_modified_by)
  references oh_user (us_id_a)
  on delete no action
  on update no action;

alter table oh_operation 
add index fk_operation_last_modified_by_idx (ope_last_modified_by asc) visible;

alter table oh_operation 
add constraint fk_operation_last_modified_by
  foreign key (ope_last_modified_by)
  references oh_user (us_id_a)
  on delete no action
  on update no action;

alter table oh_operationrow 
add index fk_operationrow_last_modified_by_idx (oper_last_modified_by asc) visible;

alter table oh_operationrow 
add constraint fk_operationrow_last_modified_by
  foreign key (oper_last_modified_by)
  references oh_user (us_id_a)
  on delete no action
  on update no action;

alter table oh_operationtype 
add index fk_operationtype_last_modified_by_idx (ocl_last_modified_by asc) visible;

alter table oh_operationtype 
add constraint fk_operationtype_last_modified_by
  foreign key (ocl_last_modified_by)
  references oh_user (us_id_a)
  on delete no action
  on update no action;

alter table oh_patient 
add index fk_patient_last_modified_by_idx (pat_last_modified_by asc) visible;

alter table oh_patient 
add constraint fk_patient_last_modified_by
  foreign key (pat_last_modified_by)
  references oh_user (us_id_a)
  on delete no action
  on update no action;

alter table oh_patientexamination 
add index fk_patientexamination_last_modified_by_idx (pex_last_modified_by asc) visible;

alter table oh_patientexamination 
add constraint fk_patientexamination_last_modified_by
  foreign key (pex_last_modified_by)
  references oh_user (us_id_a)
  on delete no action
  on update no action;

alter table oh_patienthistory 
add index fk_patienthistory_last_modified_by_idx (pah_last_modified_by asc) visible;

alter table oh_patienthistory 
add constraint fk_patienthistory_last_modified_by
  foreign key (pah_last_modified_by)
  references oh_user (us_id_a)
  on delete no action
  on update no action;

alter table oh_patientvaccine 
add index fk_patientvaccine_last_modified_by_idx (pav_last_modified_by asc) visible;

alter table oh_patientvaccine 
add constraint fk_patientvaccine_last_modified_by
  foreign key (pav_last_modified_by)
  references oh_user (us_id_a)
  on delete no action
  on update no action;

alter table oh_permissions 
add index fk_permissions_last_modified_by_idx (p_last_modified_by asc) visible;

alter table oh_permissions 
add constraint fk_permissions_last_modified_by
  foreign key (p_last_modified_by)
  references oh_user (us_id_a)
  on delete no action
  on update no action;

alter table oh_pregnanttreatmenttype 
add index fk_pregnanttreatmenttype_last_modified_by_idx (ptt_last_modified_by asc) visible;

alter table oh_pregnanttreatmenttype 
add constraint fk_pregnanttreatmenttype_last_modified_by
  foreign key (ptt_last_modified_by)
  references oh_user (us_id_a)
  on delete no action
  on update no action;

alter table oh_pricelists 
add index fk_pricelists_last_modified_by_idx (lst_last_modified_by asc) visible;

alter table oh_pricelists 
add constraint fk_pricelists_last_modified_by
  foreign key (lst_last_modified_by)
  references oh_user (us_id_a)
  on delete no action
  on update no action;

alter table oh_prices 
add index fk_prices_last_modified_by_idx (prc_last_modified_by asc) visible;

alter table oh_prices 
add constraint fk_prices_last_modified_by
  foreign key (prc_last_modified_by)
  references oh_user (us_id_a)
  on delete no action
  on update no action;

alter table oh_pricesothers 
add index fk_pricesothers_last_modified_by_idx (oth_last_modified_by asc) visible;

alter table oh_pricesothers 
add constraint fk_pricesothers_last_modified_by
  foreign key (oth_last_modified_by)
  references oh_user (us_id_a)
  on delete no action
  on update no action;

alter table oh_session_audit 
add index fk_session_audit_last_modified_by_idx (sea_last_modified_by asc) visible;

alter table oh_session_audit 
add constraint fk_session_audit_last_modified_by
  foreign key (sea_last_modified_by)
  references oh_user (us_id_a)
  on delete cascade
  on update cascade;
  
alter table oh_supplier 
add index fk_supplier_last_modified_by_idx (sup_last_modified_by asc) visible;

alter table oh_supplier 
add constraint fk_supplier_last_modified_by
  foreign key (sup_last_modified_by)
  references oh_user (us_id_a)
  on delete no action
  on update no action;

alter table oh_therapies 
add index fk_therapies_last_modified_by_idx (thr_last_modified_by asc) visible;

alter table oh_therapies 
add constraint fk_therapies_last_modified_by
  foreign key (thr_last_modified_by)
  references oh_user (us_id_a)
  on delete no action
  on update no action;

alter table oh_user 
add index fk_user_last_modified_by_idx (us_last_modified_by asc) visible;

alter table oh_user 
add constraint fk_user_last_modified_by
  foreign key (us_last_modified_by)
  references oh_user (us_id_a)
  on delete no action
  on update no action;

alter table oh_usergroup 
add index fk_usergroup_last_modified_by_idx (ug_last_modified_by asc) visible;

alter table oh_usergroup 
add constraint fk_usergroup_last_modified_by
  foreign key (ug_last_modified_by)
  references oh_user (us_id_a)
  on delete no action
  on update no action;

alter table oh_vaccine 
add index fk_vaccine_last_modified_by_idx (vac_last_modified_by asc) visible;

alter table oh_vaccine 
add constraint fk_vaccine_last_modified_by
  foreign key (vac_last_modified_by)
  references oh_user (us_id_a)
  on delete no action
  on update no action;

alter table oh_vaccinetype 
add index fk_vaccinetype_last_modified_by_idx (vact_last_modified_by asc) visible;

alter table oh_vaccinetype 
add constraint fk_vaccinetype_last_modified_by
  foreign key (vact_last_modified_by)
  references oh_user (us_id_a)
  on delete no action
  on update no action;

alter table oh_visits 
add index fk_visits_last_modified_by_idx (vst_last_modified_by asc) visible;

alter table oh_visits 
add constraint fk_visits_last_modified_by
  foreign key (vst_last_modified_by)
  references oh_user (us_id_a)
  on delete no action
  on update no action;

alter table oh_ward 
add index fk_ward_last_modified_by_idx (wrd_last_modified_by asc) visible;

alter table oh_ward 
add constraint fk_ward_last_modified_by
  foreign key (wrd_last_modified_by)
  references oh_user (us_id_a)
  on delete no action
  on update no action;
  
set sql_mode=@old_sql_mode;
set foreign_key_checks=@old_foreign_key_checks;
set unique_checks=@old_unique_checks;