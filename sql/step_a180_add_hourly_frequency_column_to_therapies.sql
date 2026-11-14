-- ========================================================================
-- Add THR_FREQINHOUR column to OH_THERAPIES for hourly therapy prescriptions
-- ========================================================================

ALTER TABLE `oh_therapies`
    ADD COLUMN `THR_FREQINHOUR` INT DEFAULT 0;
