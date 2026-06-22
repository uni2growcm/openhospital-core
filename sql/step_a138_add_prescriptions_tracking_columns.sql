-- Add qtyBougth column to therapies table to track billed quantities
ALTER TABLE `oh_therapies`
    ADD COLUMN `THR_QTY_BOUGTH` DOUBLE DEFAULT 0;

CREATE INDEX `idx_therapies_qty_bougth` ON `oh_therapies` (`THR_QTY_BOUGTH`);

-- Add itemAmountBrut column to billitems table to track gross prices before reductions
ALTER TABLE `oh_billitems`
    ADD COLUMN `BLI_ITEM_AMOUNT_BRUT` DOUBLE DEFAULT 0;

-- Verify columns were added
SHOW COLUMNS FROM `oh_therapies` LIKE 'THR_QTY_BOUGTH';
SHOW COLUMNS FROM `oh_billitems` LIKE 'BLI_ITEM_AMOUNT_BRUT';

-- Display statistics
SELECT
    COUNT(*) as total_therapies,
    SUM(CASE WHEN THR_QTY_BOUGTH > 0 THEN 1 ELSE 0 END) as billed_therapies
FROM `oh_therapies`;

SELECT
    COUNT(*) as total_billitems,
    SUM(CASE WHEN BLI_ITEM_AMOUNT_BRUT > 0 THEN 1 ELSE 0 END) as items_with_brut_price
FROM `oh_billitems`;
