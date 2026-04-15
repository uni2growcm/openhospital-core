alter table oh_operationrow change oper_id oper_ope_id_a varchar(10) not null;

alter table oh_operationrow engine = innodb, convert to character set utf8;

alter table oh_operationrow 
add index FK_OPERATIONROW_OPERATION_idx (oper_ope_id_a asc);

alter table oh_operationrow 
add constraint fk_operationrow_operation
  foreign key (oper_ope_id_a)
  references oh_operation (ope_id_a)
  on delete no action
  on update no action;


  