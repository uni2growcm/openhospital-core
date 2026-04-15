delimiter //

drop procedure if exists moveOperationsToNewTable;
create procedure moveOperationsToNewTable()
begin
	declare v_adm_id int(11);
	declare v_adm_user varchar(50);
    declare v_ope_id varchar(10);
	declare v_ope_date datetime;
	declare v_ope_result varchar(10);
	declare v_trans float;
    declare done int default false;
    
    declare cur cursor for select adm_id, adm_usr_id_a, adm_ope_id_a, adm_date_op, 
    						case coalesce(adm_resop, 'U') when 'P' then 'success' when 'N' then 'failure' when 'U' then 'unknown' end AS adm_resop, adm_trans
							from admission
							where adm_ope_id_a IS not null;
							
    declare continue handler for not found set done = 1;

    open cur;
    read_loop: loop
        fetch cur into v_adm_id, v_adm_user, v_ope_id, v_ope_date, v_ope_result, v_trans;
        if done then
            leave read_loop;
        end if;
        
        insert into operationrow set oper_id = v_ope_id, 
        						 oper_prescriber = v_adm_user,
        						 oper_result = v_ope_result,
        						 oper_opdate = v_ope_date,
        						 oper_admission_id = v_adm_id,
        						 oper_trans_unit = v_trans;
    end loop;
  	close cur;
end; //

delimiter ;

create table operationrow(
	oper_id_a int (11) not null auto_increment,
	oper_id varchar (11) not null,
	oper_prescriber varchar (150) not null,
	oper_result varchar (250) not null,
	oper_opdate datetime not null,
	oper_remarks varchar (250) not null,
	oper_admission_id int(11) default null, 
	oper_opd_id int(11) default null,
	oper_bill_id int(11) default null,
	oper_trans_unit float null default 0,	
	primary key (oper_id_a)
);

call moveOperationsToNewTable();