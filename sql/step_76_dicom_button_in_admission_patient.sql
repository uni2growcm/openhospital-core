-- This script adds a "link button" to the dicom functionality in the Admission/Patient window

-- add dicom button in Admission/Patient
insert into oh_menuitem values ('btnadmdicom','angal.menu.btn.dicom','angal.menu.dicom','x','L','admission','none','N',4);
insert into oh_groupmenu (gm_ug_id_a, gm_mni_id_a, gm_active) values ('admin','btnadmdicom',1);
