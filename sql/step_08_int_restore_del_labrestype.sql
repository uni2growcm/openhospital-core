alter table patient modify column pat_age int not null default 0;
alter table opd modify column opd_age int not null default 0;
delete from groupmenu where gm_mni_id_a = 'laboratoryresulttype';
delete from menuitem where mni_id_a = 'laboratoryresulttype';
update menuitem set mni_class = 'org.isf.lab.gui.LabBrowser', mni_is_submenu = 'N' where mni_id_a = 'laboratory';
delete from groupmenu where gm_mni_id_a = 'labbrowsing';
delete from menuitem where mni_id_a = 'labbrowsing';