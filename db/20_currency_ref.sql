-- raw.currency_ref definition

-- Drop table

-- DROP TABLE raw.currency_ref;

CREATE TABLE IF NOT EXISTS raw.currency_ref (
	currency_code varchar(3) NOT NULL,
	decimal_number numeric(10) NOT NULL,
	"label" varchar(96) NULL,
	"version" numeric(10) NULL,
	access_code varchar(10) NOT NULL DEFAULT 'WORKSPACE'::character varying,
	id_origin varchar(3) NULL,
	cut_off time NULL DEFAULT to_timestamp('01/01/2015 12:00'::text, 'DD/MM/YYYY HH24:MI'::text)
);
