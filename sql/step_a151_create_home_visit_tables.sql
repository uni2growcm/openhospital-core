-- Tables backing HomeVisitActivityReport (rpt_base/HomeVisitActivityReport.jasper and its
-- HomeVisitStats/HomeVisitByStaff/HomeVisitTopPatients subreports), which query these tables
-- directly via SQL (no org.isf.homevisit core/GUI layer is used or required to run the report).
--
-- Deliberately NOT porting feature/OH-414-mada-release's menu-item wiring for this table
-- (org.isf.homevisit.gui.HomeVisitBrowser / StaffBrowser) since those GUI classes don't exist
-- in this repo -- adding menu items for them would reintroduce the orphaned-class-reference
-- problem already found and cleaned up earlier in this change (see tasks.md 5.3e).

CREATE TABLE IF NOT EXISTS OH_STAFF (
    STF_ID                  INT AUTO_INCREMENT PRIMARY KEY,
    STF_FIRST_NAME          VARCHAR(50)  NOT NULL,
    STF_LAST_NAME           VARCHAR(50)  NOT NULL,
    STF_PROFESSION          VARCHAR(50)  DEFAULT NULL,
    STF_POSITION            VARCHAR(50),
    STF_PHONE               VARCHAR(50)  DEFAULT NULL,
    STF_IS_ACTIVE           TINYINT(1)   NOT NULL DEFAULT 1,
    STF_CREATED_BY          VARCHAR(50)  DEFAULT NULL,
    STF_CREATED_DATE        DATETIME     DEFAULT NULL,
    STF_LAST_MODIFIED_BY    VARCHAR(50)  DEFAULT NULL,
    STF_LAST_MODIFIED_DATE  DATETIME     DEFAULT NULL,
    STF_LOCK                INT          DEFAULT 0,
    KEY IDX_STAFF_NAME (STF_LAST_NAME, STF_FIRST_NAME),
    KEY IDX_STAFF_PROFESSION (STF_PROFESSION)
)   ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS OH_HOME_VISIT (
    HV_ID                       INT AUTO_INCREMENT PRIMARY KEY,
    HV_PAT_ID                   INT          NOT NULL,
    HV_STAFF_ID                 INT          DEFAULT NULL,
    HV_VISIT_START_DATE         DATETIME     NOT NULL,
    HV_VISIT_END_DATE           DATETIME     DEFAULT NULL,
    HV_STATUS                   VARCHAR(20)  NOT NULL DEFAULT 'PLANNED',
    HV_PURPOSE                  VARCHAR(255) DEFAULT NULL,
    HV_CLINICAL_NOTES           TEXT         DEFAULT NULL,
    HV_OBSERVATIONS             TEXT         DEFAULT NULL,
    HV_ADDRESS                  VARCHAR(255) DEFAULT NULL,
    HV_CONTACT_PHONE            VARCHAR(50)  DEFAULT NULL,
    HV_NEXT_VISIT_DATE          DATETIME     DEFAULT NULL,
    HV_CANCELLATION_REASON      VARCHAR(500) NULL,
    HV_CREATED_BY               VARCHAR(50)  DEFAULT NULL,
    HV_CREATED_DATE             DATETIME     DEFAULT NULL,
    HV_LAST_MODIFIED_BY         VARCHAR(50)  DEFAULT NULL,
    HV_LAST_MODIFIED_DATE       DATETIME     DEFAULT NULL,
    HV_DELETE                   TINYINT(1)   NOT NULL DEFAULT 1,
    HV_LOCK                     INT          DEFAULT 0,
    FOREIGN KEY (HV_PAT_ID) REFERENCES OH_PATIENT(PAT_ID),
    FOREIGN KEY (HV_STAFF_ID) REFERENCES OH_STAFF(STF_ID),
    KEY IDX_HOME_VISIT_PATIENT (HV_PAT_ID),
    KEY IDX_HOME_VISIT_STAFF (HV_STAFF_ID),
    KEY IDX_HOME_VISIT_START_DATE (HV_VISIT_START_DATE),
    KEY IDX_HOME_VISIT_STATUS (HV_STATUS),
    KEY IDX_HOME_VISIT_NEXT_DATE (HV_NEXT_VISIT_DATE)
)   ENGINE=InnoDB;
