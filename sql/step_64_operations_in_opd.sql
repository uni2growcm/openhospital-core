alter table operation
add column ope_for char(1) default '1' comment "'1' = opd/ipd, '2' = ipd only, '3' = opd only" after ope_stat;

insert into menuitem values ('btnopdnewoperation','angal.opd.operation','angal.opd.operation','x','A','btnopdnew','none','N',2);
insert into menuitem values ('btnopdeditoperation','angal.opd.operation','angal.opd.operation','x','A','btnopdedit','none','N',2);

insert into groupmenu (gm_ug_id_a, gm_mni_id_a, gm_active) values ('admin','btnopdnewoperation',1);
insert into groupmenu (gm_ug_id_a, gm_mni_id_a, gm_active) values ('admin','btnopdeditoperation',1);
