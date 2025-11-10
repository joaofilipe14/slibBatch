CREATE TABLE IF NOT EXISTS raw.ds_super_client (
	ds_id numeric(19) PRIMARY KEY,
	id numeric(19) NOT NULL,
	access_code varchar(10) NOT NULL,
	credit_line numeric(10) NULL,
	"name" varchar(32) NOT NULL,
	id_origin numeric(19) NULL,
	"version" numeric(10) NULL,
	reference_currency_ref_id varchar(3) NOT NULL,
	audit_entry_ts timestamp NULL,
	audit_entry_ts_prev timestamp NULL,
	audit_entry_ts_last timestamp NULL,
	audit_validation_ts_last timestamp NULL,
	unique_id varchar(64) NULL,
	user_id_last_change varchar(35) NULL,
	simulation bool NOT NULL,
	insert_date date NULL,
	insert_date_time timestamp NULL
);
