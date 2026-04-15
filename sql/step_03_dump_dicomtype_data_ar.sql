delete from oh_dicom;
delete from oh_dicomtype;

-- dicomtype
load data local infile './data_ar/dicomtype.csv'
	into table oh_dicomtype
	fields terminated by ';'
	lines terminated by '\n';
