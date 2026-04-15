--
-- add lock (version) column in tables where it's missing
--

alter table `oh_bills`
    add column `bll_lock` int(11) not null default 0;

alter table `oh_agetype`
    add column `at_lock` int(11) not null default 0;

alter table `oh_patienthistory`
    add column `pah_lock` int(11) not null default 0;

alter table `oh_patientexamination`
    add column `pex_lock` int(11) not null default 0;

alter table `oh_user_settings`
    add column `uss_lock` int(11) not null default 0;

alter table `oh_prices`
    add column `prc_lock` int(11) not null default 0;

alter table `oh_pricelists`
    add column `lst_lock` int(11) not null default 0;

alter table `oh_supplier`
    add column `sup_lock` int(11) not null default 0;

alter table `oh_pricesothers`
    add column `oth_lock` int(11) not null default 0;

alter table `oh_visits`
    add column `vst_lock` int(11) not null default 0;

alter table `oh_medicaldsrward`
    add column `mdsrwrd_lock` int(11) not null default 0;