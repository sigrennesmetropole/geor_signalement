CREATE EXTENSION IF NOT EXISTS postgis;
CREATE EXTENSION IF NOT EXISTS postgis_topology;
CREATE EXTENSION IF NOT EXISTS fuzzystrmatch;
CREATE EXTENSION IF NOT EXISTS postgis_tiger_geocoder;
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE SCHEMA IF NOT EXISTS signalement AUTHORIZATION signalement;

ALTER ROLE signalement SET search_path TO signalement;
-- SET search_path TO signalement;

-- DROP SEQUENCE signalement.context_description_id_seq;
CREATE SEQUENCE signalement.context_description_id_seq;
ALTER SEQUENCE signalement.context_description_id_seq OWNER TO signalement;

-- DROP TABLE signalement.context_description;
CREATE TABLE signalement.context_description
(
    id bigint NOT NULL DEFAULT nextval('signalement.context_description_id_seq'::regclass),
    context_type character varying(30)  NOT NULL,
    geographic_type character varying(30)  NOT NULL,
    name character varying(100)  NOT NULL,
    label character varying(250)  NOT NULL,
    process_definition_key character varying(64) ,
    revision integer,
    CONSTRAINT context_description_pkey PRIMARY KEY (id),
    CONSTRAINT uk_bl728f8cx2fmhwxsy588wqmq4 UNIQUE (name)

)
WITH (OIDS = FALSE);
ALTER TABLE signalement.context_description OWNER to signalement;

-- DROP TABLE signalement.abstract_reporting;
CREATE TABLE signalement.abstract_reporting
(
    id bigint NOT NULL,
    uuid uuid NOT NULL,
    status character varying(50)  NOT NULL,
    geographic_type character varying(50)  NOT NULL,
    initiator character varying(100)  NOT NULL,
    datas text ,
    context_description_id bigint,
    creation_date timestamp without time zone NOT NULL,
    updated_date timestamp without time zone NOT NULL,
    CONSTRAINT abstract_reporting_pkey PRIMARY KEY (id),
    CONSTRAINT fk8eihrfrctlvi6pgik8a05eosw FOREIGN KEY (context_description_id)
        REFERENCES signalement.context_description (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
) WITH ( OIDS = FALSE) ;

ALTER TABLE signalement.abstract_reporting   OWNER to signalement;

CREATE SEQUENCE signalement.abstract_reporting_id_seq;
ALTER SEQUENCE signalement.abstract_reporting_id_seq OWNER TO signalement;
ALTER TABLE signalement.abstract_reporting ALTER COLUMN ID SET DEFAULT nextval('signalement.abstract_reporting_id_seq'::regclass);

-- DROP TABLE signalement.line_reporting;
CREATE TABLE signalement.line_reporting
(
    geometry geometry,
    id bigint NOT NULL,
    CONSTRAINT line_reporting_pkey PRIMARY KEY (id),
    CONSTRAINT fkeqq1pjfdkgwxqygtcmnhvh2vq FOREIGN KEY (id)
        REFERENCES signalement.abstract_reporting (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)
WITH (OIDS = FALSE);
ALTER TABLE signalement.line_reporting OWNER to signalement;
    
-- DROP TABLE signalement.point_reporting;
CREATE TABLE signalement.point_reporting
(
    geometry geometry,
    id bigint NOT NULL,
    CONSTRAINT point_reporting_pkey PRIMARY KEY (id),
    CONSTRAINT fk3pftpmvn0r2p862dl1hf073tb FOREIGN KEY (id)
        REFERENCES signalement.abstract_reporting (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)
WITH (OIDS = FALSE);
ALTER TABLE signalement.point_reporting OWNER to signalement;
    
-- DROP TABLE signalement.polygon_reporting;
CREATE TABLE signalement.polygon_reporting
(
    geometry geometry,
    id bigint NOT NULL,
    CONSTRAINT polygon_reporting_pkey PRIMARY KEY (id),
    CONSTRAINT fkorvvipin19mujjlrxh9cixl7n FOREIGN KEY (id)
        REFERENCES signalement.abstract_reporting (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)WITH (OIDS = FALSE);
ALTER TABLE signalement.polygon_reporting OWNER to signalement;

-- DROP SEQUENCE signalement.attachment_id_seq;
CREATE SEQUENCE signalement.attachment_id_seq;
ALTER SEQUENCE signalement.attachment_id_seq OWNER TO signalement;

-- DROP TABLE signalement.attachment;
CREATE TABLE signalement.attachment
(
    id bigint NOT NULL DEFAULT nextval('signalement.attachment_id_seq'::regclass),
    content oid,
    mime_type character varying(100) ,
    name character varying(150) ,
    CONSTRAINT attachment_pkey PRIMARY KEY (id)
)
WITH (OIDS = FALSE) ;
ALTER TABLE signalement.attachment OWNER to signalement;

-- DROP TABLE signalement.attachment_entity_attachment_ids;
CREATE TABLE signalement.attachment_entity_attachment_ids
(
    attachment_entity_id bigint NOT NULL,
    attachment_ids character varying(255) ,
    CONSTRAINT fk29ceqvmjbg1jpd5fss13r20cl FOREIGN KEY (attachment_entity_id)
        REFERENCES signalement.attachment (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)
WITH (OIDS = FALSE);
ALTER TABLE signalement.attachment_entity_attachment_ids OWNER to signalement;

-- DROP SEQUENCE signalement.section_definition_id_seq;
CREATE SEQUENCE signalement.section_definition_id_seq;
ALTER SEQUENCE signalement.section_definition_id_seq OWNER TO signalement;

-- DROP TABLE signalement.section_definition;
CREATE TABLE signalement.section_definition
(
    id bigint NOT NULL DEFAULT nextval('signalement.section_definition_id_seq'::regclass),
    definition text  NOT NULL,
    label character varying(150)  NOT NULL,
    name character varying(100)  NOT NULL,
    CONSTRAINT section_definition_pkey PRIMARY KEY (id)
)
WITH (OIDS = FALSE);
ALTER TABLE signalement.section_definition OWNER to signalement;

-- DROP SEQUENCE signalement.form_definition_id_seq;
CREATE SEQUENCE signalement.form_definition_id_seq;
ALTER SEQUENCE signalement.form_definition_id_seq OWNER TO signalement;
    
 -- DROP TABLE signalement.form_definition;
CREATE TABLE signalement.form_definition
(
  	id bigint NOT NULL DEFAULT nextval('signalement.form_definition_id_seq'::regclass),
    name character varying(100)  NOT NULL,
    CONSTRAINT form_definition_pkey PRIMARY KEY (id)
)
WITH (OIDS = FALSE);
ALTER TABLE signalement.form_definition OWNER to signalement;

-- DROP SEQUENCE signalement.form_section_definition_id_seq;
CREATE SEQUENCE signalement.form_section_definition_id_seq;
ALTER SEQUENCE signalement.form_section_definition_id_seq OWNER TO signalement;
    
-- DROP TABLE signalement.form_section_definition;
CREATE TABLE signalement.form_section_definition
(
    id bigint NOT NULL DEFAULT nextval('signalement.form_section_definition_id_seq'::regclass),
    order_ integer NOT NULL,
    read_only boolean NOT NULL,
    section_definition_id bigint,
    form_definition_id bigint,
    CONSTRAINT form_section_definition_pkey PRIMARY KEY (id),
    CONSTRAINT fkjpx0jm21mae9injyxgeaavfs FOREIGN KEY (section_definition_id)
        REFERENCES signalement.section_definition (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fkjvk89pj3kxxr22ir9gc4dc4b3 FOREIGN KEY (form_definition_id)
        REFERENCES signalement.form_definition (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)
WITH (OIDS = FALSE);
ALTER TABLE signalement.form_section_definition OWNER to signalement;

-- DROP SEQUENCE signalement.process_form_definition_id_seq;
CREATE SEQUENCE signalement.process_form_definition_id_seq;
ALTER SEQUENCE signalement.process_form_definition_id_seq OWNER TO signalement;
    
-- DROP TABLE signalement.process_form_definition;
CREATE TABLE signalement.process_form_definition
(
 	id bigint NOT NULL DEFAULT nextval('signalement.process_form_definition_id_seq'::regclass),
    process_definition_id character varying(64)  NOT NULL,
    revision integer,
    user_task_id character varying(64) ,
    form_definition_id bigint,
    CONSTRAINT process_form_definition_pkey PRIMARY KEY (id),
    CONSTRAINT fka46uc2xkhmviwxnbbgudfi3n3 FOREIGN KEY (form_definition_id)
        REFERENCES signalement.form_definition (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)
WITH (OIDS = FALSE);
ALTER TABLE signalement.process_form_definition OWNER to signalement;

-- DROP SEQUENCE signalement.geographic_area_id_seq;
CREATE SEQUENCE signalement.geographic_area_id_seq;
ALTER SEQUENCE signalement.geographic_area_id_seq OWNER TO signalement;
    
-- DROP TABLE signalement.geographic_area;
CREATE TABLE signalement.geographic_area
(
    id bigint NOT NULL DEFAULT nextval('signalement.geographic_area_id_seq'::regclass),
    geometry geometry,
    CONSTRAINT geographic_area_pkey PRIMARY KEY (id)
)
WITH (OIDS = FALSE);
ALTER TABLE signalement.geographic_area OWNER to signalement;

-- DROP SEQUENCE signalement.role_id_seq;
CREATE SEQUENCE signalement.role_id_seq;
ALTER SEQUENCE signalement.role_id_seq OWNER TO signalement;
    
-- DROP TABLE signalement.role;
CREATE TABLE signalement.role
(
    id bigint NOT NULL DEFAULT nextval('signalement.role_id_seq'::regclass),
    label character varying(255)  NOT NULL,
    name character varying(50)  NOT NULL,
    CONSTRAINT role_pkey PRIMARY KEY (id),
    CONSTRAINT uk_8sewwnpamngi6b1dwaa88askk UNIQUE (name)

)
WITH (OIDS = FALSE);
ALTER TABLE signalement.role OWNER to signalement;

-- DROP SEQUENCE signalement.user__id_seq;
CREATE SEQUENCE signalement.user__id_seq;
ALTER SEQUENCE signalement.user__id_seq OWNER TO signalement;
    
-- DROP TABLE signalement.user_;
CREATE TABLE signalement.user_
(
    id bigint NOT NULL DEFAULT nextval('signalement.user__id_seq'::regclass),
    email character varying(150)  NOT NULL,
    first_name character varying(150) ,
    last_name character varying(150) ,
    CONSTRAINT user__pkey PRIMARY KEY (id)
)
WITH (OIDS = FALSE);
ALTER TABLE signalement.user_ OWNER to signalement;
    
-- DROP SEQUENCE signalement.user_role_context_id_seq;
CREATE SEQUENCE signalement.user_role_context_id_seq;
ALTER SEQUENCE signalement.user_role_context_id_seq OWNER TO signalement;

-- DROP TABLE signalement.user_role_context;
CREATE TABLE signalement.user_role_context
(
    id bigint NOT NULL DEFAULT nextval('signalement.user_role_context_id_seq'::regclass),
    context_description_id bigint,
    geographic_area_id bigint,
    role_id bigint,
    user_id bigint,
    CONSTRAINT user_role_context_pkey PRIMARY KEY (id),
    CONSTRAINT fkiyeaotjxm6mc4dr5pdwtthumi FOREIGN KEY (role_id)
        REFERENCES signalement.role (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fknbv5lgp14g5c843a2lkd9kyv3 FOREIGN KEY (user_id)
        REFERENCES signalement.user_ (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fknlhxbhwhiby25esf43cc3nx0x FOREIGN KEY (context_description_id)
        REFERENCES signalement.context_description (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fkrd66a15s917bw58ulehm6lql9 FOREIGN KEY (geographic_area_id)
        REFERENCES signalement.geographic_area (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)
WITH (
    OIDS = FALSE
);

ALTER TABLE signalement.user_role_context OWNER to signalement;    