update oh_laboratory set lab_created_date = lab_date;
update oh_laboratory set lab_date = cast(concat(lab_exam_date, ' ', time(lab_date)) as datetime);

alter table oh_laboratory drop column lab_exam_date;
