DO $$
DECLARE
    schema TEXT := 'raw';
	table_list TEXT[] := ARRAY[
        'kpi_config',
        'connector_param',
        'currency_ref',
        'ds_client_entity',
        'ds_exposition_vacation',
        'ds_portfolio',
        'ds_portfolio_to_risk',
        'ds_risk_unit',
        'ds_super_client',
        'ds_algo_external_results'
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
