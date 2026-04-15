-- extend wrd_id_a to char(3) and update all its references
-- drop foreign keys and indexes
alter table oh_admission drop foreign key fk_admission_ward;
alter table oh_admission drop index fk_admission_ward;
alter table oh_medicaldsrstockmov drop foreign key fk_medicaldsrstockmov_ward;
alter table oh_medicaldsrstockmov drop index fk_medicaldsrstockmov_ward;
alter table oh_medicaldsrstockmovward drop foreign key fk_medicaldsrstockmovward_ward;
alter table oh_medicaldsrstockmovward drop index FK_MEDICALDSRSTOCKMOVWARD_WARD_idx;
alter table oh_medicaldsrward drop foreign key fk_medicaldsrward_ward;
alter table oh_medicaldsrward drop index FK_MEDICALDSRWARD_WARD_idx;
alter table oh_visits drop foreign key fk_visits_ward;
alter table oh_visits drop index fk_visits_ward;

-- change columns types
alter table oh_ward modify column wrd_id_a char(3) not null default '' ;
alter table oh_admission modify column adm_wrd_id_a char(3) not null default '' ;
alter table oh_medicaldsrward modify column mdsrwrd_wrd_id_a char(3) not null default '';
alter table oh_medicaldsrstockmovward modify column mmvn_wrd_id_a char(3) not null default '';
alter table oh_medicaldsrstockmov modify column mmv_wrd_id_a char(3) default '';
alter table oh_visits modify column vst_wrd_id_a char(3) default '';

-- add again foreign keys and indexes
alter table oh_admission
	add constraint fk_admission_ward
	    foreign key (adm_wrd_id_a )
	    references oh_ward (wrd_id_a )
	    on delete no action
	    on update no action,
	add index FK_ADMISSION_WARD_idx (adm_wrd_id_a asc);

alter table oh_medicaldsrstockmov
 	add constraint fk_medicaldsrstockmov_ward
	    foreign key (mmv_wrd_id_a )
	    references oh_ward (wrd_id_a )
	    on delete no action
    	on update no action,
	add index FK_MEDICALDSRSTOCKMOV_WARD_idx (mmv_wrd_id_a asc);

alter table oh_medicaldsrstockmovward  	
    add constraint fk_medicaldsrstockmovward_ward
	    foreign key (mmvn_wrd_id_a )
	    references oh_ward (wrd_id_a )
	    on delete no action
	    on update no action,
	add index FK_MEDICALDSRSTOCKMOVWARD_WARD_idx (mmvn_wrd_id_a asc);

alter table oh_medicaldsrward  	    
 	add constraint fk_medicaldsrward_ward
	    foreign key (mdsrwrd_wrd_id_a )
	    references oh_ward (wrd_id_a )
	    on delete no action
	    on update no action,
	add index FK_MEDICALDSRWARD_WARD_idx (mdsrwrd_wrd_id_a asc);
        
alter table oh_visits
	add constraint fk_visits_ward 
		foreign key (vst_wrd_id_a) 
        references oh_ward (wrd_id_a) 
        on delete no action 
        on update no action,
	add index FK_VISITS_WARD_idx (vst_wrd_id_a asc);
	
-- add new field to ward table
alter table oh_ward add column wrd_is_opd tinyint(1) not null default '0' after wrd_ndoc;
insert into oh_ward (wrd_id_a, wrd_name, wrd_tele, wrd_fax, wrd_email, wrd_nbeds, wrd_nqua_nurs, wrd_ndoc, wrd_is_opd, wrd_is_pharmacy, wrd_is_male, wrd_is_female, wrd_visit_duration, wrd_lock, wrd_created_by, wrd_created_date, wrd_active) 
values ('opd', 'opd', '234/52544', '54324/5424', 'opd@stluke.org', '0', '1', '1', '1', '1', '1', '1', '15', '0', 'admin', now(), '1');

-- add new field to opd table
alter table oh_opd add column opd_wrd_id_a char(3) null default null after opd_id;
update oh_opd set opd_wrd_id_a = 'opd'; -- set new 'opd' ward for previous data
alter table oh_opd add index FK_OPD_WARD_idx (opd_wrd_id_a asc); -- add index
alter table oh_opd change column opd_wrd_id_a opd_wrd_id_a char(3) not null ; -- set not null
alter table oh_opd 
add constraint fk_opd_ward
  foreign key (opd_wrd_id_a)
  references oh_ward (wrd_id_a)
  on delete no action
  on update no action; -- add foreign key
  
-- update visits table
update oh_visits set vst_wrd_id_a = 'opd', vst_duration = 15, vst_service = '' where vst_wrd_id_a IS null; -- set new 'opd' ward for previous data

-- convert opd_date_next_vis into FK
alter table oh_opd add opd_next_visit_id int(11) null after opd_usr_id_a; -- the new field

-- update new fields
update oh_opd set opd_next_visit_id = (select vst_id from oh_visits where vst_wrd_id_a = 'opd' and vst_pat_id = opd_pat_id and vst_date = opd_date_next_vis);

-- add indexes foreign keys
alter table oh_opd 
add index FK_OPD_NEXT_VISIT_idx (opd_next_visit_id asc);
alter table oh_opd 
add constraint fk_opd_next_visit
  foreign key (opd_next_visit_id)
  references oh_visits (vst_id)
  on delete no action
  on update no action;
  
-- drop opd_date_next_vis field
alter table oh_opd drop column opd_date_next_vis;