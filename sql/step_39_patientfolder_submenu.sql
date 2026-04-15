update menuitem set mni_is_submenu='Y' where mni_id_a='btnadmpatientfolder';

insert into menuitem values ('btnpatfoldopdrpt', 'angal.menu.btn.opdchart', 'angal.menu.opdchart', 'x', 'O', 'btnadmpatientfolder', 'none','N', 1);
insert into groupmenu (gm_ug_id_a, gm_mni_id_a, gm_active) values ('admin','btnpatfoldopdrpt','Y');

insert into menuitem values ('btnpatfoldadmrpt', 'angal.menu.btn.admchart', 'angal.menu.admchart', 'x', 'A', 'btnadmpatientfolder', 'none','N', 2);
insert into groupmenu (gm_ug_id_a, gm_mni_id_a, gm_active) values ('admin','btnpatfoldadmrpt','Y');

insert into menuitem values ('btnpatfoldpatrpt', 'angal.menu.btn.patreport', 'angal.menu.patreport', 'x', 'R', 'btnadmpatientfolder', 'none','N', 3);
insert into groupmenu (gm_ug_id_a, gm_mni_id_a, gm_active) values ('admin','btnpatfoldpatrpt','Y');

insert into menuitem values ('btnpatfolddicom', 'angal.menu.btn.dicom', 'angal.menu.dicom', 'x', 'D', 'btnadmpatientfolder', 'none','N', 4);
insert into groupmenu (gm_ug_id_a, gm_mni_id_a, gm_active) values ('admin','btnpatfolddicom','Y');
