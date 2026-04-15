alter table patientexamination 
change column pex_height pex_height int(11) null default null comment 'Height in cm' ,
change column pex_weight pex_weight double null default null comment 'Weight in Kg' ,
change column pex_pa_min pex_ap_min int(11) null default null comment 'Blood Pressure min in mmHg' ,
change column pex_pa_max pex_ap_max int(11) null default null comment 'Blood Pressure max in mmHg' ,
change column pex_fc pex_hr int(11) null default null comment 'Heart Rate in APm' ,
change column pex_temp pex_temp double null default null comment 'Temperature in °C' ,
change column pex_sat pex_sat double null default null comment 'Saturation in %' ,
add column pex_hgt int(3) null default null comment 'Hemo Glucose Test' after pex_sat,
add column pex_diuresis int(11) null default null comment 'Daily Urine Volume in ml' after pex_hgt,
add column pex_diuresis_desc varchar(45) null default null comment 'Diuresis: physiological, oliguria, anuria, fequent, nocturia, stranguria, hematuria, pyuria' after pex_diuresis,
add column pex_bowel_desc varchar(45) null default null comment 'Bowel Function: regular, irregular, constipation, diarrheal' after pex_diuresis_desc;


update patientexamination set pex_height = null where pex_height = 0;
update patientexamination set pex_weight = null where pex_weight = 0;
update patientexamination set pex_ap_min = null where pex_ap_min = 0;
update patientexamination set pex_ap_max = null where pex_ap_max = 0;
update patientexamination set pex_hr = null where pex_hr = 0;
update patientexamination set pex_temp = null where pex_temp = 0;
update patientexamination set pex_sat = null where pex_sat = 0;
update patientexamination set pex_note = null where pex_note = '';
