create table oh_medicaldsrinventory (
	minvt_id int not null auto_increment,
	minvt_status varchar (10)  not null,
	minvt_date datetime not null,
	minvt_us_id_a varchar (50) not null,
	minvt_reference varchar (50) not null,
	minvt_type varchar(30) not null,
	minvt_wrd_id_a char(3) null,
	minvt_charge_type varchar(10) null,
	minvt_discharge_type varchar(10) null,
	minvt_supplier int null,
	minvt_destination char(3) null,
	minvt_lock int not null default 0,
	minvt_created_by varchar(50) null default null,
  	minvt_created_date datetime null default null,
  	minvt_last_modified_by varchar(50) null default null,
  	minvt_last_modified_date datetime null default null,
  	minvt_active tinyint(1) not null default 1,
	primary key (minvt_id ),
	foreign key (minvt_us_id_a) references oh_user (us_id_a),
	foreign key (minvt_wrd_id_a) references oh_ward (wrd_id_a),
	foreign key (minvt_charge_type) references oh_medicaldsrstockmovtype (mmvt_id_a),
	foreign key (minvt_discharge_type) references oh_medicaldsrstockmovtype (mmvt_id_a),
	foreign key (minvt_supplier) references oh_supplier (sup_id),
	foreign key (minvt_destination) references oh_ward (wrd_id_a)
) engine = innodb default character set utf8;

create table oh_medicaldsrinventoryrow (
	minvtr_id int not null auto_increment,
	minvtr_theoretic_qty float  not null default 0,
	minvtr_real_qty float  not null default 0,
	minvtr_invt_id int not null,
	minvtr_mdsr_id int not null,
	minvtr_lt_id_a varchar (50) null,
	minvtr_is_new_lot tinyint(1) not null default 0,
	minvtr_lock int not null default 0,
	minvtr_created_by varchar(50) null default null,
  	minvtr_created_date datetime null default null,
  	minvtr_last_modified_by varchar(50) null default null,
  	minvtr_last_modified_date datetime null default null,
  	minvtr_active tinyint(1) not null default 1,
	primary key (minvtr_id ),
	foreign key (minvtr_invt_id) references oh_medicaldsrinventory (minvt_id),
	foreign key (minvtr_mdsr_id) references oh_medicaldsr (mdsr_id),
	foreign key (minvtr_lt_id_a) references oh_medicaldsrlot (lt_id_a)
) engine = innodb default character set utf8;

insert into oh_menuitem (mni_id_a, mni_btn_label, mni_label, mni_tooltip, mni_shortcut, mni_submenu, mni_class, mni_is_submenu, mni_position) values ('inventoryward','angal.menu.btn.inventoryward','angal.menu.inventoryward','x','D','pharmacy','org.isf.medicalinventory.gui.InventoryWardBrowser','N',6);
insert into oh_groupmenu (gm_id, gm_ug_id_a, gm_mni_id_a, gm_active, gm_created_by, gm_created_date, gm_last_modified_by, gm_last_modified_date) values (346,'admin','inventoryward',1,null,null,null,null);
insert into oh_menuitem (mni_id_a, mni_btn_label, mni_label, mni_tooltip, mni_shortcut, mni_submenu, mni_class, mni_is_submenu, mni_position) values ('inventory','angal.menu.btn.inventory','angal.menu.invertory','x','I','pharmacy','org.isf.medicalinventory.gui.InventoryBrowser','N',5);
insert into oh_groupmenu (gm_id, gm_ug_id_a, gm_mni_id_a, gm_active, gm_created_by, gm_created_date, gm_last_modified_by, gm_last_modified_date) values (345,'admin','inventory',1,null,null,null,null);

