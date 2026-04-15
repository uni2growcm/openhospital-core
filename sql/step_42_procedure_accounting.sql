delimiter //

drop procedure if exists calcAmount;
create procedure calcAmount()
begin
    declare v_id int;
	declare v_amount double;
    declare done int default false;
    declare cur cursor for select bll_id, amount from 
							(select bll_id, bll_amount, sum(bli_item_amount * bli_qty) AS amount
							from bills join billitems on bll_id = bli_id_bill
							where bli_item_amount > 0
							group by bll_id
							order by bll_id) bills
							where bll_amount <> amount;
    declare continue handler for not found set done = 1;

    open cur;
    read_loop: loop
        fetch cur into v_id, v_amount;
        if done then
            leave read_loop;
        end if;
        update bills set bll_amount = v_amount where bll_id = v_id;
    end loop;
  close cur;

end; //
delimiter ;

call calcAmount();