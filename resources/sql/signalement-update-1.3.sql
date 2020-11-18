alter table signalement.user_ add column if not exists email character varying(150)  NOT NULL;
alter table signalement.user_ add column if not exists first_name character varying(150);
alter table signalement.user_ add column if not exists last_name character varying(150);
alter table signalement.user_ add column if not exists organization character varying(150);
alter table signalement.user_ add column if not exists roles character varying(1024);

alter table signalement.geographic_area add column if not exists nom character varying(255);
alter table signalement.geographic_area add column if not exists codeinsee character varying(5);