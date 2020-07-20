
INSERT INTO section_definition (name,label,definition) values ('section-test', 'Test', '');
INSERT INTO form_definition( name) VALUES ('simpleProcces_draft');
INSERT INTO form_section_definition ( read_only, order_, section_definition_id, form_definition_id) values ( false, 0, currval('section_definition_id_seq'), currval('form_definition_id_seq'));
insert into process_form_definition ( process_definition_id, revision, user_task_id, form_definition_id) values ( 'simpleGroupReportingProcess', null, 'draft', currval('form_definition_id_seq') );

INSERT INTO section_definition (name,label,definition) values ('section-simple-comment', 'Commentaire', '');
INSERT INTO form_definition( name) VALUES ('simpleProcces_userTask_1');
INSERT INTO form_section_definition ( read_only, order_, section_definition_id, form_definition_id) values ( false, 0, currval('section_definition_id_seq'), currval('form_definition_id_seq'));

insert into process_form_definition ( process_definition_id, revision, user_task_id, form_definition_id) values ( 'simpleGroupReportingProcess', null, null, currval('form_definition_id_seq') );
insert into process_form_definition ( process_definition_id, revision, user_task_id, form_definition_id) values ( 'simpleGroupReportingProcess', null, 'UserTask_1', currval('form_definition_id_seq'));
insert into process_form_definition ( process_definition_id, revision, user_task_id, form_definition_id) values ( 'simpleGroupReportingProcess', null, 'UserTask_1', currval('form_definition_id_seq'));

INSERT INTO role ( name, label) VALUES ( 'Validator', 'Validateur');

INSERT INTO user_ ( login, first_name, last_name, email) VALUES ( 'testuser', 'Test', 'User', 'testuser@georchestra.fr');
INSERT INTO user_ ( login, first_name, last_name, email) VALUES ( 'testadmin', 'Test', 'Admin', 'testadmin@georchestra.fr');

INSERT INTO signalement.context_description(context_type, geographic_type, name, label, process_definition_key, revision) VALUES ( 'LAYER', 'POINT', 'layer1', 'Layer 1', 'simpleGroupReportingProcess', null);
INSERT INTO signalement.context_description(context_type, geographic_type, name, label, process_definition_key, revision) VALUES ( 'THEMA', 'POINT', 'thema1', 'Thema 1', 'simpleGroupReportingProcess', null);
INSERT INTO signalement.context_description(context_type, geographic_type, name, label, process_definition_key, revision) VALUES ( 'THEMA', 'POLYGON', 'thema2', 'Thema 2', 'simpleGroupReportingProcess', null);
INSERT INTO signalement.context_description(context_type, geographic_type, name, label, process_definition_key, revision) VALUES ( 'THEMA', 'LINE', 'thema3', 'Thema 3', 'simpleGroupReportingProcess', null);

INSERT INTO signalement.user_role_context(context_description_id, geographic_area_id, role_id, user_id) VALUES ( 1, null, 1, 2);
INSERT INTO signalement.user_role_context(context_description_id, geographic_area_id, role_id, user_id) VALUES ( 2, null, 1, 2);
INSERT INTO signalement.user_role_context(context_description_id, geographic_area_id, role_id, user_id) VALUES ( 3, null, 1, 2);
INSERT INTO signalement.user_role_context(context_description_id, geographic_area_id, role_id, user_id) VALUES ( 4, null, 1, 2);