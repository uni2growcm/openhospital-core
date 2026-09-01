--
-- Add shape (pharmaceutical form) and dosing (strength) fields to medicals
--

ALTER TABLE `oh_medicaldsr`
    ADD COLUMN `MDSR_SHAPE` VARCHAR(50) NULL DEFAULT NULL COMMENT 'Pharmaceutical shape/form',
    ADD COLUMN `MDSR_DOSING` VARCHAR(50) NULL DEFAULT NULL COMMENT 'Dosing/strength';
