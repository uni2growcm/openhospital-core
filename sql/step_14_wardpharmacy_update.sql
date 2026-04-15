alter table medicaldsrstockmovward add column mmvn_is_patient tinyint(1)  not null after mmvn_date,
 add column mmvn_desc varchar(100)  not null after mmvn_pat_id;

