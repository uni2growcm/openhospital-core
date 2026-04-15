-- alterazione tabelle
alter table pricesothers 
add column oth_discharge int(11) null default '0' after oth_daily, 
add column oth_undefined int(11) null default '0' after oth_discharge;