drop procedure if exists dump_image;
delimiter //
  create procedure dump_image()
  begin

	declare done int;
	declare this_id int;
	declare cur1 cursor for select pat_profile_photo_id from oh_patient where pat_profile_photo_id IS not null;
	declare continue handler for not found set done = 1;
	open cur1;
	  read_loop: loop
		fetch cur1 into this_id;
		if done = 1 then
			leave read_loop;
		end if;
		set @query = concat('select pat_photo from oh_patient_profile_photo where pat_profile_photo_id=', this_id, ' into dumpfile "oh_path_substitute/photo_dir', this_id,'.png"');
		prepare write_file from @query;
		execute write_file;
	  end loop;
	close cur1;
  end //
delimiter ;

call dump_image();