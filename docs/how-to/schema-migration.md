<frontmatter>
  title: "Schema Migration"
</frontmatter>

# Schema Migration

TEAMMATES uses [Liquibase](https://docs.liquibase.com/start/home.html) to manage database schema changes. Changelogs are written in XML and live in `src/main/resources/db/changelog/migrations/`.

## Configuration

The following variables can be configured in `gradle.properties`. Defaults are pre-configured for local development.

- `liquibaseDbUrl`
- `liquibaseUsername`
- `liquibasePassword`

## Applying Migrations

<tabs>
<tab header="Mac / Linux">

```sh
./gradlew liquibaseUpdate
```

</tab>
<tab header="Windows">

```sh
gradlew.bat liquibaseUpdate
```

</tab>
</tabs>

Run this after pulling changes that include new migration files, or after a fresh database setup.

## Generating a Migration

1. Apply all existing migrations and verify the server starts cleanly:

<tabs>
<tab header="Mac / Linux">

```sh
./gradlew liquibaseUpdate
./gradlew serverRun
```

</tab>
<tab header="Windows">

```sh
gradlew.bat liquibaseUpdate
gradlew.bat serverRun
```

</tab>
</tabs>

2. Make your entity/model changes.

3. Generate a diff changelog:

<tabs>
<tab header="Mac / Linux">

```sh
./gradlew liquibaseDiffChangelog -PmigrationName=<descriptive-name>
```

This compares your Hibernate entity model against the live database and produces the necessary changesets. Review and edit the output before committing.

</tab>
<tab header="Windows">

```sh
gradlew.bat liquibaseDiffChangelog -PmigrationName=<descriptive-name>
```

This compares your Hibernate entity model against the live database and produces the necessary changesets. Review and edit the output before committing.

</tab>
</tabs>

<box type="info">
  <md>
Liquibase auto-generates rollback for most change types. If you use a `<sql>` tag, add a `<rollback>` block manually.
  </md>
</box>

## Rolling Back

To roll back the last N changesets:

<tabs>
<tab header="Mac / Linux">

```sh
./gradlew liquibaseRollbackCount -Pcount=<N>
```

</tab>
<tab header="Windows">

```sh
gradlew.bat liquibaseRollbackCount -Pcount=<N>
```

</tab>
</tabs>

Note that a single migration file may contain multiple changesets — check the file to determine the correct count.

## Schema Validation Failures

Hibernate validates the database schema on every startup. If the schema does not match the entity model, the server will fail to start. This usually means new migrations have not been applied yet.

**Recovery steps:**

1. Apply missing migrations and restart:

<tabs>
<tab header="Mac / Linux">

```sh
./gradlew liquibaseUpdate
./gradlew serverRun
```

</tab>
<tab header="Windows">

```sh
gradlew.bat liquibaseUpdate
gradlew.bat serverRun
```

</tab>
</tabs>

2. If that does not resolve it, you may have to reset the database as a last resort:

<tabs>
<tab header="Mac / Linux">

```sh
docker compose down
rm -rf postgres-data
docker compose up -d
./gradlew liquibaseUpdate
./gradlew serverRun
```

</tab>
<tab header="Windows">

```sh
docker compose down
rm -rf postgres-data
docker compose up -d
gradlew.bat liquibaseUpdate
gradlew.bat serverRun
```

</tab>
</tabs>
