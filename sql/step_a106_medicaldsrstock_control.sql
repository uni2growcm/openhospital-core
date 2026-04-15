-- add category to movementstock types: 'operational' or 'non-operational'
alter table oh_medicaldsrstockmovtype add column mmvt_category varchar(15) not null default 'operational' after `mmvt_type`;

-- create new table for balances history
create table oh_medicaldsrstock (
  ms_id int(11) not null auto_increment,
  ms_mdsr_id int(11) not null,
  ms_date_balance date not null,
  ms_balance int(11) not null,
  ms_date_next_mov date default null,
  ms_days int(11) default null,
  ms_created_by varchar(50) null default null,
  ms_created_date datetime null default null,
  ms_last_modified_by varchar(50) null default null,
  ms_last_modified_date datetime null default null,
  ms_active tinyint(1) not null default 1,
  primary key (ms_id),
  unique key ms_mdsr_id_date_balance_unique (ms_mdsr_id,ms_date_balance),
  key FK_MEDICALDSRSTOCK_MEDICALDSR_idx (ms_mdsr_id),
  constraint fk_medicaldsrstock_medicaldsr 
	foreign key (ms_mdsr_id) 
	references oh_medicaldsr (mdsr_id) 
    on delete no action 
    on update no action);
    
drop procedure if exists populateMSTable;
delimiter //
    
create procedure populateMSTable()
begin
	declare v_ms_id int;
    declare v_mdsr_id int;
    declare v_date date;
    declare v_balance int;
    declare v_qty int;
    declare no_more_rows boolean default false;
	
	declare curMovement cursor for
			select mmv_mdsr_id, date(mmv_date), sum(if(mmvt_type like '+', mmv_qty, -mmv_qty)) AS qty
            from oh_medicaldsrstockmov
            left join oh_medicaldsrstockmovtype on mmvt_id_a = mmv_mmvt_id_a
            group by date(mmv_date), mmv_mdsr_id;
			
	declare continue handler for not found set no_more_rows := true;
	-- declare exit handler for 1048 select v_mdsr_id; -- needed for handling errors
    
	open curMovement;
	movement_loop: loop
		fetch curmovement into v_mdsr_id, v_date, v_qty;
		if no_more_rows then
            close curMovement;
			leave movement_loop;
		end if;
        
        set v_ms_id = null;
        set v_balance = null;
        
        select max(ms_id)
			from oh_medicaldsrstock
            where ms_mdsr_id = v_mdsr_id
            and ms_date_balance < v_date into v_ms_id;
            
            
		if v_qty <> 0 then
			if v_ms_id IS not null then
				update oh_medicaldsrstock set 
					ms_date_next_mov = v_date,
					ms_days = datediff(v_date, ms_date_balance)
					where ms_id = v_ms_id;
				select ms_balance from oh_medicaldsrstock where ms_id = v_ms_id into v_balance;
				set v_balance = v_balance + v_qty;
                insert into oh_medicaldsrstock values (0, v_mdsr_id, v_date, v_balance, null, null, 'admin', null, 'admin', null, 1);
			else set v_balance = v_qty;
                insert into oh_medicaldsrstock values (0, v_mdsr_id, v_date, v_qty, null, null, 'admin', null, 'admin', null, 1);
			end if;
            
		end if;
        
	end loop movement_loop;
	
end; //
delimiter ;

call populateMSTable();