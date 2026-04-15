drop table if exists oh_telemetry;
create table oh_telemetry (
  -- identification
  tel_uuid varchar(36) not null comment 'Software ID',
  tel_dbid varchar(36) not null comment 'Database ID',
  tel_hwid varchar(36) not null comment 'Hardware ID',
  tel_osid varchar(36) not null comment 'Operating System ID',
  -- settings
  tel_active tinyint(1) comment 'true|false|null',
  tel_consent text comment 'User consent informations',
  -- history
  tel_info text comment 'Last collected data',
  tel_sent_time datetime comment 'Timestamp when message sent',
  tel_optin_date datetime comment 'when user enables telemetry',
  tel_optout_date datetime comment 'when user disables telemetry',
  primary key (tel_uuid,tel_dbid,tel_hwid,tel_osid)
) engine=MyISAM;

insert into oh_menuitem values ('telemetry', 'angal.menu.btn.telemetry', 'angal.menu.telemetry', 'x', 'M', 'generaldata', 'org.isf.telemetry.gui.TelemetryEdit','N', 9);
insert into oh_groupmenu (gm_ug_id_a, gm_mni_id_a, gm_active) values ('admin','telemetry',1);
