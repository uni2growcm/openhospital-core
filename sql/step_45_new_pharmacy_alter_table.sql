alter table medicaldsrstockmov add column mmv_refno varchar(50) not null default ''  after mmv_lock;
-- porting previous data
update medicaldsrstockmov AS t
inner join 
(select distinct(mmv_mmvt_id_a) AS type from medicaldsrstockmov) AS t1 
on t.mmv_mmvt_id_a = t1.type 
set mmv_refno = concat("Auto-Refno-", mmv_mmvt_id_a);

insert into menuitem values ('btnpharmstockcharge','angal.menu.btn.btnpharmstockcharge','angal.menu.btnpharmstockcharge','x','C','medicalstock','none','N', 1);
insert into groupmenu (gm_ug_id_a, gm_mni_id_a, gm_active) values ('admin','btnpharmstockcharge','Y');

insert into menuitem values ('btnpharmstockdischarge','angal.menu.btn.btnpharmstockdischarge','angal.menu.btnpharmstockdischarge','x','D','medicalstock','none','N', 2);
insert into groupmenu (gm_ug_id_a, gm_mni_id_a, gm_active) values ('admin','btnpharmstockdischarge','Y');