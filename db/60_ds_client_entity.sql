CREATE TABLE IF NOT EXISTS raw.ds_client_entity (
	ds_id numeric(19) PRIMARY KEY,
	id numeric(19) NOT NULL,
	access_code varchar(10) NOT NULL,
	credit_line numeric(10) NULL,
	is_real_time_oex_feed bool NOT NULL DEFAULT false,
	"name" varchar(32) NOT NULL,
	id_origin numeric(19) NULL,
	"version" numeric(10) NULL,
	reference_currency_ref_id varchar(3) NOT NULL,
	super_client_id numeric(19) NOT NULL,
	unique_id varchar(64) NULL,
	simulation bool NOT NULL,
	insert_date date NULL,
	insert_date_time timestamp NULL
);
