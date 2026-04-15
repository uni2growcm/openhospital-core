alter table oh_laboratory add column lab_status varchar(7) null default 'draft'; -- draft, open, done
update oh_laboratory set lab_status = 'done'; -- for previous data