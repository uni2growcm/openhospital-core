alter table oh_patienthistory
change column `pah_phy_alcool` `pah_phy_alcohol` tinyint(1) null default 0 ;

alter table oh_patienthistory
change column `pah_phy_alvo_nor` `pah_phy_bowel_nor` tinyint(1) null default 1 ,
change column `pah_phy_alvo_abn` `pah_phy_bowel_abn` varchar(30) null default null ;
