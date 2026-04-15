-- This script adds a "link button" to the Laboratory in the Admission/Patient window

-- add Laboratory button in Admission/Patient
insert into oh_menuitem values ('btnadmlab','angal.menu.btn.laboratory','angal.menu.laboratory','x','L','admission','none','N',3);
insert into oh_groupmenu (gm_ug_id_a, gm_mni_id_a, gm_active) values ('admin','btnadmlab',1);
