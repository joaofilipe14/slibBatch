### 🛠️ Prerequistes

- Have [psql cli tool](https://www.postgresql.org/docs/current/app-psql.html) installed

### ▶️ How to run

The restart scripts are implemented by layer **(raw, bronze, silver, gold)**. Run each script according to layer you want to reset.

**Mac OS / Linux**

```bash
psql -U {db_user} -d {target_db_name} < {path/to/file}.sql
```

For **silver** and **gold** there's a input argument called `delete_date_var` to be used for the deleting the rows that are older than the passed date in `yyyy-mm-dd hh:mm:ss` format

```bash
psql -U {db_user} -d {target_db_name} -v delete_date_var='{older_than_date}' < {path/to/file}.sql
```

**Windows / powershell**

```bash
Get-Content -Path "{path/to/file}.sql" -Raw | psql -U "{db_user}" -d "{target_db_name}" -v delete_date_var='{older_than_date}'
```
