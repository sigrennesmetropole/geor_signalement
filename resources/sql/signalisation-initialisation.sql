CREATE EXTENSION IF NOT EXISTS postgis;
CREATE EXTENSION IF NOT EXISTS postgis_topology;
CREATE EXTENSION IF NOT EXISTS fuzzystrmatch;
CREATE EXTENSION IF NOT EXISTS postgis_tiger_geocoder;
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";


-- DROP TABLE abstract_reporting;
CREATE TABLE abstract_reporting
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
        REFERENCES context_description (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
) WITH ( OIDS = FALSE) ;

ALTER TABLE abstract_reporting   OWNER to signalisationusr;

CREATE SEQUENCE abstract_reporting_seq;
ALTER SEQUENCE abstract_reporting_seq OWNER TO signalisationusr;
ALTER TABLE abstract_reporting ALTER COLUMN ID SET DEFAULT NEXTVAL('abstract_reporting_seq'::regclass);

-- DROP TABLE line_reporting;
CREATE TABLE line_reporting
(
    geometry geometry,
    id bigint NOT NULL,
    CONSTRAINT line_reporting_pkey PRIMARY KEY (id),
    CONSTRAINT fkeqq1pjfdkgwxqygtcmnhvh2vq FOREIGN KEY (id)
        REFERENCES abstract_reporting (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)
WITH (OIDS = FALSE);
ALTER TABLE line_reporting OWNER to signalisationusr;
    
-- DROP TABLE point_reporting;
CREATE TABLE point_reporting
(
    geometry geometry,
    id bigint NOT NULL,
    CONSTRAINT point_reporting_pkey PRIMARY KEY (id),
    CONSTRAINT fk3pftpmvn0r2p862dl1hf073tb FOREIGN KEY (id)
        REFERENCES abstract_reporting (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)
WITH (OIDS = FALSE);
ALTER TABLE point_reporting OWNER to signalisationusr;
    
-- DROP TABLE polygon_reporting;
CREATE TABLE polygon_reporting
(
    geometry geometry,
    id bigint NOT NULL,
    CONSTRAINT polygon_reporting_pkey PRIMARY KEY (id),
    CONSTRAINT fkorvvipin19mujjlrxh9cixl7n FOREIGN KEY (id)
        REFERENCES abstract_reporting (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)WITH (OIDS = FALSE);
ALTER TABLE polygon_reporting OWNER to signalisationusr;

-- DROP SEQUENCE attachment_id_seq;
CREATE SEQUENCE attachment_id_seq;
ALTER SEQUENCE attachment_id_seq OWNER TO signalisationusr;

-- DROP TABLE attachment;
CREATE TABLE attachment
(
    id bigint NOT NULL DEFAULT nextval('attachment_id_seq'::regclass),
    content oid,
    mime_type character varying(100) ,
    name character varying(150) ,
    CONSTRAINT attachment_pkey PRIMARY KEY (id)
)
WITH (OIDS = FALSE) ;
ALTER TABLE attachment OWNER to signalisationusr;

-- DROP TABLE attachment_entity_attachment_ids;
CREATE TABLE attachment_entity_attachment_ids
(
    attachment_entity_id bigint NOT NULL,
    attachment_ids character varying(255) ,
    CONSTRAINT fk29ceqvmjbg1jpd5fss13r20cl FOREIGN KEY (attachment_entity_id)
        REFERENCES attachment (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)
WITH (OIDS = FALSE);
ALTER TABLE attachment_entity_attachment_ids OWNER to signalisationusr;

-- DROP SEQUENCE context_description_id_seq;
CREATE SEQUENCE context_description_id_seq;
ALTER SEQUENCE context_description_id_seq OWNER TO signalisationusr;

-- DROP TABLE context_description;
CREATE TABLE context_description
(
    id bigint NOT NULL DEFAULT nextval('context_description_id_seq'::regclass),
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
ALTER TABLE context_description OWNER to signalisationusr;

-- DROP SEQUENCE public.section_definition_id_seq;
CREATE SEQUENCE public.section_definition_id_seq;
ALTER SEQUENCE public.section_definition_id_seq OWNER TO signalisationusr;

-- DROP TABLE section_definition;
CREATE TABLE section_definition
(
    id bigint NOT NULL DEFAULT nextval('section_definition_id_seq'::regclass),,
    definition text  NOT NULL,
    label character varying(150)  NOT NULL,
    name character varying(100)  NOT NULL,
    CONSTRAINT section_definition_pkey PRIMARY KEY (id)
)
WITH (OIDS = FALSE);
ALTER TABLE section_definition OWNER to signalisationusr;

-- DROP SEQUENCE public.form_definition_id_seq;
CREATE SEQUENCE public.form_definition_id_seq;
ALTER SEQUENCE public.form_definition_id_seq OWNER TO signalisationusr;
    
 -- DROP TABLE form_definition;
CREATE TABLE form_definition
(
  	id bigint NOT NULL DEFAULT nextval('form_definition_id_seq'::regclass),
    name character varying(100)  NOT NULL,
    CONSTRAINT form_definition_pkey PRIMARY KEY (id)
)
WITH (OIDS = FALSE);
ALTER TABLE form_definition OWNER to signalisationusr;

-- DROP SEQUENCE public.form_section_definition_id_seq;
CREATE SEQUENCE public.form_section_definition_id_seq;
ALTER SEQUENCE public.form_section_definition_id_seq OWNER TO signalisationusr;
    
-- DROP TABLE form_section_definition;
CREATE TABLE form_section_definition
(
    id bigint NOT NULL DEFAULT nextval('form_section_definition_id_seq'::regclass),
    order_ integer NOT NULL,
    read_only boolean NOT NULL,
    section_definition_id bigint,
    form_definition_id bigint,
    CONSTRAINT form_section_definition_pkey PRIMARY KEY (id),
    CONSTRAINT fkjpx0jm21mae9injyxgeaavfs FOREIGN KEY (section_definition_id)
        REFERENCES section_definition (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fkjvk89pj3kxxr22ir9gc4dc4b3 FOREIGN KEY (form_definition_id)
        REFERENCES form_definition (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)
WITH (OIDS = FALSE);
ALTER TABLE form_section_definition OWNER to signalisationusr;

-- DROP SEQUENCE public.process_form_definition_id_seq;
CREATE SEQUENCE public.process_form_definition_id_seq;
ALTER SEQUENCE public.process_form_definition_id_seq OWNER TO signalisationusr;
    
-- DROP TABLE process_form_definition;
CREATE TABLE process_form_definition
(
 	id bigint NOT NULL DEFAULT nextval('process_form_definition_id_seq'::regclass),
    process_definition_id character varying(64)  NOT NULL,
    revision integer,
    service_task_id character varying(64) ,
    form_definition_id bigint,
    CONSTRAINT process_form_definition_pkey PRIMARY KEY (id),
    CONSTRAINT fka46uc2xkhmviwxnbbgudfi3n3 FOREIGN KEY (form_definition_id)
        REFERENCES form_definition (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)
WITH (OIDS = FALSE);
ALTER TABLE process_form_definition OWNER to signalisationusr;

-- DROP SEQUENCE public.geographic_area_id_seq;
CREATE SEQUENCE public.geographic_area_id_seq;
ALTER SEQUENCE public.geographic_area_id_seq OWNER TO signalisationusr;
    
-- DROP TABLE geographic_area;
CREATE TABLE geographic_area
(
    id bigint NOT NULL DEFAULT nextval('geographic_area_id_seq'::regclass),
    geometry geometry,
    CONSTRAINT geographic_area_pkey PRIMARY KEY (id)
)
WITH (OIDS = FALSE);
ALTER TABLE geographic_area OWNER to signalisationusr;

-- DROP SEQUENCE public.role_id_seq;
CREATE SEQUENCE public.role_id_seq;
ALTER SEQUENCE public.role_id_seq OWNER TO signalisationusr;
    
-- DROP TABLE role;
CREATE TABLE role
(
    id bigint NOT NULL DEFAULT nextval('role_id_seq'::regclass),
    label character varying(255)  NOT NULL,
    name character varying(50)  NOT NULL,
    CONSTRAINT role_pkey PRIMARY KEY (id),
    CONSTRAINT uk_8sewwnpamngi6b1dwaa88askk UNIQUE (name)

)
WITH (OIDS = FALSE);
ALTER TABLE role OWNER to signalisationusr;

-- DROP SEQUENCE public.user__id_seq;
CREATE SEQUENCE public.user__id_seq;
ALTER SEQUENCE public.user__id_seq OWNER TO signalisationusr;
    
-- DROP TABLE user_;
CREATE TABLE user_
(
    id bigint NOT NULL DEFAULT nextval('user__id_seq'::regclass),
    email character varying(150)  NOT NULL,
    first_name character varying(150) ,
    last_name character varying(150) ,
    CONSTRAINT user__pkey PRIMARY KEY (id)
)
WITH (OIDS = FALSE);
ALTER TABLE user_ OWNER to signalisationusr;
    
-- DROP SEQUENCE public.user_role_context_id_seq;
CREATE SEQUENCE public.user_role_context_id_seq;
ALTER SEQUENCE public.user_role_context_id_seq OWNER TO signalisationusr;

-- DROP TABLE user_role_context;
CREATE TABLE user_role_context
(
    id bigint NOT NULL DEFAULT nextval('user_role_context_id_seq'::regclass),
    context_description_id bigint,
    geographic_area_id bigint,
    role_id bigint,
    user_id bigint,
    CONSTRAINT user_role_context_pkey PRIMARY KEY (id),
    CONSTRAINT fkiyeaotjxm6mc4dr5pdwtthumi FOREIGN KEY (role_id)
        REFERENCES role (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fknbv5lgp14g5c843a2lkd9kyv3 FOREIGN KEY (user_id)
        REFERENCES user_ (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fknlhxbhwhiby25esf43cc3nx0x FOREIGN KEY (context_description_id)
        REFERENCES context_description (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fkrd66a15s917bw58ulehm6lql9 FOREIGN KEY (geographic_area_id)
        REFERENCES geographic_area (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)
WITH (
    OIDS = FALSE
);

ALTER TABLE user_role_context OWNER to signalisationusr;    