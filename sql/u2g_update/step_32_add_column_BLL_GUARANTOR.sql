ALTER TABLE OH_BILLS ADD COLUMN `BLL_GUARANTOR` VARCHAR(50) NULL DEFAULT NULL;

ALTER TABLE OH_BILLS ADD CONSTRAINT FK_BILLS_USER_2 FOREIGN KEY (`BLL_GUARANTOR`) REFERENCES OH_USER(`US_ID_A`);
