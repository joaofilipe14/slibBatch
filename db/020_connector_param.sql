CREATE TABLE IF NOT EXISTS raw.connector_param (
	id varchar(255) PRIMARY KEY,
	fin_fil_eau time NULL,
	flow_reference varchar(150) NULL,
	gscp bool NULL DEFAULT false,
	heure_max_arrete time NULL,
	identification varchar(64) NOT NULL,
	client_setup bool NOT NULL DEFAULT false,
	periodicite numeric(19) NULL,
	time_lag numeric(19) NULL,
	"type" varchar(20) NULL,
	"version" numeric(10) NULL,
	flow_direction varchar(10) NOT NULL DEFAULT 'INPUT'::character varying,
	btf_instance_name varchar(32) NULL,
	ccp varchar(32) NULL,
	algorithm varchar(32) NULL
);
