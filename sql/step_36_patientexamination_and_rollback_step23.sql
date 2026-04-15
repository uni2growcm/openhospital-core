create table patientexamination (
	pex_id int not null auto_increment ,
	pex_date datetime not null ,
	pex_pat_id int not null ,
	pex_height double default 0 ,
	pex_weight int default 0 ,
	pex_pa_min int default 0 ,
	pex_pa_max int default 0 ,
	pex_fc int default 0 ,
	pex_temp double default 0 ,
	pex_sat double default 0 ,
	pex_note varchar(300) null ,
	
	index ( pex_pat_id  ) ,
	primary key ( pex_id )
);

--
-- constraints
--
alter table patientexamination
	add constraint fk_patientexamination_patient 
	foreign key (pex_pat_id) 
	references patient (pat_id)
	on delete cascade
	on update cascade;

-- Examination Button
insert into menuitem values ('btnopdnewexamination','angal.opd.examination','angal.opd.examination','x','A','btnopdnew','none','N', 1);
insert into menuitem values ('btnopdeditexamination','angal.opd.examination','angal.opd.examination','x','A','btnopdedit','none','N', 1);
insert into menuitem values ('btnadmadmexamination','angal.admission.examination','angal.admission.examination','x','A','btnadmadm','none','N', 1);
insert into menuitem values ('btnadmexamination','angal.admission.examination','angal.admission.examination','x','A','admission','none','N', 1);


-- Admin activation
insert into groupmenu (gm_ug_id_a, gm_mni_id_a, gm_active)  values  ('admin','btnopdnewexamination','Y');
insert into groupmenu (gm_ug_id_a, gm_mni_id_a, gm_active)  values  ('admin','btnopdeditexamination','Y');
insert into groupmenu (gm_ug_id_a, gm_mni_id_a, gm_active)  values  ('admin','btnadmadmexamination','Y');
insert into groupmenu (gm_ug_id_a, gm_mni_id_a, gm_active)  values  ('admin','btnadmexamination','Y');

--
--  rollback on malnutritioncontrol changes (step_23_wardpharmacy20.sql)
--

insert into patientexamination (pex_date, pex_pat_id, pex_height, pex_weight) 
select mln_date_supp, mln_pat_id, if(mln_height<3, mln_height * 100, mln_height), mln_weight from malnutritioncontrol where mln_pat_id <> 0;

delete from malnutritioncontrol where mln_adm_id = 0;

alter table malnutritioncontrol drop column mln_pat_id, change column mln_adm_id mln_adm_id int(11) not null;
