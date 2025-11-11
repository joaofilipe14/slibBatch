CREATE TABLE IF NOT EXISTS raw.ds_portfolio_to_risk (
	ds_id numeric(19) PRIMARY KEY,
	id numeric(19) NOT NULL,
	access_code varchar(10) NOT NULL,
	id_origin numeric(19) NULL,
	"version" numeric(10) NULL,
	unique_id varchar(64) NULL,
	risk_unit_id numeric(19) NOT NULL,
	portfolio_type varchar(10) NOT NULL,
	portfolio_name varchar(32) NOT NULL,
	algorithm varchar(32) NOT NULL,
	portfolio_excluded bool NOT NULL,
	insert_date date NULL,
	insert_date_time timestamp NULL,
	lcn varchar(12) NULL
);
