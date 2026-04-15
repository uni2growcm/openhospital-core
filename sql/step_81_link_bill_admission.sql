alter table oh_bills add column bll_adm_id int(11) null default null after bll_usr_id_a;
alter table oh_bills
add index FK_BILLS_ADMISSION_idx (bll_adm_id asc);
alter table oh_bills
add constraint fk_bills_admission
  foreign key (bll_adm_id)
  references oh_admission (adm_id)
  on delete no action
  on update no action;


-- Link previous bills related admissions (using admission and discharge date)
drop procedure if exists link_bill_admission;
delimiter //
  create procedure link_bill_admission()
  begin

	declare done int;
	declare $adm_id int;
	declare $adm_pat_id int;
	declare $adm_date_adm datetime;
	declare $adm_date_dis datetime;
    
	declare cur1 cursor for select adm_id, adm_pat_id, adm_date_adm, adm_date_dis from oh_admission where adm_deleted = 'N';
	declare continue handler for not found set done = 1;
    
	open cur1;
	  read_loop: loop
		fetch cur1 into $adm_id, $adm_pat_id, $adm_date_adm, $adm_date_dis;
		if done = 1 then
			leave read_loop;
		end if;
        
		-- select $adm_id, $adm_pat_id, $adm_date_adm, $adm_date_dis;
		if $adm_date_dis IS not null then
			-- select "Discharged";
			update oh_bills set bll_adm_id = $adm_id where bll_id_pat = $adm_pat_id and bll_date between $adm_date_adm and $adm_date_dis;
		else
			-- select "Current admission";
			update oh_bills set bll_adm_id = $adm_id where bll_id_pat = $adm_pat_id and bll_date >= $adm_date_adm;
		end if;
	  end loop;
	close cur1;
  end //
delimiter ;

call link_bill_admission();