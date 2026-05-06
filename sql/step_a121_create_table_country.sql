CREATE TABLE IF NOT EXISTS `COUNTRY` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `iso_code` VARCHAR(2) NOT NULL,
    `phone_code` VARCHAR(10) NOT NULL,
    `name` VARCHAR(100) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_country_iso_code` (`iso_code`),
    UNIQUE KEY `uk_country_phone_code` (`phone_code`),
    UNIQUE KEY `uk_country_name` (`name`)
    ) ENGINE=InnoDB;
