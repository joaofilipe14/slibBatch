-- raw.ds_algo_external_results definition

-- Drop table

-- DROP TABLE raw.ds_algo_external_results;

CREATE TABLE IF NOT EXISTS raw.ds_algo_external_results (
	ds_id bigserial NOT NULL,
	id numeric(19) NOT NULL,
	ptf_key varchar(100) NULL,
	ccp_account_id varchar(50) NULL,
	ccp_id varchar(20) NULL,
	tp_id varchar(50) NULL,
	bucket_id varchar(50) NULL,
	member_id varchar(50) NULL,
	algorithm varchar(50) NOT NULL,
	computation_datetime timestamp NULL,
	currency varchar(3) NULL,
	mr numeric(24,8) NULL,
	im numeric(24,8) NULL,
	vm numeric(24,8) NULL,
	am numeric(24,8) NULL,
	lcrm numeric(24,8) NULL,
	wwrm numeric(24,8) NULL,
	cf numeric(24,8) NULL,
	extraction_id timestamp NULL,
	status varchar(20) NULL,
	context varchar(20) NULL,
	created_by_user varchar(50) NULL,
	approved_by_user varchar(50) NULL,
	created_at timestamp NOT NULL,
	account_id numeric NOT NULL,
	operation varchar(50) NOT NULL,
	insert_date date NULL DEFAULT CURRENT_DATE
);
