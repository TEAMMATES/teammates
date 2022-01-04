# Working with workflow automation files 

This is the main documentation meant for TEAMMATES developers who wish to contribute towards automating various workflows on the github repository.

Some background to the automation project can be found here: https://github.com/TEAMMATES/teammates/wiki/Automation-Hero.

## Overview of features

Briefly, the following are the tasks that have been automated in this repository. These have been automated by using customly written actions in github workflows.
 
1. Editting labels assigned to PRs when they are newly opened, or when they are converted from a draft PR to a ready-for-review PR or vice versa on the github webpage.

1. Editting labels assigned to PRs with the s.ToReview or s.FinalReview labels, when the latest commit fails checks.

1. Restoring review labels when the PR author gets the checks passing once again.

These pr-management related features were prioritised for implementation after some discussion with experienced TEAMMATES contributers. Hence do discuss with them before starting any PRs related to adding to these features.

# Organisation of files

Here is how the files related to workflow automation have been organised.

* **./.github/workflow**
  * yaml files that trigger a workflow will be found here
  * The github actions api searches this exact path for workflow files so this folder should not be renamed
  * Not all files in this folder belong to the pr-management project. Some are existing continuous integration files.
* **./.github/pr-management**
  * All files that are related to automating pr management have been grouped under this folder
* **./.github/pr-management/common**
  * This folder contains all the common code that can be used by multiple actions 
  * Functions that need to be used by different actions can be kept in this common folder to avoid reuse
* **./.github/pr-management/common/github-helper**
  * This folder contains modules that abstract away the github api (i.e. usages of the octokit module) 
  * The different files in this folder correspond to the different apis that github provides, e.g. the issues api, pulls api, etc., for convenience of keeping each file small and organised. If usages of other github apis become necessary, more files can be created.
  * There is an interface.js file in this folder which exports the all the other files. As such, to import from the github-helper folder into the main.ts files, you can do the following:

      ```
      import { … } from '../common/github-manager/interface';
      ```
* **./.github/pr-management/{custom-written-action}** 
  * Except for the common folder, every folder in pr-management belongs to this category. Each of these folders contain a custom action written in javascript
  * These are the files usually in this folder
    * **action.yaml** - This file contains all necessary metadata for the action, including the inputs that the actions takes, and what files the action runs. 
    * **main.ts** - This file has the code that encapsulates the logic that runs during the action. 
    * **index.js** - Once the main.ts file is built and minified using the ncc module, this index.js file is outputted. The action.yaml file makes a reference to this file so this file must be committed.


# Process of creating a new workflow

This section is an overview of the steps needed to create a new workflow for the automation project. It can also help shed light on how the many different files mentioned above were created progressively. 

When making a new workflow, these are the steps generally needed: 

1. Create a yaml file in `.github/workflow` to trigger that workflow. This workflow file needs to specify what events to be triggered on and what steps to execute when the workflow is running. ([Here's the documentation](https://docs.github.com/en/actions/learn-github-actions/workflow-syntax-for-github-actions) for the syntax in these workflow files.)

1. For each `step` in the workflow, you can specify an action to run. This action can be an action in the marketplace or a customly written action. In this repo, it is generally preferred to use the latter. 
You can make a workflow run a customly written actions by pointing the `uses:` key in the yaml file to a directory that contains your `action.yaml` file, for example: 

    ```yaml
    - name: Check if a PR is a draft and assign label s.Ongoing
        uses: ./.github/pr-management/pr-marked-draft
        with:
            repo-token: ${{secrets.GITHUB_TOKEN}}
    ```

1. Write your custom action in a typescript file. This will contain the logic of what is supposed to happen when the workflow is triggered. You can name this file `main.ts` and put it in a directory that describes the action. 

1. For committing purposes, build and minify your `main.ts` files to create the license file and a `index.js` file.

1. In the `action.yaml` file, specify the metadata needed for the action [(metadata documentation)](https://docs.github.com/en/actions/creating-actions/metadata-syntax-for-github-actions#inputs), including the location of the javascript file to run. Currently, in this repo, each action.yaml file, its corresponding javascript and typescript files are kept in the same directory.

Generally, these are the new files you would have added to the repository when creating a new workflow:

    |-- ./.github/workflow/{workflow-file-name}.yaml
    |-- ./.github/pr-management/{custom-action-name}/
        |-- action.yaml
        |-- main.ts
        |-- index.js

# Setting up

The process of setting up this project is similar to any node project. Simply install the dependencies using `npm i`.

# Main dependencies (node modules this project depends on)

There are two main modules that custom actions depend on - `@actions/core` and `@actions/github`. The core module provides important functionality such as logging output that you can view after the execution of a workflow. This can also be useful for debugging.

The `@actions/github` module provides an api that you can call to access specific issues, and other information that's related to the teammates repository. 

## The need for @vercel/ncc 

For committing the custom actions into the repository, the ncc module by vercel is needed.

Normally, github actions run javascript files and require the node_modules folder with their dependencies to be checked in.

However, to keep the repository cleaner we want to avoid committing node_modules in TEAMMATES. We also want to take advantage of typescript's features for easier debugging of code so we write in typescript instead of javascript directly. 

Hence, a workaround is after coding in a typescript file, we minify all the dependencies and the code into one output file, a functionality that the `ncc` module provides. This output file can be then committed so that it can be used during a workflow run. 

Do note that the ncc command also depends on the tsconfig.json file.


# Commands to run before committing

Before minifying the main.ts file, you should run the `tsc` command to check for any warnings generated by the typescript compiler. 

Minifying the main.ts file can be done from the github folder using: 

`ncc build {main.ts file path} -o {output file path} -m --license licenses.txt`

The command needs to be repeated for each file to minify.

To simplify things, the command for building all main.ts files into index.js files is added as a script in package.json. This script can be executed by doing `npm run actions:minify`.

The command to check for typescript errors and then build can be executed in one step with `npm run actions`. 

When new main.ts files are added into this repository, package.json should be updated.

:exclamation: Do remember to rebuild the index.js files before every commit so that changes to made to main.ts files are reflected on the next workflow run. 

# Troubleshooting

If you ever see your changes to the actions you have written not being reflected in the next workflow run, these may be some of the issues:

1) Changes to some workflows need to be committed into the main branch before you can run them. This may depend on the github api, for example, the [github documentation](https://docs.github.com/en/actions/learn-github-actions/events-that-trigger-workflows#issue_comment) specifies that workflows that are trigged on 'issue comment' events must be committed to the main branch first.

1) The index.js file has not been rebuilt yet, after making changes to the typescript files. 

# Glossary of some terms used

## Custom actions vs workflows

A workflow is triggered when events specified in the workflow file occur in the repo, such as a PR is created or a comment is added to a PR.
				
An action is code that can be referred to and ran during a workflow run, reducing the amount of code you need to write in your workflow yaml files. 

## github.context.payload

When a workflow is triggered, there is a payload object that is available to reference in your javascript action. In the current version of the api, this payload object is accessed using `github.context.payload` in the typescript files.

The payload object contains different properties depending on the events that triggered the workflow, e.g. if it was a new commit to the PR that triggered the workflow*, or if it was a new 'issue comment' that triggered it. Properties that are contained in the payload object are specified in this [github documentation](https://docs.github.com/en/developers/webhooks-and-events/webhooks/webhook-events-and-payload)

*This falls under what github calls a `synchronize` event.

## Functions that refer to a current PR 

Function names that mention a current pr or current issue (e.g. `getCurrentPr()`) mean that api calls made in the function use the `issue_number` from the `github.context` object. 

# Extra resources

For writing the logic of your custom action, the following resources might be helpful: 

* Rest api documentation: https://octokit.github.io/rest.js/v18
* Github tutorial: https://lab.github.com/githubtraining/github-actions:-writing-javascript-actions
