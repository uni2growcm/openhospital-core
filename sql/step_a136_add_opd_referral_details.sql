--
-- Free-text detail for OPD referrals, alongside the existing OPD_REFERRAL_FROM / OPD_REFERRAL_TO
-- "R"/null flags (left untouched): which hospital the visit was referred from/to, and the motif
-- (reason) for an incoming referral.
--

ALTER TABLE OH_OPD ADD COLUMN OPD_REFERRAL_FROM_HOSP varchar(50) DEFAULT NULL AFTER OPD_REFERRAL_FROM;
ALTER TABLE OH_OPD ADD COLUMN OPD_REFERRAL_TO_HOSP varchar(50) DEFAULT NULL AFTER OPD_REFERRAL_TO;
ALTER TABLE OH_OPD ADD COLUMN OPD_MOTIF varchar(20) DEFAULT NULL AFTER OPD_REFERRAL_TO_HOSP;
