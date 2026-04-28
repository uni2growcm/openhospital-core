-- Drop table if exists
DROP TABLE IF EXISTS OH_PREGNANCY;

-- Create Pregnancy Table
CREATE TABLE OH_PREGNANCY (
    PRG_ID INT(11) NOT NULL AUTO_INCREMENT,
    PRG_PAT_ID INT(11) NOT NULL,

    -- Pregnancy timeline
    PRG_LMP DATE COMMENT 'Last Menstrual Period',
    PRG_EDD_LMP DATE COMMENT 'Estimated Delivery Date (LMP)',
    PRG_EDD_SCAN DATE COMMENT 'Estimated Delivery Date (Ultrasound adjusted)',

    -- Obstetric history
    PRG_GRAVIDITY INT(11) DEFAULT 0 COMMENT 'Total number of pregnancies',
    PRG_PARITY INT(11) DEFAULT 0 COMMENT 'Births reaching viability',
    PRG_MISSCARRIAGES INT(11) DEFAULT 0 COMMENT 'Miscarriages / abortions',

    PRG_RISK_LEVEL VARCHAR(20) DEFAULT 'Low',
    PRG_STATUS VARCHAR(20) DEFAULT 'Ongoing',

    PRG_CREATED_BY VARCHAR(50) NOT NULL,
    PRG_CREATED_DATE DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRG_LAST_MODIFIED_BY VARCHAR(50),
    PRG_LAST_MODIFIED_DATE DATETIME DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,

    PRG_LOCK INT(11) DEFAULT 0,

    PRG_ACTIVE BOOLEAN DEFAULT TRUE,

    -- Primary key
    PRIMARY KEY (PRG_ID),

    -- Foreign key constraint
    CONSTRAINT FK_PREGNANCY_PATIENT
        FOREIGN KEY (PRG_PAT_ID)
        REFERENCES OH_PATIENT(PAT_ID)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,

    -- Indexing for performance
    INDEX idx_pregnancy_patient (PRG_PAT_ID),
    INDEX idx_pregnancy_status (PRG_STATUS),
    INDEX idx_pregnancy_risk (PRG_RISK_LEVEL),
    INDEX idx_pregnancy_created (PRG_CREATED_DATE),
    INDEX idx_pregnancy_lmp (PRG_LMP)

) ENGINE = INNODB;