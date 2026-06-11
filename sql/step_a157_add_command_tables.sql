CREATE TABLE OH_COMMAND (
                            CMD_ID INT AUTO_INCREMENT,
                            CMD_REFNO VARCHAR(50) NOT NULL DEFAULT '',
                            CMD_DATE DATETIME NOT NULL,
                            CMD_LOCK INT NOT NULL DEFAULT 0,
                            CMD_CREATED_BY VARCHAR(50) DEFAULT NULL,
                            CMD_CREATED_DATE DATETIME DEFAULT NULL,
                            CMD_LAST_MODIFIED_BY VARCHAR(50) DEFAULT NULL,
                            CMD_LAST_MODIFIED_DATE DATETIME DEFAULT NULL,
                            CMD_ACTIVE TINYINT(1) NOT NULL DEFAULT 1,

                            PRIMARY KEY (CMD_ID),
                            KEY FK_COMMAND_CREATED_BY (CMD_CREATED_BY),
                            KEY FK_COMMAND_LAST_MODIFIED_BY (CMD_LAST_MODIFIED_BY),
                            CONSTRAINT FK_COMMAND_CREATED_BY FOREIGN KEY (CMD_CREATED_BY) REFERENCES OH_USER (US_ID_A),
                            CONSTRAINT FK_COMMAND_LAST_MODIFIED_BY FOREIGN KEY (CMD_LAST_MODIFIED_BY) REFERENCES OH_USER (US_ID_A)
) ENGINE=INNODB;


CREATE TABLE OH_COMMAND_ROW (
                                CMR_ID INT AUTO_INCREMENT,
                                CMR_CMD_ID INT NOT NULL,
                                CMR_MDSR_ID INT NOT NULL,
                                CMR_MDSR_CODE VARCHAR(50) NOT NULL,
                                CMR_MDSR_DESC VARCHAR(255) NOT NULL,
                                CMR_LT_ID_A VARCHAR(50) DEFAULT NULL,
                                CMR_SUP_ID INT DEFAULT NULL,
                                CMR_QTY_IN_STORE FLOAT NOT NULL DEFAULT 0,
                                CMR_CRITICAL_LEVEL FLOAT NOT NULL DEFAULT 0,
                                CMR_STILL_QTY FLOAT DEFAULT NULL,
                                CMR_ORDER_QTY FLOAT DEFAULT NULL,
                                CMR_USER_ADDED_QTY FLOAT NOT NULL DEFAULT 0,
                                CMR_CREATED_BY VARCHAR(50) DEFAULT NULL,
                                CMR_CREATED_DATE DATETIME DEFAULT NULL,
                                CMR_LAST_MODIFIED_BY VARCHAR(50) DEFAULT NULL,
                                CMR_LAST_MODIFIED_DATE DATETIME DEFAULT NULL,
                                CMR_ACTIVE TINYINT(1) NOT NULL DEFAULT 1,

                                PRIMARY KEY (CMR_ID),
                                KEY FK_COMMAND_ROW_COMMAND (CMR_CMD_ID),
                                KEY FK_COMMAND_ROW_MEDICALDSR (CMR_MDSR_ID),
                                KEY FK_COMMAND_ROW_MEDICALDSRLOT (CMR_LT_ID_A),
                                KEY FK_COMMAND_ROW_SUPPLIER (CMR_SUP_ID),
                                KEY FK_COMMAND_ROW_CREATED_BY (CMR_CREATED_BY),
                                KEY FK_COMMAND_ROW_LAST_MODIFIED_BY (CMR_LAST_MODIFIED_BY),

                                CONSTRAINT FK_COMMAND_ROW_COMMAND FOREIGN KEY (CMR_CMD_ID) REFERENCES OH_COMMAND (CMD_ID) ON DELETE CASCADE,
                                CONSTRAINT FK_COMMAND_ROW_CREATED_BY FOREIGN KEY (CMR_CREATED_BY) REFERENCES OH_USER (US_ID_A),
                                CONSTRAINT FK_COMMAND_ROW_LAST_MODIFIED_BY FOREIGN KEY (CMR_LAST_MODIFIED_BY) REFERENCES OH_USER (US_ID_A),
                                CONSTRAINT FK_COMMAND_ROW_MEDICALDSR FOREIGN KEY (CMR_MDSR_ID) REFERENCES OH_MEDICALDSR (MDSR_ID),
                                CONSTRAINT FK_COMMAND_ROW_MEDICALDSRLOT FOREIGN KEY (CMR_LT_ID_A) REFERENCES OH_MEDICALDSRLOT (LT_ID_A),
                                CONSTRAINT FK_COMMAND_ROW_SUPPLIER FOREIGN KEY (CMR_SUP_ID) REFERENCES OH_SUPPLIER (SUP_ID)
) ENGINE=INNODB;








































