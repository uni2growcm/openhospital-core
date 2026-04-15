drop table if exists vaccinetype;
create table vaccinetype (
	vact_id_a char (1)  not null ,
	vact_desc varchar (50)  not null ,
	primary key ( vact_id_a )
) engine=MyISAM;

alter table vaccine 
   change vac_pati  vac_vact_id_a char(1) not null;

insert into vaccinetype (vact_id_a, vact_desc)  
select distinct  vac_vact_id_a, case vac_vact_id_a when "C" then "Child" when "P" then "Pregnant" when "N" then "No pregnant" end
from vaccine;

insert into menuitem values ('vaccinetype','angal.menu.btn.vaccinetype','angal.menu.vaccinetype','x','V','types','org.isf.vactype.gui.VaccineTypeBrowser','N', 12);
insert into groupmenu (gm_ug_id_a, gm_mni_id_a, gm_active)  values  ('admin','vaccinetype','Y');

insert into menuitem values ('patientvaccine','angal.menu.btn.patientvaccine','angal.menu.patientvaccine','x','V','main','org.isf.patvac.gui.PatVacBrowser','N', 5);
insert into menuitem values ('btnpatientvaccinenew','angal.patvac.new','angal.patvac.new','x','N','patientvaccine','none','N',0);
insert into menuitem values ('btnpatientvaccineedit','angal.patvac.edit','angal.patvac.edit','x','E','patientvaccine','none','N',1);
insert into menuitem values ('btnpatientvaccinedel','angal.patvac.delete','angal.patvac.delete','x','D','patientvaccine','none','N',2);

insert into groupmenu (gm_ug_id_a, gm_mni_id_a, gm_active)  values  ('admin','patientvaccine','Y');
insert into groupmenu (gm_ug_id_a, gm_mni_id_a, gm_active)  values  ('admin','btnpatientvaccinenew','Y');
insert into groupmenu (gm_ug_id_a, gm_mni_id_a, gm_active)  values  ('admin','btnpatientvaccineedit','Y');
insert into groupmenu (gm_ug_id_a, gm_mni_id_a, gm_active)  values  ('admin','btnpatientvaccinedel','Y');
