alter table patientexamination 
	add column pex_rr int(11) null default null comment 'Respiratory rate in bpm' after pex_bowel_desc,
	add column pex_ausc varchar(50) null default null comment 'Auscultation: normal, wheezes, rhonchi, crackles, stridor, bronchial' after pex_rr;
	