# Managing Dependencies

## Managing Server-Side Dependencies

- Dependency management tool: Gradle
- Library source: [JCenter](https://bintray.com/bintray/jcenter)
- Configuration: `build.gradle`

To update your local library configuration:

- If you are using command line, your next build will automatically reflect the change.
- If you are using Eclipse, right click on the project in the Project Explorer and select `Gradle → Refresh Gradle Project` for the changes to be reflected.
- If you are using IntelliJ, dependencies are automatically refreshed as soon as changes to the file are detected (assuming auto-import is enabled).

## Managing Client-Side Dependencies

- Dependency management tool: NPM
- Library source: [NPM registry](https://www.npmjs.com)
- Configuration:
  - Production dependencies: `src/main/resources/package.json`
  - Development dependencies: `package.json`

If a library cannot be found in the NPM registry, simply host a local copy in the repository.

To update your local library configuration:

- For production dependencies, only if you are adding/updating the library, find the files from the library that are necessary to be loaded to webpages, and add/update the entry(ies) in `FrontEndLibrary.java`.
- For development dependencies, run `npm install` from the project root folder.
