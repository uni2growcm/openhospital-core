-- change pat_bdate from varchar(50) to date
-- Needed conversion from lonG (old values) to date
alter table patient add temp_bdate date after pat_name;

update patient set temp_bdate = case pat_bdate 
when null then null 
when "" then null
when "-" then null
else date(from_unixtime(pat_bdate/1000)) end;

alter table patient drop pat_bdate;

alter table patient change temp_bdate pat_bdate date;