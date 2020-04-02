
INSERT INTO section_definition (name,label,definition) values ('section-simple-comment', 'Commentaire', '');
INSERT INTO form_definition( name) VALUES ('simpleProcces_userTask_1');
INSERT INTO form_section_definition ( read_only, order_, section_definition_id, form_definition_id) values ( false, 0, currval('section_definition_id_seq'), currval('form_definition_id_seq'));

insert into process_form_definition ( process_definition_id, revision, user_task_id, form_definition_id) values ( 'simpleReportingProcess', null, null, currval('form_definition_id_seq') );
insert into process_form_definition ( process_definition_id, revision, user_task_id, form_definition_id) values ( 'simpleReportingProcess', null, 'UserTask_1', currval('form_definition_id_seq'));
insert into process_form_definition ( process_definition_id, revision, user_task_id, form_definition_id) values ( 'simpleReportingProcess', 8, 'UserTask_1', currval('form_definition_id_seq'));

INSERT INTO signalement.context_description(context_type, geographic_type, name, label, process_definition_key, revision) VALUES ( 'LAYER', 'POINT', 'layer1', 'Layer 1', 'simpleReportingProcess', null);
INSERT INTO signalement.context_description(context_type, geographic_type, name, label, process_definition_key, revision) VALUES ( 'THEMA', 'POINT', 'thema1', 'Thema 1', 'simpleReportingProcess', null);
INSERT INTO signalement.context_description(context_type, geographic_type, name, label, process_definition_key, revision) VALUES ( 'THEMA', 'POLYGON', 'thema2', 'Thema 2', 'simpleReportingProcess', null);