alter table ward add column wrd_is_male tinyint(1) not null default '1' after wrd_is_pharmacy, 
	add column wrd_is_female tinyint(1) not null default '1' after wrd_is_male;
update ward set wrd_is_male = 0 where wrd_id_a = 'M';
