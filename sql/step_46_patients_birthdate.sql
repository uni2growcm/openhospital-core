-- bdate from agetype
-- select pat_timestamp, pat_bdate, pat_age, pat_agetype, substring(pat_agetype, locate('/', pat_agetype)+1) AS months, date((date_sub(pat_timestamp, interval substring(pat_agetype, locate('/', pat_agetype)+1) month))) AS bdate 
-- from patient
-- where pat_timestamp > 0
-- and pat_id > 0
-- and pat_agetype not like "" 
-- and pat_bdate IS null;

update patient set pat_bdate = date((date_sub(pat_timestamp, interval substring(pat_agetype, locate('/', pat_agetype)+1) month))), pat_agetype = ""
where pat_timestamp > 0
and pat_id > 0
and pat_agetype not like "" 
and pat_bdate IS null;

-- bdate from age
-- select pat_timestamp, pat_bdate, pat_age, date(date_sub(pat_timestamp, interval pat_age year)) AS bdate
-- from patient
-- where pat_timestamp > 0
-- and pat_id > 0
-- and pat_bdate IS null;

update patient set pat_bdate = date(date_sub(pat_timestamp, interval pat_age year))
where pat_timestamp > 0
and pat_id > 0
and pat_bdate IS null;

-- age from bdate
-- select pat_timestamp, pat_bdate, pat_age, pat_agetype, timestampdiff(year, pat_bdate, curdate()) AS age
-- from patient
-- where pat_timestamp > 0
-- and pat_id > 0
-- and pat_bdate IS not null;

update patient set pat_age = timestampdiff(year, pat_bdate, curdate())
where pat_timestamp > 0
and pat_id > 0
and pat_bdate IS not null;