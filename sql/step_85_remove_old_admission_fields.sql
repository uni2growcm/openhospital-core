alter table oh_admission 
drop foreign key fk_admission_operation;

alter table oh_admission 
drop column adm_resop,
drop column adm_date_op,
drop column adm_ope_id_a,
drop index fk_admission_operation;
