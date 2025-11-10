-- raw.kpi_config definition

-- Drop table

-- DROP TABLE raw.kpi_config;

CREATE TABLE IF NOT EXISTS raw.kpi_config (
	kpi_id text NULL,
	kpi_name text NULL,
	unit text NULL,
	threshold_upper_value numeric NULL,
	threshold_lower_value numeric NULL,
	target_field_window_values numeric NULL,
	target_field_window_unit text NULL,
	severity text NULL,
	breach_on text NULL,
	control_ts timestamp NULL,
	created_at_utc timestamp NULL
);
