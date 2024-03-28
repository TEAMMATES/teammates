<frontmatter>
  title: "Schema Migration"
</frontmatter>

# SQL Schema Migration

Teammates uses _[Liquibase]_(https://docs.liquibase.com/start/home.html), a database schema change management solution that enables developers to revise and release database changes to production. If you were to change the schema of any entity, you would have to create a _Liquibase_ changelog which a maintainer will run to keep the production databases schema in sync with the code.

## Liquibase in Teammates
_Liquibase_ is made available using the [gradle plugin](https://github.com/liquibase/liquibase-gradle-plugin), providing _liquibase_ functions as tasks. Try `gradle tasks | grep "liquibase"` to see all the tasks available. In teammates, change logs (more in the next section) are written in _XML_.

### Liquibase connection
Amend the `liquibaseDbUrl`, `liquibaseUsername` and `liquibasePassword` in `gradle.properties` to allow the _Liquibase_ plugin to connect your database.

## Change logs, change sets and change types
A _change log_ is a file that contains a series of _change sets_ (analagous to a transaction) which applies _change types_ (actions). You can refer to this page on liquibase on the types of [change types](https://docs.liquibase.com/change-types/home.html) that can be used.

## How to use Liquibase in Teammates
1. Create an _XML_ change log file in `src/main/resources/db/changelog` naming convention is the `db.changelog-YYYY-MM-DD-entity.xml` e.g `db.changelog-2024-03-24-courses.xml`.
2. Add changelog file to be included in the `db.changelog-root` as the last entry

## Generating liquibase change logs
1. Delete the `postgres-data` folder to clear any old database schemas
2. Run `git checkout master` and 
3. Run the server using `./gradlew serverRun` to generate tables found on master
4. Generate snapshot of database by running `./gradlew liquibaseSnapshot -PrunList=snapshot`, the snapshot will be output to `liquibase-snapshot.json`
4. Checkout your branch and repeat steps 1 and 3 to generate the tables found on your branch
5. Run `./gradlew liquibaseDiffChangeLog -PrunList=diffMain` to generate changeLog to resolve database schema differences
6. Rename this file to the format `db.changelog-YYYY-MM-DD-entity.xml` and add it as a changelog in `db.changelog-root`

