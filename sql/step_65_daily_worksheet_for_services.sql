-- alter visits
alter table visits
	add column vst_wrd_id_a char(1) default null,
	add column vst_duration int(11) default null,
	add column vst_service varchar(45) default null;
	
alter table visits
	add constraint fk_visits_ward foreign key (vst_wrd_id_a) references ward (wrd_id_a) on delete no action on update no action;

-- create menus
update menuitem set mni_position = '6' where (mni_id_a = 'accounting');
update menuitem set mni_position = '7' where (mni_id_a = 'patientvaccine');
update menuitem set mni_position = '9' where (mni_id_a = 'printing');
update menuitem set mni_position = '11' where (mni_id_a = 'help');
update menuitem set mni_position = '10' where (mni_id_a = 'communication');
update menuitem set mni_shortcut = 'M' where (mni_id_a = 'communication');

insert into menuitem values ('worksheet','angal.menu.btn.worksheet','angal.menu.worksheet','x','W','main','org.isf.visits.gui.VisitView','N',8);
insert into groupmenu (gm_ug_id_a, gm_mni_id_a, gm_active) values ('admin','worksheet',1);

