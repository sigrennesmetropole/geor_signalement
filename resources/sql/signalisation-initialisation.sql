CREATE EXTENSION postgis;
CREATE EXTENSION postgis_topology;
CREATE EXTENSION fuzzystrmatch;
CREATE EXTENSION postgis_tiger_geocoder;

-- Table: public.abstract_reporting
-- DROP TABLE public.abstract_reporting;
CREATE TABLE abstract_reporting (
    id character varying(255) COLLATE pg_catalog."default" NOT NULL,
    geographic_type character varying(50) COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT abstract_reporting_pkey PRIMARY KEY (id)
) WITH (OIDS = FALSE) TABLESPACE pg_default;

ALTER TABLE abstract_reporting OWNER to signalisationusr;