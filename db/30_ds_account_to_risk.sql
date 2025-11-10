-- raw.ds_account_to_risk definition

-- Drop table

-- DROP TABLE raw.ds_account_to_risk;

CREATE TABLE IF NOT EXISTS raw.ds_account_to_risk (
	ds_id numeric(19) NOT NULL,
	id numeric(19) NOT NULL,
	access_code varchar(10) NULL,
	cash_filter varchar(32) NULL,
	id_origin numeric(19) NULL,
	security_filter varchar(32) NULL,
	"version" numeric(10) NULL,
	account_id numeric(19) NULL,
	risk_unit_id numeric(19) NULL,
	unique_id varchar(64) NULL,
	portfolio_name varchar(32) NULL,
	insert_date date NULL,
	insert_date_time timestamp NULL
);
