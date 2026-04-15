alter table malnutritioncontrol add column mln_pat_id int(11) null default null after mln_adm_id, change column mln_adm_id mln_adm_id int(11) null default null;
alter table medicaldsrstockmovward add column mmvn_pat_age smallint not null after mmvn_pat_id;
alter table medicaldsrstockmovward add column mmvn_pat_weight float not null after mmvn_pat_age;
update medicaldsrstockmovward set mmvn_pat_age = (select pat_age from patient where patient.pat_id = medicaldsrstockmovward.mmvn_pat_id) where exists (select 1 from patient where patient.pat_id = medicaldsrstockmovward.mmvn_pat_id);

drop table if exists medicaldsrward;
create table medicaldsrward (
  mdsrwrd_wrd_id_a char(1) not null,
  mdsrwrd_mdsr_id int(11) not null,
  mdsrwrd_in_qti float,
  mdsrwrd_out_qti float,
  primary key (mdsrwrd_wrd_id_a, mdsrwrd_mdsr_id)
);

insert into medicaldsrward(mdsrwrd_wrd_id_a, mdsrwrd_mdsr_id, mdsrwrd_in_qti, mdsrwrd_out_qti)
select mmv_wrd_id_a AS ward, mmv_mdsr_id AS med, sum(mmv_qty), 0 AS total from medicaldsrstockmov where mmv_mmvt_id_a like "discharge" and mmv_wrd_id_a is not null group by mmv_wrd_id_a, mmv_mdsr_id;
  
insert into medicaldsrward(mdsrwrd_wrd_id_a, mdsrwrd_mdsr_id, mdsrwrd_out_qti)
select out_qti.ward, out_qti.med, out_qti.qti from
(select mmvn_wrd_id_a AS ward, mmvn_mdsr_id AS med, sum(mmvn_mdsr_qty) AS qti from medicaldsrstockmovward group by mmvn_wrd_id_a, mmvn_mdsr_id) AS out_qti
on duplicate key update mdsrwrd_out_qti = qti;
