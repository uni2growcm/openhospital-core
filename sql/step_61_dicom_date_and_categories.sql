-- changing date type from varchar to datetime to ease sorting
alter table dicom
change column dm_file_st_date dm_file_st_date datetime null default null ,
change column dm_file_ser_date dm_file_ser_date datetime null default null ;

create table dicomtype (
  dcmt_id varchar(3) not null,
  dcmt_desc varchar(50) not null,
  primary key (dcmt_id)
);

alter table dicom 
add column dm_dcmt_id varchar(3) null after dm_thumbnail,
add index FK_DICOM_DICOMTYPE_idx (dm_dcmt_id asc);

alter table dicom 
add constraint fk_dicom_dicomtype
  foreign key (dm_dcmt_id)
  references dicomtype (dcmt_id)
  on delete no action
  on update no action;
  
insert into menuitem (mni_id_a, mni_btn_label, mni_label, mni_tooltip, mni_shortcut, mni_submenu, mni_class, mni_is_submenu, mni_position) values ('dicomtype', 'angal.menu.btn.dicomtype', 'angal.menu.dicomtype', 'x', 'X', 'types', 'org.isf.dicomtype.gui.DicomTypeBrowser', 'N', '13');
insert into groupmenu (gm_ug_id_a, gm_mni_id_a, gm_active) values ('admin','dicomtype',1);
  

