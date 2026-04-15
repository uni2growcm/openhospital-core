-- version 10-06-2014
delete from hospital;
delete from medicaldsr;
delete from medicaldsrtype;
delete from medicaldsrstockmovtype;
delete from deliverytype;
delete from deliveryresulttype;
delete from pregnanttreatmenttype;
delete from examrow;
delete from exam;
delete from examtype;
delete from operation;
delete from operationtype;
delete from disease;
delete from diseasetype;
delete from vaccine;
delete from admissiontype;
delete from dischargetype;
delete from ward;

-- hospital
insert into hospital (hos_id_a,hos_name,hos_addr,hos_city,hos_tele,hos_fax,hos_email,hos_lock) values 
 ('stluke','St. Luke hospital - Angal','P.O. box 85 - nebbi','angal','+256 0472621076','+256 0','angal@ucmb.ug.co.',0);

-- medicaldsrtype
load data local infile './data_sq/medicaldsrtype.csv'
	into table medicaldsrtype 
	fields terminated by ';' 
	lines terminated by '\n';

-- medicaldsrstockmovtype
load data local infile './data_sq/medicaldsrstockmovtype.csv'
	into table medicaldsrstockmovtype 
	fields terminated by ';' 
	lines terminated by '\n';

-- deliverytype
load data local infile './data_sq/deliverytype.csv'
	into table deliverytype 
	fields terminated by ';' 
	lines terminated by '\n';

-- deliveryresulttype
load data local infile './data_sq/deliveryresulttype.csv'
	into table deliveryresulttype 
	fields terminated by ';' 
	lines terminated by '\n';

-- pregnanttreatmenttype
load data local infile './data_sq/pregnanttreatmenttype.csv'
	into table pregnanttreatmenttype 
	fields terminated by ';' 
	lines terminated by '\n';

-- examtype
load data local infile './data_sq/examtype.csv'
	into table examtype 
	fields terminated by ';' 
	lines terminated by '\n';

-- exam
load data local infile './data_sq/exam.csv'
	into table exam 
	fields terminated by ';' 
	lines terminated by '\n';

-- examrow
load data local infile './data_sq/examrow.csv'
	into table examrow 
	fields terminated by ';' 
	lines terminated by '\n';

-- operationtype
load data local infile './data_sq/operationtype.csv'
	into table operationtype 
	fields terminated by ';' 
	lines terminated by '\n';

-- vaccine
load data local infile './data_sq/vaccine.csv'
	into table vaccine 
	fields terminated by ';' 
	lines terminated by '\n';

-- admissiontype
load data local infile './data_sq/admissiontype.csv'
	into table admissiontype 
	fields terminated by ';' 
	lines terminated by '\n';

-- dischargetype
load data local infile './data_sq/dischargetype.csv'
	into table dischargetype 
	fields terminated by ';' 
	lines terminated by '\n';

-- diseasetype
load data local infile './data_sq/diseasetype.csv'
	into table diseasetype 
	fields terminated by ';' 
	lines terminated by '\n';

-- disease
load data local infile './data_sq/disease.csv'
	into table disease 
	fields terminated by ';' 
	lines terminated by '\n';

-- operation
load data local infile './data_sq/operation.csv'
	into table operation 
	fields terminated by ';' 
	lines terminated by '\n';

-- medicaldsr
load data local infile './data_sq/medicaldsr.csv'
	into table medicaldsr 
	fields terminated by ';' 
	lines terminated by '\n';

-- ward
load data local infile './data_sq/ward.csv'
	into table ward 
	fields terminated by ';' 
	lines terminated by '\n';
