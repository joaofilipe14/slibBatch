-- Truncate
DO $$
DECLARE
    schema TEXT := 'analytics_silver';
	table_list TEXT[] := ARRAY[
        'slv_dim_client',
        'slv_dim_currency',
        'slv_dim_portfolio_risk_unit',
        'slv_param_alert_config',
        'slv_dim_kpi',
        'slv_dim_ccp'
    ];
	current_table TEXT;
BEGIN
    RAISE NOTICE 'Starting truncate procedure for %', schema;
    FOREACH current_table IN ARRAY table_list
    LOOP
        IF EXISTS (
            SELECT 1 
            FROM information_schema.tables
            WHERE 
                table_schema = schema
                AND table_name = current_table
        ) THEN
            RAISE NOTICE 'Truncating table: %.%', schema, current_table;
            EXECUTE format('TRUNCATE TABLE %I.%I;', schema, current_table);
        ELSE
            RAISE NOTICE 'Skipping table: %.%. It does not exist.', schema, current_table;
        END IF;
    END LOOP;
	RAISE NOTICE 'Truncate operations complete.';
END
$$;

SET filter.delete_date = :'delete_date_var';

-- Delete
DO $$
DECLARE
    schema TEXT := 'analytics_silver';
	table_list TEXT[] := ARRAY[
        'slv_fct_portfolio_margin_eod_ccp',
        'slv_fct_portfolio_margin_eod_rms',
        'slv_fct_portfolio_margin_eod',
        'slv_param_alert_config',
        'slv_kpi_04_mr_discrepancy',
        'slv_kpi_05_mr_rollingavg',
        'slv_kpi_06_mr_rollingavg_py',
        'slv_kpi_09_mr_observ_exceed',
        'slv_kpi_10_mr_zscore',
        'slv_fct_portfolio_alert_events'
    ];
	current_table TEXT;
    delete_date DATE := current_setting('filter.delete_date')::DATE;
BEGIN
    RAISE NOTICE 'Starting delete procedure for % schema: Will delete all records older than %', schema, delete_date;
    FOREACH current_table IN ARRAY table_list
    LOOP
        IF EXISTS (
            SELECT 1 
            FROM information_schema.tables
            WHERE 
                table_schema = schema
                AND table_name = current_table
        ) THEN
            RAISE NOTICE 'Deleting from table: %.%', schema, current_table;
            EXECUTE format('DELETE FROM %I.%I WHERE control_ts = $1;', schema, current_table)
            USING delete_date;
        ELSE
            RAISE NOTICE 'Skipping table: %.%. It does not exist.', schema, current_table;
        END IF;
    END LOOP;
	RAISE NOTICE 'Delete operations complete.';
END
$$;
