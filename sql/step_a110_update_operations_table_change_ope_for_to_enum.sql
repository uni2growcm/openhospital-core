alter table oh_operation modify column ope_for varchar(20);

update oh_operation set ope_for='opd_admission' where ope_for='1';
update oh_operation set ope_for='admission' where ope_for='2';
update oh_operation set ope_for='opd' where ope_for='3';

alter table oh_operation
    modify column ope_for enum('opd_admission', 'admission', 'opd') default 'opd_admission';