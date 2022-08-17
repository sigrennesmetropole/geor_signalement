DROP VIEW IF EXISTS signalement.context_geographic_area;

CREATE VIEW signalement.context_geographic_area AS
SELECT DISTINCT
    contextDescription.id AS id_context,
    userSignalement.first_name AS first_name,
    userSignalement.last_name AS last_name,
    userRole.name AS role_name,
    contextDescription.name AS context_name,
    geographicArea.nom AS area_name,
    geographicArea.codeinsee AS code_insee,
    geographicArea.geometry AS geometry

FROM signalement.context_description AS contextDescription
    LEFT JOIN signalement.user_role_context AS userContext ON contextDescription.id = userContext.context_description_id
    LEFT JOIN signalement.user_ AS userSignalement ON userContext.user_id = userSignalement.id
    LEFT JOIN signalement.role AS userRole ON userRole.id = userContext.role_id
    LEFT JOIN signalement.geographic_area AS geographicArea ON geographicArea.id = userContext.geographic_area_id;

GRANT SELECT ON VIEW signalement.context_geographic_area TO signalement;