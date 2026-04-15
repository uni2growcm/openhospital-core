--
-- Definition of table pricelists
--

drop table if exists pricelists;
create table  pricelists (
  lst_id int(11) not null auto_increment,
  lst_code varchar(7) not null,
  lst_name varchar(50) not null,
  lst_desc varchar(100) not null,
  lst_currency varchar(10) not null,
  primary key  (lst_id)
) engine=MyISAM;

--
-- Dumping data for table pricelists
--

lock tables pricelists write;
insert into pricelists (lst_code, lst_name, lst_desc, lst_currency) values  ('list001','Basic','Basic price list','');
unlock tables;

--
-- Definition of table prices
--

drop table if exists prices;
create table  prices (
  prc_id int(11) not null auto_increment,
  prc_lst_id int(11) not null,
  prc_grp char(3) not null,
  prc_item varchar(10) not null,
  prc_desc varchar(100) not null,
  prc_price double not null,
  primary key  (prc_id)
) engine=MyISAM;

--
-- Definition of table pricesothers
--

drop table if exists pricesothers;
create table  pricesothers (
  oth_id int(11) not null auto_increment,
  oth_code varchar(10) not null,
  oth_desc varchar(100) not null,
  oth_opd_include int(11) not null default '0',
  oth_ipd_include int(11) not null default '0',
  oth_daily int(11) not null default '0',
  primary key  (oth_id)
) engine=MyISAM;

--
-- Dumping data for table pricesothers
--

lock tables pricesothers write;
insert into pricesothers (oth_code, oth_desc, oth_opd_include, oth_ipd_include, oth_daily) values  ('oth001','Amount per day',0,1,1);
unlock tables;

--
-- Dumping data for table prices
--

insert into prices (prc_lst_id, prc_grp, prc_item, prc_desc, prc_price) select 1, 'exa', exa_id_a, exa_desc, 0 from exam order by exa_desc;

insert into prices (prc_lst_id, prc_grp, prc_item, prc_desc, prc_price) select 1, 'ope', ope_id_a, ope_desc, 0 from operation order by ope_desc;

insert into prices (prc_lst_id, prc_grp, prc_item, prc_desc, prc_price) select 1, 'med', mdsr_id, mdsr_desc, 0 from medicaldsr order by mdsr_desc;

insert into prices (prc_lst_id, prc_grp, prc_item, prc_desc, prc_price) select 1, 'oth', oth_id, oth_desc, 0 from pricesothers order by oth_desc;

-- Menu items

insert into menuitem values ('priceslists', 'angal.menu.btn.priceslists', 'angal.menu.priceslists', 'x', 'P', 'generaldata', 'org.isf.priceslist.gui.PricesBrowser','N', 7);

insert into menuitem values ('otherprices', 'angal.menu.btn.otherprices', 'angal.menu.otherprices', 'x', 'H', 'types', 'org.isf.pricesothers.gui.PricesOthersBrowser','N', 10);

-- Admin grants

insert into groupmenu (gm_ug_id_a, gm_mni_id_a, gm_active) values ('admin','priceslists','Y');

insert into groupmenu (gm_ug_id_a, gm_mni_id_a, gm_active) values ('admin','otherprices','Y');
