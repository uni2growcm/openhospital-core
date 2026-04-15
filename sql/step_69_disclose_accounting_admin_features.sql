update menuitem set mni_position='1' where mni_id_a='btnbillnew';
update menuitem set mni_position='2' where mni_id_a='btnbilledit';
update menuitem set mni_position='4' where mni_id_a='btnbilldelete';
update menuitem set mni_position='5' where mni_id_a='btnbillreport';
update menuitem set mni_position='6' where mni_id_a='btnbillreceipt';

insert into menuitem (mni_id_a, mni_btn_label, mni_label, mni_tooltip, mni_shortcut, mni_submenu, mni_class, mni_is_submenu, mni_position) values ('cashiersfilter', 'angal.menu.accounting.cashiersfilter', 'angal.menu.accounting.cashiersfilter', 'x', 'X', 'billsmanager', 'none', 'N', '0');
insert into menuitem (mni_id_a, mni_btn_label, mni_label, mni_tooltip, mni_shortcut, mni_submenu, mni_class, mni_is_submenu, mni_position) values ('editclosedbills', 'angal.menu.accounting.editclosedbills', 'angal.menu.accounting.editclosedbills', 'x', 'E', 'billsmanager', 'none', 'N', '3');

insert into groupmenu (gm_ug_id_a, gm_mni_id_a, gm_active) values ('admin', 'cashiersfilter', 1);
insert into groupmenu (gm_ug_id_a, gm_mni_id_a, gm_active) values ('admin', 'editclosedbills', 1);
