#TEAMMATES Development Process

##Roles and Positions
**Roles**:
* `Dev` - Issue owner who fixes the issue. 
* `Reviewer` - Assigned per issue. Usually, a core team member.
* `Project Manager` (PM) - General project coordination and deploying to the live server.

Note: *Roles* are related to the development process and they are different from *Positions*, which relate to
the organization structure of the TEAMMATES dev community. 

##Workflow

<img src='images/workflow.png' width='600'>

###Fixing Issues

Role: Dev

{If you need any help regarding the workflow, please [post a new issue in our issue tracker] 
(https://github.com/TEAMMATES/teammates/issues/new) (Yes, our issue tracker doubles as a discussion board).}

This workflow is an adaptation of the [GitHub flow](https://guides.github.com/introduction/flow/index.jsp).

1. Select an issue to handle. If you are a contributor, there is no need to get the issue assigned to you. <br>
   (If you are a committer, assign the issue to yourself and assign it a milestone).

2. Optionally, you can discuss alternative solutions before choosing one to implement. 
   This can be done through Issue tracker. 
   Such a discussion reduces the chance of the fix being rejected later.

3. If the issue is assigned to you, a Pull Request (PR) is expected to be opened for it within a week. 
   Inactivity for a longer time may result in the issue being un-assigned so that
   someone else can work on it.

5. Update your local repo (the one you created when setting up the project on your computer) 
   with the latest version of the code from the committer repo.
   ```
   git pull upstream master
   ``` 

7. Start a new branch named `{IssueNumber}-{some-keywords}`.
   If you are already working in a branch, remember to switch to the `master` 
   before creating the new branch. e.g.,
    ```
    //switch to master (if not already on the master)
    git checkout master
    //create new branch and switch to it at the same time e.g. git checkout -b 2342-remove-println
    git checkout -b {branch-name}
    ```
8. Fix the issue.
   * Have a look at our coding and testing best practices (links given [here]
   (README.md)) before you start your first issue.
   * Keep in mind that we have 'reference' code that has extra explanatory 
   notes to help new developers. These are listed in the 'Coding Best Practices' document.
   * You may commit as many times as you wish while you are fixing the code. 
       * Push your commits frequently. If you have push privileges, 
         push to the committer repo. If not, push to your fork.
       * Try to keep the branch reasonably clean (e.g. commit at meaningful points)
       * use meaningful commit messages. (e.g. `added tests for the truncate method`) <br>
         Do NOT use the following format for commit messages. 
         That format is reserved for merge commits only.<br>
         ~~`[Issue number] Issue title as given in the original issue`~~ 
   * Sync with the committer repo frequently: While you were fixing the issue, others 
   might have pushed new code to the committer repo. In that case, update your 
   repo's master branch with any new changes from committer repo and merge those 
   updates to the branch you are working on.
        
       ```
       //switch to master and sync with committer repo
       git checkout master
       git pull upstream master       
       //merge updates into working branch
       git checkout {branch-name}
       git merge master
       ```

   * If there are updates to the dependencies on the build configuration, you should update your local
   copies accordingly. The details on the steps can be found on [this document](dependencies.md).

9. When the work is ready for review:
   * Format the code: Select the code segments you modified and apply the code 
     formatting function of Eclipse (`Source → Format`). 
     This is to ensure that the code is properly formatted. 
     You may tweak the code further to improve readability as auto-format 
     doesn't always result in a good layout.

   * Ensure that the code passes static analysis.
     The details on how to run static analysis locally is given on [this document](staticAnalysis.md).
        
   * Ensure _dev green_ (i.e., all *local* tests are passing on dev server).
     
     Local tests can be run using the "Local Tests" run configuration in Eclipse.

    >If any of the browsertests fail, use [*GodMode*](godmode.md) to fix them. 
    Ensure that dev green is reached without GodMode before submitting for review
     
    >If your new code might behave differently on a remote server than how it 
     behaves on the dev server, ensure those tests are passing against the 
     updated app running on your own GAE staging server.
        
   * Push your branch to the committer repo (push to the fork if you do not 
     have push permission to the committer repo), if you haven't done that already.
   
   * Create a pull request (PR). For the pull request name, copy paste
     the relevant issue name and include the issue number as well.<br>
     e.g. ` Incorrect error message when adding an existing instructor #1760`<br>
     In the PR description, mention the issue number in this format: `Fixes #1760`. 
     Doing so will [automatically close the related issue once the pull request is merged.](https://github.com/blog/1506-closing-issues-via-pull-requests)<br>

   * Once a PR is opened, the CI server will first run static analysis on the code base.
     If there are problems found, the build will terminate without proceeding to testing.
     Some of the tools will display the cause of the failures in the console; if this is not the case,
     you can run any of the static analysis tools and obtain the reports locally.
     Ensure that the static analysis passes before triggering another CI build.

     Once the code base passes static analysis, the CI server will build and test it. Ensure that the
     build is successful. If the some tests fail, look at the CI log and fix any tests that
     failed. Repeat until all tests pass on the CI server.
     
     If tests fail on the CI server, the CI log will contain the command that will enable running the failing tests locally.
    

   * The PR will be assigned to the reviewer, not to you. 
     Wait for a reviewer to be assigned to the issue.
     Feel free to add a comment asking for a reviewer if a reviewer is not assigned
     within 24 hours.

   * Wait for reviewer to change the PR status to `s.toMerge` 
     or to suggest changes. If you did not get a review within 2 days, 
     it is OK to request for a review by posting a comment in the PR.

   * Once the PR is open, try and complete it within 2 weeks. Inactivity for a 
     longer period would necessitate a restart of the PR.

   * The cycle of 'update pull request' and 'review' is to continue until PR 
     status changes to `s.ToMerge`. After doing suggested changes, 
     remember to add a comment to indicate the PR is ready for review again.
     e.g. `ready to review` or `changes done`<br>
     If you have permissions to change labels, use the `s.ToReview` to indicate
     the PR is ready for review, and the `s.Ongoing` label to indicate the PR
     is not yet ready to be reviewed.

   * As a final check, the PM will look through the changes and either suggest changes (back to `s.Ongoing`),
     or apply the `s.MergeApproved` label to the PR.
   

###Reviewing a fix
Role: reviewer

  * Ensure that the Travis CI build is successful and the developer has local dev green.
  * You are the reviewer for a PR if you are the `assignee` of it.
  * Ensure the following:
    * The solution is the best possible solution to the problem under the 
      circumstances.
    * Tests have been updated to reflect changes to the functional code. Almost 
      all code changes should have changes to both functional code and test code.
    * User documentation has been updated, if required. e.g. help pages.
    * Developer documentation has been updated, if required. e.g. `design.md`
    * The changeset does not contain changes unrelated to the issue. 
      e.g. unnecessary formatting changes.
    * The code is synced with upstream. GitHub should show it as 'can merge'. 
      If not, ask the dev to sync with upstream. 
    * Ensure appropriate header comments and expected standards are followed
      * the standards used in TEAMMATES are available [here](README.md) under the *Supplementary documents* section
  * If any of the above are not OK, 
    * change the status of the PR to `s.Ongoing`
    * Add comments in the diff to suggest changes.
    * Optionally, add a comment in the conversation thread to inform the author to refine the code.
  * To remove white space changes from being shown, append `?w=1` to url of the `/files` page of the pull request (the "Files changed" tab)
  * If the code is OK on all aspects,
    * Change issue status to `s.ToMerge`

Role: PM

  * Review the code for maintainability and style
  * If the above is not OK,
    * Change the issue status to `s.Ongoing`
  * If the code is OK on all aspects,
    * Change issue status to `s.MergeApproved`


###Applying a fix
Role: dev (with push permission), or reviewer

  * Merging can be done via GitHub. Make sure that GitHub gives a green light for merging.
    There are a few scenarios where GitHub can prevent merging from proceeding:
    * **Merge conflict**: The PR is conflicting with the current `master` branch; the author will
      need to resolve the conflicts before proceeding.
    * **Outdated branch**: The PR is not in sync with the current `master` branch; the author will
      need to sync it before proceeding. This can be done via GitHub with the "Update branch" button.
    
    The dev will need to resolve them before merging can proceed. It is up to the dev/reviewer's discretion
    on whether the merge conflict or outdated branch needs another review to be called for.
    In general, unless the changeset is functionally conflicting, there is no need for another review.
  * When GitHub gives a green light for merging,
    * Checkout to the PR branch, merge with the current `master` branch, and test the code locally by running the "Local tests".<br>
      `git checkout -b 2287-add-sample-course-test origin/2287-add-sample-course-test`<br>
      `git merge master`<br>
    * If green,
      * Merge with "squash and merge" option (preferable). Format of the commit message:<br>
        `[Issue number] Issue title as given in the original issue`<br>
        e.g. `[2287] Add more tests for newly joined Instructor accessing sample course`<br>
        The additional descriptions can be left as is.
      * Optionally, apply an `e.*` label to the issue (not the PR) to indicate 
        the estimated effort required to fix the issue, and another `e.*` label to the PR
        to indicate the estimated effort required to review the PR.
    * If not green,
      * Change the pull request status to `s.Ongoing`.
      * Add a comment to mention the test failure.
