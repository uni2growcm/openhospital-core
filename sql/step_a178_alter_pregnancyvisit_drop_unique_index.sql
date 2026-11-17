-- ========================================================================
-- Remove erroneous UNIQUE constraint on PRGV_PRG_ID from OH_PREGNANCYVISIT
-- so that a pregnant woman can have multiple antenatal consultations (CPN 1, CPN 2, etc.)
-- ========================================================================

ALTER TABLE OH_PREGNANCYVISIT DROP INDEX IF EXISTS PRGV_PRG_ID;
