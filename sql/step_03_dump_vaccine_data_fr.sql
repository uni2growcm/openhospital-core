delete from oh_vaccine;
delete from oh_vaccinetype;

-- vaccinetype
load data local infile './data_fr/vaccinetype.csv'
	into table oh_vaccinetype
	fields terminated by ';' 
	lines terminated by '\n';
	
-- vaccine
load data local infile './data_fr/vaccine.csv'
	into table oh_vaccine
	fields terminated by ';' 
	lines terminated by '\n';