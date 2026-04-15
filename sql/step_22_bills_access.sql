insert into menuitem values ('btnbillnew','angal.billbrowser.newbill.btn','angal.billbrowser.newbill.btn','x','N','billsmanager','none','N',0);
insert into menuitem values ('btnbilledit','angal.billbrowser.editbill','angal.billbrowser.editbill','x','N','billsmanager','none','N',1);
insert into menuitem values ('btnbilldelete','angal.billbrowser.deletebill.btn','angal.billbrowser.deletebill.btn','x','N','billsmanager','none','N',2);
insert into menuitem values ('btnbillreport','angal.billbrowser.report','angal.billbrowser.report','x','N','billsmanager','none','N',3);

insert into groupmenu (gm_ug_id_a, gm_mni_id_a, gm_active) values ('admin','btnbillnew','Y');
insert into groupmenu (gm_ug_id_a, gm_mni_id_a, gm_active) values ('admin','btnbilledit','Y');
insert into groupmenu (gm_ug_id_a, gm_mni_id_a, gm_active) values ('admin','btnbilldelete','Y');
insert into groupmenu (gm_ug_id_a, gm_mni_id_a, gm_active) values ('admin','btnbillreport','Y');
