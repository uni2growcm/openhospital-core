alter table hospital add column hos_visit_start time not null default '06:30:00' after hos_curr_cod;
alter table hospital add column hos_visit_end time not null default '20:00:00' after hos_visit_start;
alter table hospital add column hos_visit_increment int(11) not null default 15 after hos_visit_end;
alter table hospital add column hos_visit_duration int(11) not null default 30 after hos_visit_increment;