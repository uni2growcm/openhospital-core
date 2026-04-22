-- Drop table if exists
DROP TABLE IF EXISTS OH_PREGNANCYVISIT;

-- Pregnancy Visit (ANC) Table
CREATE TABLE OH_PREGNANCYVISIT (

    PRGV_ID INT(11) NOT NULL AUTO_INCREMENT COMMENT 'Pregnancy Visit ID',

    -- Link to pregnancy
    PRGV_PRG_ID INT(11) NOT NULL,

     -- Link to pregnancy
    PRGV_TYPE_ID INT(11) NOT NULL,

    -- Visit information
    PRGV_DATE DATETIME NOT NULL,

    -- Gestational age
    PRGV_GESTATIONAL_WEEKS INT(11) COMMENT 'Gestational age in weeks',
    PRGV_GESTATIONAL_DAYS INT(11) COMMENT 'Gestational age in days (0-6)',

    -- Maternal vitals
    PRGV_MATERNAL_WEIGHT DOUBLE COMMENT 'Weight (kg)',
    PRGV_SYSTOLIC_BP INT(11) COMMENT 'Systolic blood pressure',
    PRGV_DIASTOLIC_BP INT(11) COMMENT 'Diastolic blood pressure',
    PRGV_TEMPERATURE DOUBLE COMMENT 'Body temperature',

    -- Obstetric examination
    PRGV_FUNDAL_HEIGHT DOUBLE COMMENT 'Fundal height (cm)',
    PRGV_ABDOMINAL_CIRCUMFERENCE DOUBLE COMMENT 'Abdominal circumference (cm)',
    PRGV_FETAL_HEART_RATE INT(11) COMMENT 'Fetal heart rate (BPM)',
    PRGV_FETAL_PRESENTATION VARCHAR(50) COMMENT 'Cephalic, Breech, Transverse',

    PRGV_CEPHALIC_ENGAGEMENT VARCHAR(100) COMMENT 'Engagement details',

    -- Clinical observations
    PRGV_EDEMA_PRESENCE VARCHAR(20) COMMENT 'None, +, ++, +++',
    PRGV_CONJUNCTIVA_STATUS VARCHAR(50) COMMENT 'Conjunctiva status',

    -- System examination
    PRGV_LOWER_LIMBS TEXT COMMENT 'Lower limbs exam',
    PRGV_BREASTS TEXT COMMENT 'Breast exam',
    PRGV_VAGINAL_EXAM TEXT COMMENT 'Vaginal exam',

    -- Lab results
    PRGV_URINE_PROTEIN VARCHAR(50) COMMENT 'Protein level',
    PRGV_URINE_GLUCOSE VARCHAR(50) COMMENT 'Glucose level',

    -- Follow-up
    PRGV_NEXT_APPOINTMENT_DATE DATE COMMENT 'Next visit date',

    -- Notes
    PRGV_CLINICAL_NOTES LONGTEXT COMMENT 'Clinical notes',

    -- Audit fields (JPA aligned)
    PRGV_CREATED_BY VARCHAR(50) NOT NULL,
    PRGV_CREATED_DATE DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRGV_LAST_MODIFIED_BY VARCHAR(50) NOT NULL,
    PRGV_LAST_MODIFIED_DATE DATETIME DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,

    PRGV_LOCK INT(11),

    -- Primary key
    PRIMARY KEY (PRGV_ID),

    -- Foreign key (IMPORTANT FIX)
    CONSTRAINT FK_PREGNANCYVISIT_PREGNANCY
        FOREIGN KEY (PRGV_PRG_ID)
        REFERENCES OH_PREGNANCY(PRG_ID)
        ON DELETE CASCADE
        ON UPDATE CASCADE,

        -- Foreign key (IMPORTANT FIX)
    CONSTRAINT FK_PREGNANCYVISIT_VISITTYPE
        FOREIGN KEY (PRGV_TYPE_ID)
        REFERENCES OH_PREGNANCYVISITTYPE(PRGVT_ID)
        ON DELETE CASCADE
        ON UPDATE CASCADE,

    -- Indexes
    INDEX idx_pregnancyvisit_pregnancy (PRGV_PRG_ID),
    INDEX idx_pregnancyvisit_date (PRGV_DATE)

) ENGINE = INNODB DEFAULT CHARACTER SET utf8;