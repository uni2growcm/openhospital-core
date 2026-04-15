-- add agetype field

alter table patient add column pat_agetype varchar(50) not null default '' after pat_age;

--
-- Definition of table agetype
--

drop table if exists agetype;
create table agetype (
  at_code varchar(4) not null default '',
  at_from int(11) not null default 0,
  at_to int(11) not null default 0,
  at_desc varchar(100) character set utf8 collate utf8_unicode_ci not null default '',
  primary key (at_code)
) engine=MyISAM;

--
-- Dumping default data for table agetype
--

/*!40000 alter table agetype disable keys */;
lock tables agetype write;
insert into agetype values  ('d0',0,0,'angal.agetype.newborn'),
 ('d1',1,5,'angal.agetype.earlychildhood'),
 ('d2',6,12,'angal.agetype.latechildhood'),
 ('d3',13,24,'angal.agetype.adolescents'),
 ('d4',25,59,'angal.agetype.adult'),
 ('d5',60,99,'angal.agetype.elderly');
unlock tables;
/*!40000 alter table agetype enable keys */;


insert into menuitem values ('agetype', 'angal.menu.btn.agetype', 'angal.menu.agetype', 'x', 'G', 'types', 'org.isf.agetype.gui.AgeTypeBrowser','N', 11);

insert into groupmenu (gm_ug_id_a, gm_mni_id_a, gm_active) values ('admin','agetype','Y');

