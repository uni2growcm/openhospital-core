drop table if exists supplier;
create table supplier (
	sup_id int(11) not null auto_increment,
	sup_name varchar(100) not null,
	sup_address varchar(150) null,
	sup_taxcode varchar(50) null,
	sup_phone varchar(20) null,
	sup_fax varchar(20) null,
	sup_email varchar(100) null,
	sup_note varchar(200) null,
	sup_deleted char(1) default 'N',
	primary key (sup_id)
);

insert into menuitem values ('supplier', 'angal.menu.btn.supplier', 'angal.menu.supplier', 'x', 'S', 'generaldata', 'org.isf.supplier.gui.SupplierBrowser','N', 8);
insert into groupmenu (gm_ug_id_a, gm_mni_id_a, gm_active) values ('admin','supplier','Y');

-- Collecting supplier old informations
insert into supplier (sup_name) select distinct(mmv_from) from medicaldsrstockmov where mmv_from IS not null;

-- Updating medicaldsrstockmov table with new codes
update medicaldsrstockmov join supplier on mmv_from = sup_name set mmv_from = sup_id;
-- update medicaldsrstockmov set mmv_from = 0 where mmv_from IS null;

-- Altering medicaldsrstockmov table to reflect codes type
alter table medicaldsrstockmov change column mmv_from mmv_from int(11) null default null; 