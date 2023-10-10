create schema if not exists public;
set schema public;

--CREATE ALIAS if not exists uuid_generate_v4 AS $$
--String uuid_generate(){ return java.util.UUID.randomUUID().toString();}
--$$;
--
--CREATE ALIAS if not exists uuid_generate_v4 AS $$
--String uuid_generate(){ return java.util.UUID.randomUUID().toString();}
--$$;
--
--CREATE ALIAS if not exists st_union AS $$
--String st_union2(String[] a){ if (a != null && a.length > 0) return a[0]; else return null;}
--$$;

CREATE ALIAS if not exists st_contains AS $$
boolean st_contains2(String a, String b){ return true;}
$$;

CREATE ALIAS if not exists st_geometryfromtext AS $$
String st_geometryfromtext2(String a, String b){ return a;}
$$;



CREATE TABLE if not exists abstract_reporting (
    id bigserial NOT NULL,
    uuid uuid NOT NULL,
    status varchar(50) NOT NULL,
    geographic_type varchar(50) NOT NULL,
    initiator varchar(100) NOT NULL,
    datas text,
    context_description_id bigint,
    creation_date timestamp without time zone NOT NULL,
    updated_date timestamp without time zone NOT NULL,
    description varchar(1024) NOT NULL,
    assignee varchar(100),
    functional_status varchar(100),
    primary key (id)
);


CREATE TABLE if not exists attachment (
    id bigserial NOT NULL,
    content varchar(4096),
    mime_type varchar(100),
    name varchar(150),
    primary key (id)
);

CREATE TABLE if not exists attachment_entity_attachment_ids (
    attachment_entity_id bigint NOT NULL,
    attachment_ids varchar(255),
    primary key (attachment_entity_id)
);


CREATE TABLE if not exists context_description (
    id bigserial NOT NULL,
    context_type varchar(30) NOT NULL,
    geographic_type varchar(30) NOT NULL,
    name varchar(100) NOT NULL,
    label varchar(250) NOT NULL,
    process_definition_key varchar(64),
    revision int4,
    primary key (id)
);

CREATE TABLE if not exists geographic_area (
    id bigserial NOT NULL,
    geometry geometry,
    nom varchar(255),
    codeinsee varchar(255),
    primary key (id)
);

CREATE TABLE if not exists role (
    id bigserial NOT NULL,
    label varchar(255) NOT NULL,
    name varchar(50) NOT NULL,
    primary key (id)
);


CREATE TABLE if not exists user_ (
    id bigserial NOT NULL,
    email varchar(150) NOT NULL,
    first_name varchar(150),
    last_name varchar(150),
    login varchar(100) NOT NULL,
    organization varchar(150),
    roles varchar(1024),
    primary key (id)
);

CREATE TABLE if not exists user_role_context (
    id bigserial NOT NULL,
    context_description_id bigint,
    geographic_area_id bigint,
    role_id bigint,
    user_id bigint,
    primary key (id)
);

CREATE TABLE if not exists form_definition (
    id bigserial NOT NULL,
    name varchar(100) NOT NULL,
    primary key (id)
);

CREATE TABLE if not exists form_section_definition (
    id bigserial NOT NULL,
    order_ int4 NOT NULL,
    read_ boolean NOT NULL,
    section_definition_id bigint,
    form_definition_id bigint,
    primary key (id)
);

CREATE TABLE if not exists line_reporting (
    geometry geometry,
    id bigint NOT NULL,
    primary key (id)
);

CREATE TABLE if not exists point_reporting (
    geometry geometry,
    id bigint NOT NULL,
    primary key (id)
);

CREATE TABLE if not exists polygon_reporting (
    geometry geometry,
    id bigint NOT NULL,
    primary key (id)
);

CREATE TABLE if not exists process_form_definition (
	id bigserial NOT NULL,
    process_definition_id varchar(64) NOT NULL,
    revision int4,
    user_task_id varchar(64),
    form_definition_id bigint,
    primary key (id)
);

CREATE TABLE if not exists process_styling (
    id bigint NOT NULL,
    process_definition_id varchar(64) NOT NULL,
    revision int4,
    user_task_id varchar(64),
    styling_id bigint,
    primary key (id)
);

CREATE TABLE if not exists section_definition (
    id bigserial NOT NULL,
    definition text NOT NULL,
    label varchar(150) NOT NULL,
    name varchar(100) NOT NULL,
    primary key (id)
);

CREATE TABLE if not exists styling (
    id bigint NOT NULL,
    definition text NOT NULL,
    name varchar(100) NOT NULL,
    primary key (id)
);

ALTER TABLE role
    ADD CONSTRAINT if not exists uk_8sewwnpamngi6b1dwaa88askk UNIQUE (name);

ALTER TABLE context_description
    ADD CONSTRAINT if not exists uk_bl728f8cx2fmhwxsy588wqmq4 UNIQUE (name);

ALTER TABLE attachment_entity_attachment_ids
    ADD CONSTRAINT if not exists fk29ceqvmjbg1jpd5fss13r20cl FOREIGN KEY (attachment_entity_id) REFERENCES attachment(id);--

ALTER TABLE point_reporting
    ADD CONSTRAINT if not exists fk3pftpmvn0r2p862dl1hf073tb FOREIGN KEY (id) REFERENCES abstract_reporting(id);

ALTER TABLE process_styling
    ADD CONSTRAINT if not exists fk831drllbrb3ltljbtj8pqemha FOREIGN KEY (styling_id) REFERENCES styling(id);

ALTER TABLE abstract_reporting
    ADD CONSTRAINT if not exists fk8eihrfrctlvi6pgik8a05eosw FOREIGN KEY (context_description_id) REFERENCES context_description(id);

ALTER TABLE process_form_definition
    ADD CONSTRAINT if not exists fka46uc2xkhmviwxnbbgudfi3n3 FOREIGN KEY (form_definition_id) REFERENCES form_definition(id);

ALTER TABLE line_reporting
    ADD CONSTRAINT if not exists fkeqq1pjfdkgwxqygtcmnhvh2vq FOREIGN KEY (id) REFERENCES abstract_reporting(id);

ALTER TABLE user_role_context
    ADD CONSTRAINT if not exists fkiyeaotjxm6mc4dr5pdwtthumi FOREIGN KEY (role_id) REFERENCES role(id);

ALTER TABLE form_section_definition
    ADD CONSTRAINT if not exists fkjpx0jm21mae9injyxgeaavfs FOREIGN KEY (section_definition_id) REFERENCES section_definition(id);

ALTER TABLE form_section_definition
    ADD CONSTRAINT if not exists fkjvk89pj3kxxr22ir9gc4dc4b3 FOREIGN KEY (form_definition_id) REFERENCES form_definition(id);

ALTER TABLE user_role_context
    ADD CONSTRAINT if not exists fknbv5lgp14g5c843a2lkd9kyv3 FOREIGN KEY (user_id) REFERENCES user_(id);

ALTER TABLE user_role_context
    ADD CONSTRAINT if not exists fknlhxbhwhiby25esf43cc3nx0x FOREIGN KEY (context_description_id) REFERENCES context_description(id);

ALTER TABLE polygon_reporting
    ADD CONSTRAINT if not exists fkorvvipin19mujjlrxh9cixl7n FOREIGN KEY (id) REFERENCES abstract_reporting(id);

ALTER TABLE user_role_context
    ADD CONSTRAINT if not exists fkrd66a15s917bw58ulehm6lql9 FOREIGN KEY (geographic_area_id) REFERENCES geographic_area(id);
    