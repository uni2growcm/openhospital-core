delimiter //
drop procedure if exists autoCreateLots;
create procedure autoCreateLots()
begin
    declare v_id varchar(50);
	declare v_ward char(1);
	declare v_medical int(11);
    declare v_prep_date, v_due_date datetime default curdate();
    declare done int default false;
    declare cur cursor for select mdsrwrd_wrd_id_a, mdsrwrd_mdsr_id
							from medicaldsrward;
    declare continue handler for not found set done = 1;

    open cur;
    read_loop: loop
        fetch cur into v_ward, v_medical;
        if done then
            leave read_loop;
        end if;
        
        set v_id = concat('AUTO_',v_ward,'_',v_medical); 
        insert into medicaldsrlot (lt_id_a, lt_mdsr_id, lt_prep_date, lt_due_date) values (v_id, v_medical, v_prep_date, v_due_date);
        update medicaldsrward set mdsrwrd_lt_id_a = v_id where mdsrwrd_wrd_id_a = v_ward and mdsrwrd_mdsr_id = v_medical;
        
    end loop;
  	close cur;
end; 

drop procedure if exists updateWardToWardReference;
create procedure updateWardToWardReference()
begin
    declare v_lot varchar(50);
	declare v_ward char(1);
	declare v_medical int(11);
    declare done int default false;
    declare cur cursor for select mmvn_mdsr_id, mmvn_wrd_id_a_from, mmvn_lt_id 
							from medicaldsrstockmovward
							where mmvn_wrd_id_a_from IS not null;
    declare continue handler for not found set done = 1;

    open cur;
    read_loop: loop
        fetch cur into v_medical, v_ward, v_lot;
        if done then
            leave read_loop;
        end if;
        
        update medicaldsrstockmovward set mmvn_lt_id = v_lot where mmvn_mdsr_id = v_medical and mmvn_wrd_id_a_to = v_ward;
        
    end loop;
  	close cur;
end; //

delimiter ;

-- adding medical id (lt_mdsr_id) to medicaldsrlot
alter table medicaldsrlot add column lt_mdsr_id int(11) null after lt_id_a;

-- populating new field (lt_mdsr_id)
update medicaldsrlot 
       join medicaldsrstockmov 
       on medicaldsrlot.lt_id_a = medicaldsrstockmov.mmv_lt_id_a
set medicaldsrlot.lt_mdsr_id = medicaldsrstockmov.mmv_mdsr_id;

-- adding lot id (mdsrwrd_lt_id_a) to medicaldsrward
alter table medicaldsrward add column mdsrwrd_lt_id_a varchar(50) null after mdsrwrd_out_qti;
alter table medicaldsrstockmovward add column mmvn_lt_id varchar(50) null after mmvn_wrd_id_a_to;

-- porting previous data
alter table medicaldsrward 
drop foreign key fk_medicaldsrward_medicaldsr,
drop foreign key fk_medicaldsrward_ward,
drop primary key;

call autoCreateLots();

alter table medicaldsrward change column mdsrwrd_lt_id_a mdsrwrd_lt_id_a varchar(50) not null;

-- adding foreign keys and indexes
alter table medicaldsrlot change column lt_mdsr_id lt_mdsr_id int(11) not null;

alter table medicaldsrlot 
add index FK_MEDICALDSRLOT_MEDICALDSR_idx (lt_mdsr_id asc),
add constraint fk_medicaldsrlot_medicaldsr
  foreign key (lt_mdsr_id)
  references medicaldsr (mdsr_id)
  on delete no action
  on update no action;

alter table medicaldsrward
add index FK_MEDICALDSRWARD_WARD_idx (mdsrwrd_wrd_id_a asc),
add constraint fk_medicaldsrward_ward
  foreign key (mdsrwrd_wrd_id_a)
  references ward (wrd_id_a)
  on delete no action
  on update no action,
add index FK_MEDICALDSRWARD_MEDICALDSR_idx (mdsrwrd_mdsr_id asc),
add constraint fk_medicaldsrward_medicaldsr
  foreign key (mdsrwrd_mdsr_id)
  references medicaldsr (mdsr_id)
  on delete no action
  on update no action,
add index FK_MEDICALDSRWARD_MEDICALDSRLOT_idx (mdsrwrd_lt_id_a asc),
add constraint fk_medicaldsrward_medicaldsrlot
  foreign key (mdsrwrd_lt_id_a)
  references medicaldsrlot (lt_id_a)
  on delete no action
  on update no action;

alter table medicaldsrstockmovward 
drop index fk_medicaldsrstockmovward_ward,
add index FK_MEDICALDSRSTOCKMOVWARD_WARD_idx (mmvn_wrd_id_a asc),
drop index fk_medicaldsrstockmovward_patient,
add index FK_MEDICALDSRSTOCKMOVWARD_PATIENT_idx (mmvn_pat_id asc),
add index FK_MEDICALDSRSTOCKMOVWARD_LOT_idx (mmvn_lt_id asc),
add constraint fk_medicaldsrstockmovward_lot
  foreign key (mmvn_lt_id)
  references medicaldsrlot (lt_id_a)
  on delete no action
  on update no action;

-- changing compound primary key
alter table medicaldsrward add primary key (mdsrwrd_wrd_id_a, mdsrwrd_mdsr_id, mdsrwrd_lt_id_a);

call updateWardToWardReference();