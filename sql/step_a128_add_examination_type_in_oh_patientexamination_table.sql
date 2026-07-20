-- Add examination_type column
-- Add examination_ipt column

ALTER TABLE OH_PATIENTEXAMINATION
   ADD COLUMN PEX_TYPE VARCHAR(50) DEFAULT 'Parametre a l''admision',
   ADD COLUMN PEX_IPT VARCHAR(255);