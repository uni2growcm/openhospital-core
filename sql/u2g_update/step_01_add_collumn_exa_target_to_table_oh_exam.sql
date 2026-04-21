--Add the new column EXA_FOR
ALTER TABLE oh_exam 
ADD COLUMN EXA_TARGET ENUM('no', 'prenatal', 'postnatal', 'both') DEFAULT 'no' AFTER EXA_PROC;