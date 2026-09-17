-- Migration steps a121-a152
-- Exécuté APRÈS load_demo_data.sql (create_all_demo) car le dump demo
-- DROP/CREATE les tables depuis un ancien snapshot et détruirait ces apports.
-- Retirés de step_04_all_following_steps.sql.
source step_a121_add_lab_filters.sql;
source step_a122_add_article_family.sql;
source step_a123_add_prescriber.sql;
source step_a124_add_medical_shape_and_dosing.sql;
source step_a125_add_block_exam_tables.sql;
source step_a125_create_table_typologies.sql;
source step_a126_add_stock_order.sql;
source step_a127_add_pregnancy_module.sql;
source step_a128_add_pregnancy_delivery_newborn.sql;
source step_a129_add_family_planning_module.sql;
source step_a130_add_hiv_child_followup_module.sql;
source step_a131_add_admission_bed_room.sql;
source step_a132_add_pagination_filter_indexes.sql;
source step_a133_add_opd_manage_exams_permission.sql;
source step_a134_add_opd_malnutrition.sql;
source step_a135_add_opd_disease.sql;
source step_a136_add_opd_referral_details.sql;
source step_a137_add_patient_mada_extended_fields.sql;
source step_a138_add_village_to_patientvaccine.sql;
source step_a139_add_vaccine_stock_tracking.sql;
source step_a140_restructure_patientvaccine_menu.sql;
source step_a141_add_variable_price_column.sql;
source step_a142_add_billing_ward_stock.sql;
source step_a143_add_prescription_billing_columns.sql;
source step_a144_add_reduction_plan.sql;
source step_a145_add_close_bill_permission.sql;
source step_a146_add_partner_type.sql;
source step_a147_add_partners.sql;
source step_a148_add_bill_guarantor.sql;
source step_a149_statistics_submenus.sql;
source step_a150_hiv_module.sql;
source step_a151_create_home_visit_tables.sql;
source step_a152_tuberculosis_module.sql;