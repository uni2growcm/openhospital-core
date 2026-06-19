-- ========================================================================
-- SCRIPT : step_a160_familyplanning_method_history.sql
-- OBJECTIVE : Add method history table and migrate to typology-based
--             method and visit type for Family Planning module.
--             This preserves existing data while enabling the new schema.
-- ========================================================================

-- ========================================================================
-- Table OH_FPMETHODHISTORY
-- Description : Tracks contraceptive method changes over time per FP record
-- ========================================================================
CREATE TABLE IF NOT EXISTS OH_FPMETHODHISTORY (

    FPMH_ID                 INT(11) NOT NULL AUTO_INCREMENT,

    FPMH_FP_ID              INT(11) NOT NULL,

    FPMH_METHOD_CODE        VARCHAR(20) NOT NULL,

    FPMH_START_DATE         DATE NOT NULL,

    FPMH_END_DATE           DATE DEFAULT NULL,

    FPMH_STOP_REASON        VARCHAR(255) DEFAULT NULL,

    FPMH_CREATED_BY         VARCHAR(50) NOT NULL,
    FPMH_CREATED_DATE       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    FPMH_LAST_MODIFIED_BY   VARCHAR(50) DEFAULT NULL,
    FPMH_LAST_MODIFIED_DATE DATETIME DEFAULT NULL
                                ON UPDATE CURRENT_TIMESTAMP,

    FPMH_ACTIVE             BOOLEAN DEFAULT TRUE,

    FPMH_LOCK               INT(11) DEFAULT 0,

    PRIMARY KEY (FPMH_ID),

    CONSTRAINT FK_FPMH_FP
        FOREIGN KEY (FPMH_FP_ID)
        REFERENCES OH_FAMILYPLANNING(FP_ID)
        ON DELETE CASCADE
        ON UPDATE CASCADE,

    CONSTRAINT FK_FPMH_METHOD
        FOREIGN KEY (FPMH_METHOD_CODE)
        REFERENCES OH_TYPOLOGIES(TYPO_CODE)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,

    INDEX IDX_FPMH_FP (FPMH_FP_ID),
    INDEX IDX_FPMH_METHOD (FPMH_METHOD_CODE)

) ENGINE=INNODB;

-- ========================================================================
-- Add new columns for typology-based method and registration date
-- ========================================================================
ALTER TABLE OH_FAMILYPLANNING
    ADD COLUMN IF NOT EXISTS FP_CURRENT_METHOD_CODE VARCHAR(20) DEFAULT NULL AFTER FP_STATUS,
    ADD COLUMN IF NOT EXISTS FP_REGISTRATION_DATE DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP AFTER FP_CURRENT_METHOD_CODE;

-- ========================================================================
-- Add foreign key for FP_CURRENT_METHOD_CODE
-- ========================================================================
ALTER TABLE OH_FAMILYPLANNING DROP FOREIGN KEY IF EXISTS FK_FP_CURRENT_METHOD;
ALTER TABLE OH_FAMILYPLANNING
    ADD CONSTRAINT FK_FP_CURRENT_METHOD
        FOREIGN KEY (FP_CURRENT_METHOD_CODE)
        REFERENCES OH_TYPOLOGIES(TYPO_CODE)
        ON DELETE RESTRICT
        ON UPDATE CASCADE;

-- ========================================================================
-- Migrate existing data : copy current FP_METHOD to method history
-- and set FP_CURRENT_METHOD_CODE (only if old columns exist)
-- ========================================================================
SELECT COUNT(*) INTO @has_fp_method FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'OH_FAMILYPLANNING' AND COLUMN_NAME = 'FP_METHOD';

SET @insert_history = IF(@has_fp_method > 0,
    'INSERT INTO OH_FPMETHODHISTORY
        (FPMH_FP_ID, FPMH_METHOD_CODE, FPMH_START_DATE, FPMH_END_DATE, FPMH_STOP_REASON,
         FPMH_CREATED_BY, FPMH_CREATED_DATE, FPMH_LAST_MODIFIED_BY, FPMH_LAST_MODIFIED_DATE,
         FPMH_ACTIVE, FPMH_LOCK)
     SELECT
         FP_ID,
         LOWER(FP_METHOD),
         FP_START_DATE,
         FP_END_DATE,
         FP_STOP_REASON,
         FP_CREATED_BY,
         FP_CREATED_DATE,
         FP_LAST_MODIFIED_BY,
         FP_LAST_MODIFIED_DATE,
         FP_ACTIVE,
         FP_LOCK
     FROM OH_FAMILYPLANNING
     WHERE FP_METHOD IS NOT NULL',
    'SELECT 1');
PREPARE stmt FROM @insert_history;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- ========================================================================
-- Set FP_CURRENT_METHOD_CODE from existing data
-- For ACTIVE records, use the current method; for others, use last method
-- ========================================================================
SET @update_method = IF(@has_fp_method > 0,
    'UPDATE OH_FAMILYPLANNING fp
     SET fp.FP_CURRENT_METHOD_CODE = LOWER(fp.FP_METHOD)
     WHERE fp.FP_METHOD IS NOT NULL',
    'SELECT 1');
PREPARE stmt FROM @update_method;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- ========================================================================
-- Set FP_REGISTRATION_DATE from existing START_DATE
-- ========================================================================
SELECT COUNT(*) INTO @has_fp_start_date FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'OH_FAMILYPLANNING' AND COLUMN_NAME = 'FP_START_DATE';

SET @update_reg_date = IF(@has_fp_start_date > 0,
    'UPDATE OH_FAMILYPLANNING fp
     SET fp.FP_REGISTRATION_DATE = CONVERT(fp.FP_START_DATE, DATETIME)
     WHERE fp.FP_START_DATE IS NOT NULL',
    'SELECT 1');
PREPARE stmt FROM @update_reg_date;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- ========================================================================
-- Add new column for typology-based visit type
-- ========================================================================
ALTER TABLE OH_FAMILYPLANNINGVISIT
    ADD COLUMN IF NOT EXISTS FPV_VISIT_TYPE_CODE VARCHAR(20) DEFAULT NULL AFTER FPV_VISIT_DATE;

-- ========================================================================
-- Migrate existing visit type data
-- ========================================================================
SELECT COUNT(*) INTO @has_fpv_visit_type FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'OH_FAMILYPLANNINGVISIT' AND COLUMN_NAME = 'FPV_VISIT_TYPE';

SET @update_visit_type = IF(@has_fpv_visit_type > 0,
    'UPDATE OH_FAMILYPLANNINGVISIT fpv
     SET fpv.FPV_VISIT_TYPE_CODE = LOWER(fpv.FPV_VISIT_TYPE)
     WHERE fpv.FPV_VISIT_TYPE IS NOT NULL',
    'SELECT 1');
PREPARE stmt FROM @update_visit_type;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- ========================================================================
-- Add foreign key for FPV_VISIT_TYPE_CODE
-- ========================================================================
ALTER TABLE OH_FAMILYPLANNINGVISIT DROP FOREIGN KEY IF EXISTS FK_FPV_VISIT_TYPE;
ALTER TABLE OH_FAMILYPLANNINGVISIT
    ADD CONSTRAINT FK_FPV_VISIT_TYPE
        FOREIGN KEY (FPV_VISIT_TYPE_CODE)
        REFERENCES OH_TYPOLOGIES(TYPO_CODE)
        ON DELETE RESTRICT
        ON UPDATE CASCADE;

-- ========================================================================
-- Add index for new columns
-- ========================================================================
ALTER TABLE OH_FAMILYPLANNING
    ADD INDEX IF NOT EXISTS IDX_FP_CURRENT_METHOD (FP_CURRENT_METHOD_CODE);

ALTER TABLE OH_FAMILYPLANNINGVISIT
    ADD INDEX IF NOT EXISTS IDX_FPV_VISIT_TYPE_CODE (FPV_VISIT_TYPE_CODE);
