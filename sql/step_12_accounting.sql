--
-- Definition of table billitems
--

drop table if exists billitems;
create table  billitems (
  bli_id int(11) not null auto_increment,
  bli_id_bill int(11) default null,
  bli_is_price tinyint(1) not null,
  bli_id_price varchar(10) default null,
  bli_item_desc varchar(100) default null,
  bli_item_amount double not null,
  bli_qty int(11) not null,
  primary key (bli_id)
) engine=MyISAM;

--
-- Definition of table billpayments
--

drop table if exists billpayments;
create table  billpayments (
  blp_id int(11) not null auto_increment,
  blp_id_bill int(11) default null,
  blp_date datetime not null,
  blp_amount double not null,
  primary key (blp_id)
) engine=MyISAM;

--
-- Definition of table bills
--

drop table if exists bills;
create table  bills (
  bll_id int(11) not null auto_increment,
  bll_date datetime not null,
  bll_update datetime not null,
  bll_is_lst tinyint(1) not null,
  bll_id_lst int(11) default null,
  bll_lst_name varchar(50) default null,
  bll_is_pat tinyint(1) not null,
  bll_id_pat int(11) default null,
  bll_pat_name varchar(100) default null,
  bll_status varchar(1) default null,
  bll_amount double default null,
  bll_balance double default null,
  primary key (bll_id)
) engine=MyISAM;

insert into menuitem values ('accounting', 'angal.menu.btn.accounting', 'angal.menu.accounting', 'x', 'C', 'main', 'none','Y', 5);

insert into menuitem values ('newbill', 'angal.menu.btn.newbill', 'angal.menu.newbill', 'x', 'N', 'accounting', 'org.isf.accounting.gui.PatientBillEdit','N', 0);

insert into menuitem values ('billsmanager', 'angal.menu.btn.billsmanager', 'angal.menu.billsmanager', 'x', 'M', 'accounting', 'org.isf.accounting.gui.BillBrowser','N', 1);

-- Functionalities initially disabled. Put 'Y' to activate them

insert into groupmenu (gm_ug_id_a, gm_mni_id_a, gm_active) values ('admin','accounting','Y');

insert into groupmenu (gm_ug_id_a, gm_mni_id_a, gm_active) values ('admin','newbill','Y');

insert into groupmenu (gm_ug_id_a, gm_mni_id_a, gm_active) values ('admin','billsmanager','Y');

