create table oh_patienthistory (
 	pah_id int(11) not null auto_increment,
	pah_pat_id int not null,
	pah_active tinyint(1) not null default 1,
	pah_created_by varchar(50) default null,
  	pah_created_date datetime default null,
  	pah_last_modified_by varchar(50) default null,
  	pah_last_modified_date datetime default null,
	pah_fam_nothing tinyint(1)  default 1 ,
	pah_fam_hyper tinyint(1)  default 0 ,
	pah_fam_drugadd tinyint(1)  default 0 ,
	pah_fam_cardio tinyint(1)  default 0 ,
	pah_fam_infect tinyint(1)  default 0 ,
	pah_fam_endo tinyint(1)  default 0 ,
	pah_fam_resp tinyint(1)  default 0 ,
	pah_fam_cancer tinyint(1)  default 0 ,
	pah_fam_orto tinyint(1)  default 0 ,
	pah_fam_gyno tinyint(1)  default 0 ,
	pah_fam_other tinyint(1)  default 0 ,
	pah_fam_note varchar(100) null ,
	pah_pat_clo_nothing tinyint(1)  default 1 ,
	pah_pat_clo_hyper tinyint(1)  default 0 ,
	pah_pat_clo_drugadd tinyint(1)  default 0 ,
	pah_pat_clo_cardio tinyint(1)  default 0 ,
	pah_pat_clo_infect tinyint(1)  default 0 ,
	pah_pat_clo_endo tinyint(1)  default 0 ,
	pah_pat_clo_resp tinyint(1)  default 0 ,
	pah_pat_clo_cancer tinyint(1)  default 0 ,
	pah_pat_clo_orto tinyint(1)  default 0 ,
	pah_pat_clo_gyno tinyint(1)  default 0 ,
	pah_pat_clo_other tinyint(1)  default 0 ,
	pah_pat_clo_note varchar(100) null ,
	pah_pat_opn_nothing tinyint(1)  default 1 ,
	pah_pat_opn_hyper tinyint(1)  default 0 ,
	pah_pat_opn_drugadd tinyint(1)  default 0 ,
	pah_pat_opn_cardio tinyint(1)  default 0 ,
	pah_pat_opn_infect tinyint(1)  default 0 ,
	pah_pat_opn_endo tinyint(1)  default 0 ,
	pah_pat_opn_resp tinyint(1)  default 0 ,
	pah_pat_opn_cancer tinyint(1)  default 0 ,
	pah_pat_opn_orto tinyint(1)  default 0 ,
	pah_pat_opn_gyno tinyint(1)  default 0 ,
	pah_pat_opn_other tinyint(1)  default 0 ,
	pah_pat_opn_note varchar(100) null ,
	pah_pat_surgery varchar(200) null ,
	pah_pat_allergy varchar(100) null ,
	pah_pat_therapy varchar(200) null ,
	pah_pat_medicine varchar(200) null ,
	pah_pat_note varchar(100) null ,
	pah_phy_nutr_nor tinyint(1)  default 1 ,
	pah_phy_nutr_abn varchar(30) null ,
	pah_phy_alvo_nor tinyint(1)  default 1 ,
	pah_phy_alvo_abn varchar(30) null ,
	pah_phy_diure_nor tinyint(1)  null default 1 ,
	pah_phy_diure_abn varchar(30) null ,
	pah_phy_alcool tinyint(1)  default 0 ,
	pah_phy_smoke tinyint(1)  default 0 ,
	pah_phy_drug tinyint(1)  default 0 ,
	pah_phy_period_nor tinyint(1)  default 1 ,
	pah_phy_period_abn varchar(30) null ,
	pah_phy_menop tinyint(1)  default 0 ,
	pah_phy_menop_y int  null ,
	pah_phy_hrt_nor tinyint(1)  default 1 ,
	pah_phy_hrt_abn varchar(30) null ,
	pah_phy_preg tinyint(1)  default 0 ,
	pah_phy_preg_n int  null ,
	pah_phy_preg_birth int  null ,
	pah_phy_preg_abort int  null ,
	pah_date_update timestamp default current_timestamp on update current_timestamp ,

	index ( pah_pat_id  ) ,
	primary key ( pah_id )
) engine=MyISAM;


--
-- constraints
--
alter table oh_patienthistory
	add constraint fk_patienthistory_patient
	foreign key (pah_pat_id)
	references patient (pat_id)
	on delete cascade
	on update cascade;
	
-- Anamnesis Button
insert into oh_menuitem values ('btnadmpatnewanamnesis','angal.patient.anamnesis','angal.patient.anamnesis','x','A','btnadmnew','none','N', 1);
insert into oh_menuitem values ('btnadmpateditanamnesis','angal.patient.anamnesis','angal.patient.anamnesis','x','A','btnadmedit','none','N', 1);
insert into oh_menuitem values ('btnadmanamnesis','angal.admission.anamnesis','angal.admission.anamnesis','x','A','admission','none','N', 1);
insert into oh_menuitem values ('btnopdnewanamnesis','angal.opd.anamnesis','angal.opd.anamnesis','x','A','btnopdnew','none','N', 1);
insert into oh_menuitem values ('btnopdeditanamnesis','angal.opd.anamnesis','angal.opd.anamnesis','x','A','btnopdedit','none','N', 1);


-- Admin activation (default: btnadmpatnewanamnesis, btnadmpateditanamnesis, btnopdnewxamination, btnopdeditxamination)
insert into oh_groupmenu (gm_id,gm_ug_id_a,gm_mni_id_a,gm_active) values  (190, 'admin', 'btnadmanamnesis',1);
insert into oh_groupmenu (gm_id,gm_ug_id_a,gm_mni_id_a,gm_active) values  (191, 'admin', 'btnadmpatnewanamnesis',1);
insert into oh_groupmenu (gm_id,gm_ug_id_a,gm_mni_id_a,gm_active) values  (192, 'admin', 'btnadmpateditanamnesis',1);
insert into oh_groupmenu (gm_id,gm_ug_id_a,gm_mni_id_a,gm_active) values  (193, 'admin', 'btnopdnewanamnesis',0);
insert into oh_groupmenu (gm_id,gm_ug_id_a,gm_mni_id_a,gm_active) values  (194, 'admin', 'btnopdeditanamnesis',0);	
	