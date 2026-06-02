
CREATE TABLE OH_ITEMPAYMENTS (
                                 ITP_ID int(11) NOT NULL AUTO_INCREMENT,
                                 ITP_ITEM_ID varchar(25) DEFAULT NULL,
                                 ITP_ITEM_DESC varchar(100) NOT NULL,
                                 ITP_BLL_ID int(11) NOT NULL,
                                 IS_REFUND tinyint(1) NOT NULL,
                                 ITP_AMOUNT double NOT NULL,
                                 ITP_USR_ID_A varchar(50) NOT NULL,
                                 ITP_ITEM_GROUP varchar(3) DEFAULT NULL,
                                 ITP_DATE datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                 PRIMARY KEY (ITP_ID),
                                 KEY fk_itp_bll_id_itempayments (ITP_BLL_ID),
                                 CONSTRAINT fk_itp_bll_id_itempayments FOREIGN KEY (ITP_BLL_ID) REFERENCES OH_BILLS (BLL_ID)
) ENGINE=InnoDB;
