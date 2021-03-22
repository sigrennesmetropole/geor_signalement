-- Ajout de la colonne functional_status à la table abstract_reporting
ALTER TABLE abstract_reporting ADD functional_status VARCHAR(100);


-- Initialisation des valeurs de functional_status (functional_status = status)
UPDATE abstract_reporting SET functional_status = status WHERE functional_status IS NULL;


-- Ajout de la contrainte de non null sur le champ functional_status
ALTER TABLE abstract_reporting ALTER COLUMN functional_status SET NOT NULL;
