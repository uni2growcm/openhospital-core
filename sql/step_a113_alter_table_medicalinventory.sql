-- Step 1: drop the foreign key constraint
alter table oh_medicaldsrinventory 
drop constraint oh_medicaldsrinventory_ibfk_2;

alter table oh_medicaldsrinventory 
drop constraint oh_medicaldsrinventory_ibfk_1;

alter table oh_medicaldsrinventory 
drop constraint oh_medicaldsrinventory_ibfk_3;

alter table oh_medicaldsrinventory 
drop constraint oh_medicaldsrinventory_ibfk_4;

alter table oh_medicaldsrinventory 
drop constraint oh_medicaldsrinventory_ibfk_5;

alter table oh_medicaldsrinventory 
drop constraint oh_medicaldsrinventory_ibfk_6;

-- Step 2: change the data type of the foreign key column
alter table oh_medicaldsrinventory 
modify column minvt_wrd_id_a char(3);

-- Step 3: Recreate the foreign key constraint
alter table oh_medicaldsrinventory 
add constraint fk_medicaldsrinventory_ward_1
foreign key (minvt_wrd_id_a) references oh_ward (wrd_id_a);

alter table oh_medicaldsrinventory 
add constraint fk_medicaldsrinventory_user
foreign key (minvt_us_id_a) references oh_user (us_id_a);

alter table oh_medicaldsrinventory 
add constraint fk_medicaldsrinventory_medicaldsrstockmovtype_1
foreign key (minvt_charge_type) references oh_medicaldsrstockmovtype (mmvt_id_a);

alter table oh_medicaldsrinventory 
add constraint fk_medicaldsrinventory_medicaldsrstockmovtype_2
foreign key (minvt_discharge_type) references oh_medicaldsrstockmovtype (mmvt_id_a);

alter table oh_medicaldsrinventory 
add constraint fk_medicaldsrinventory_supplier
foreign key (minvt_supplier) references oh_supplier (sup_id);

alter table oh_medicaldsrinventory 
add constraint fk_medicaldsrinventory_ward_2
foreign key (minvt_destination) references oh_ward (wrd_id_a);



