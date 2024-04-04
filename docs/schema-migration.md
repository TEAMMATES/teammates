<frontmatter>
  title: "Schema Migration"
</frontmatter>

# SQL Schema Migration

Teammates uses _[Liquibase]_(https://docs.liquibase.com/start/home.html), a database schema change management solution that enables developers to revise and release database changes to production. The maintainers in charge of releases (Release Leader) will be in charge of generating a _Liquibase_ changelog prior to each release to keep the production databases schema in sync with the code. Therefore this section is just for documentation purposes for contributors.

## Liquibase in Teammates
_Liquibase_ is made available using the [gradle plugin](https://github.com/liquibase/liquibase-gradle-plugin), providing _liquibase_ functions as tasks. Try `gradle tasks | grep "liquibase"` to see all the tasks available. In teammates, change logs (more in the next section) are written in _XML_.

### Liquibase connection
Amend the `liquibaseDbUrl`, `liquibaseUsername` and `liquibasePassword` in `gradle.properties` to allow the _Liquibase_ plugin to connect your database.

## Change logs, change sets and change types
A _change log_ is a file that contains a series of _change sets_ (analagous to a transaction) which applies _change types_ (actions). You can refer to this page on liquibase on the types of [change types](https://docs.liquibase.com/change-types/home.html) that can be used.

## Generating/ Updating liquibase change logs
1. Amend the `build.gradle` to specify the path to the your changelog file e.g `src/main/resources/db/changelog/db.changelog-<release_number>.xml`
2. Ensure `diff-main` activity in `build.gradle` is pointing to the latest release changelog
3. Delete the `postgres-data` folder to clear any old database schemas
4. Run `git checkout <reference_branch>` and 
5. Run the server using `./gradlew serverRun` to generate tables found on branch
6. Generate snapshot of database by running `./gradlew liquibaseSnapshot -PrunList=snapshot`, the snapshot will be output to `liquibase-snapshot.json`
7. Checkout your branch and repeat steps 3 and 5 to generate the tables found on your branch
8. Run `./gradlew liquibaseDiffChangeLog -PrunList=diffMain` to generate changeLog to resolve database schema differences

