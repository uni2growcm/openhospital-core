ALTER TABLE `oh_laboratory`
    ADD COLUMN `LAB_BILL_ID` INT(11) NULL DEFAULT NULL;

CREATE INDEX `idx_laboratory_bill_id` ON `oh_laboratory` (`LAB_BILL_ID`);

CREATE INDEX `idx_laboratory_patient_bill` ON `oh_laboratory` (`LAB_PAT_ID`, `LAB_BILL_ID`);

SHOW COLUMNS FROM `oh_laboratory` LIKE 'LAB_BILL_ID';

SELECT
    CASE
        WHEN LAB_BILL_ID IS NULL OR LAB_BILL_ID = 0 THEN 'Not Billed'
        ELSE 'Billed'
        END AS status,
    COUNT(*) AS count
FROM `oh_laboratory`
GROUP BY CASE
    WHEN LAB_BILL_ID IS NULL OR LAB_BILL_ID = 0 THEN 'Not Billed'
    ELSE 'Billed'
END;