update opd set opd_created_date = opd_date;
update opd set opd_date = cast(concat(opd_date_vis, ' ', time(opd_date)) as datetime);

-- now we could drop opd_date_vis field for next release
alter table opd drop column opd_date_vis;
