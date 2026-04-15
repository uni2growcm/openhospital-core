alter table medicaldsrward change column mdsrwrd_in_qti mdsrwrd_in_qti float null default '0', change column mdsrwrd_out_qti mdsrwrd_out_qti float null default '0';

insert into menuitem values ('btnmedicalswardrectify','angal.menu.btn.btnmedicalswardrectify','angal.menu.btnmedicalswardrectify','x','R','medicalsward','none','N',1);
insert into groupmenu (gm_ug_id_a, gm_mni_id_a, gm_active) values ('admin','btnmedicalswardrectify','Y');
