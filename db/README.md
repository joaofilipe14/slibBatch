### 🛠️ Prerequistes

- Have [psql cli tool](https://www.postgresql.org/docs/current/app-psql.html) installed

### ▶️ How to run

**Mac OS / Linux**

```bash
cat {ddl_scripts_folder}/*.sql | psql -U {db_user} -d {target_db_name}
```

**Windows / powershell**

```bash
Get-Content -Path "{ddl_scripts_folder}\*.sql" -Raw | psql -U "{db_user}" -d "{target_db_name}"
```
