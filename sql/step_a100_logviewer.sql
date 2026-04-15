-- change Help button into a submenu
update oh_menuitem set mni_class = 'none', mni_is_submenu = 'Y' where (mni_id_a = 'help');

-- add submenu items
insert into oh_menuitem (mni_id_a, mni_btn_label, mni_label, mni_tooltip, mni_shortcut, mni_submenu, mni_class, mni_is_submenu, mni_position) values ('doc', 'angal.menu.btn.doc', 'angal.menu.doc', 'x', 'D', 'help', 'org.isf.help.HelpViewer', 'N', '1');
insert into oh_menuitem (mni_id_a, mni_btn_label, mni_label, mni_tooltip, mni_shortcut, mni_submenu, mni_class, mni_is_submenu, mni_position) values ('logfile', 'angal.menu.btn.logfile', 'angal.menu.logfile', 'x', 'L', 'help', 'org.isf.help.LogViewer', 'N', '2');

-- add admin permissions
insert into oh_groupmenu (gm_ug_id_a, gm_mni_id_a, gm_active) values ('admin', 'doc', '1');
insert into oh_groupmenu (gm_ug_id_a, gm_mni_id_a, gm_active) values ('admin', 'logfile', '1');
