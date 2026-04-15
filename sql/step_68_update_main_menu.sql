-- Remove "exit" and "file"
delete from menuitem where mni_id_a IN ('exit', 'file');
delete from groupmenu where gm_mni_id_a IN ('exit', 'file');

-- "General Data" is now "Settings" so change shortcut key
-- Note: the label is set in the properties file
update menuitem set mni_shortcut='S' where mni_id_a='generaldata';

-- Move the "Users" menu to the end of "Settings"
update menuitem set mni_submenu="generaldata", mni_position=10 where mni_id_a="users";

-- Rearrange main menu
update menuitem set mni_position=1 where mni_id_a="opd";
update menuitem set mni_position=2 where mni_id_a="admission";
update menuitem set mni_position=3 where mni_id_a="laboratory";
update menuitem set mni_position=4 where mni_id_a="pharmacy";
update menuitem set mni_position=5 where mni_id_a="patientvaccine";
update menuitem set mni_position=6 where mni_id_a="accounting";
update menuitem set mni_position=7 where mni_id_a="worksheet";
update menuitem set mni_position=8 where mni_id_a="statistics";
update menuitem set mni_position=9 where mni_id_a="printing";
update menuitem set mni_position=10 where mni_id_a="communication";
update menuitem set mni_position=11 where mni_id_a="generaldata";
update menuitem set mni_position=12 where mni_id_a="help";
