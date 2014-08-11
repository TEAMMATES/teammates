#TEAMMATES Development Process

##Roles
* `Dev` - Issue owner who fixes the issue. Can be a Committer or a Contributor.
* `Reviewer` - Assigned per issue. Usually, a core team member.
* `Area lead` - Responsible for keeping all tests green, merging pull requests.
* `PM` (Project Manager) - General project coordination and deploying to the live server

##Workflow

<img src='../src/main/webapp/dev/images/workflow.png' width='600'>

###Fixing Issues

Role: Dev

{If you need any help regarding the workflow, please post in the 
[teammates-contributors Google group](https://groups.google.com/forum/?fromgroups#!forum/teammates-contributors) .}

This workflow is an adaptation of the [GitHub flow](https://guides.github.com/introduction/flow/index.html).

1. Select an issue to handle. Get it assigned to you. 
   Contributors can request for an issue to be assigned to you by posting a comment under the issue in concern.

2. Optionally, you can discuss alternative solutions before choosing one to implement. 
   This can be done through Issue tracker. 
   Such a discussion reduces the chance of the fix being rejected later.

0. If you do not have push permission to the committer repo, create a fork. 
   Add remote names for committer repo (let's call it `upstream`) 
   and your fork (let's call it `myfork`)
   ```
   git remote add   upstream      https://github.com/TEAMMATES/repo.git
   git remote add   myfork        https://github.com/your_user_name/repo.git
   ```

4. Update your local repo (the one you created when setting up the project on your computer) 
   with the latest version of the code from the committer repo.
   ```
   git pull upstream
   ```
4. Change the issue status to `status.Ongoing`

5. Start a new branch named `Issue{IssueNumber}`. 
   If you are already working in a branch, remember to switch to the `master` 
   before creating the new branch. e.g.,
    ```
    //switch to master (if not already on the master)
    git checkout master
    //create new branch and switch to it at the same time e.g. git checkout -b Issue236
    git checkout -b Issue{issue number}
    ```
6. Fix the issue.
   * Have a look at our coding and testing best practices (links given [here]
   (../README.md)) before you start your first issue.
   * Keep in mind that we have 'reference' code that has extra explanatory 
   notes to help new developers. These are listed in the 'Coding Best Practices' document.
   * You may commit as many times as you wish while you are fixing the code. 
       * Push your commits frequently. If you have push privileges, 
         push to the committer repo. If not, push to your fork.
       * Try to keep the branch reasonably clean (e.g. use meaningful commit messages). 
   * Sync with the committer repo frequently: While you were fixing the issue, others 
   might have pushed new code to the committer repo. In that case, update your 
   repo with any new changes from committer repo and merge those updates 
   to your branch
        
       ```
       git pull upstream 
       git merge master
       git commit -a -m "your commit message"
       ```

7. When the work is ready for review:
   * Format the code: Select the code segments you modified and apply the code 
     formatting function of Eclipse (`Source → Format`). 
     This is to ensure that the code is properly formatted. 
     You may tweak the code further to improve readability as auto-format 
     doesn't always result in a good layout.
        
   * Ensure _dev green_ (i.e., all tests are passing on dev server).
     
     >If your new code might behave differently on a remote server than how it 
     behaves on the dev server, ensure staging green 
     (i.e., all tests are passing against the modified app running on your own 
     GAE staging server).
        
   * Push your branch to the committer repo (push to the fork if you do not 
     have push permission to the committer repo), if you haven't done that already.
   
   * Create a pull request (PR). For the pull request name, copy paste the relevant
     issue name.<br>
     e.g. ` Incorrect error message when adding an existing instructor #1760`<br>
     In the comment, mention the issue number. Doing so will create an 
     automatic reference from the issue to the pull request.<br>
     e.g. ` For #1760`  
     
   * Change the PR status to `status.PendingReview`
   * Wait for the reviewer to change the PR status. If you did not get a review
     within 2-3 days, it is OK to request for a review by posting a comment in 
     the PR.  
   
   

###Reviewing a fix
Role: reviewer

  * This is a code quality review. No need to run tests.
  * Ensure the following:
    * The solution uses the best alternative solution to the problem.
    * Tests have been updated to reflect changes to the functional code. Almost 
    all code changes should have changes to both functional code and test code.
    * User documentation has been updated, if required. e.g. help pages.
    * Developer documentation has been updated, if required. e.g. `devman.html`
    * The changeset does not contain changes unrelated to the issue. 
    e.g. unnecessary formatting changes.
    * The code is synced with upstream. GitHub should show it as 'can merge'
  * If any of the above are not OK, 
    * Add comments to suggest changes.
    * Change pull request status to `status.Ongoing`
    * Optionally, add a comment to inform the author to refine the code.
  * If the code is OK on all aspects,
    * Change issue status to `status.ReadyToMerge`

###Applying a fix
Role: committer

  * Do not merge online. Always merge locally and push to the repo. If you 
  merge online, the commit message will not be in the format we want.
  * Format of the commit message: `[Issue number] Issue title as given in the original issue`<br>
    e.g. `[2287] Add more tests for newly joined Instructor accessing sample course`
  * Fetch code from upstream: <br>
    `git fetch origin`<br>
  * Checkout the branch and update with latest master<br>
    `git checkout -b Issue1234 origin/Issue1234`<br>
    `git merge master` <br>
  * Test the code. 
  * If green, 
    * Merge to master and push.<br>
      `git checkout master` <br>
      `git merge --no-ff Issue1234` <br>
      `git push origin master` <br>
    * Remove any status labels from the pull request.
    * Remove any status labels from the corresponding issue and close it.
  * If not green,
    * Delete the merge commit, if any.
    * Change the pull request status to `status.ongoing`
    * Add a comment to mention the test failure.
  
    
###Deploying fixes
Role: PM

  * Pull the latest master.
  * Get dev green.
  * Deploy.
  * Get live green.
  * Make the version default.
  * Tag the version. Format `V{major}.{minor}.{build}` e.g. `V5.01.02`.
  * Push to master.
  * Update milestone.
   

### Issue/PR Lifecycle
<img src='../src/main/webapp/dev/images/IssueLifecycle.png' width='600'>

Given above is an illustration of the issue lifecycle. 
Colors indicate which roles are involved in which states/transitions. 

####Issue Labels

**Status**

* Open issues
    * No status: New issue. 
    * `status.Accepted`: Accepted as a valid issue.
    * `status.Ongoing` : The issue is being worked on.
* Open PR  
    * `status.PendingReview`: Waiting for the review
    * `status.Ongoing` : The PR is being worked on.
    * `status.ReadyToMerge`: Reviewer accepted the changes. Ready to be merged.
* Closed issue/PR
    * No status label

**Urgency**

* `p.Urgent`(short for `Priority-Urgent`): Would like to handle in the very next release.
* `p.High`: Enhances user experience significantly, would like to do in the next few releases.
* `p.Medium`: Marginal impact on user experience.
* `p.Low`: Very little impact, unlikely to do in the near future.
* `p.Zero`: Unlikely to do, ever.

**Difficulty**

* `d.Easy`: Minor change. No need to modify tests.
* `d.Moderate`: Small, mostly-localized change. Usually requires changes to tests.
* `d.Difficult`: Requires multiple, possibly non-localized changes. Requires changes to tests and possibly new tests.
* `d.VeryDifficult`: Requires wide ranging tests, new tests and possibly, changes to the data schema.

**Aspect**

* `a.Admin`,`a.Scalability` etc. : Used to categorize issues based on the aspect
  it tackles. This is useful when a developer is focusing on a specific aspect.

**Type**

* `t.Bug`
* `t.Task`: Other work items such as updating documentation.
* `t.Enhancement`: An enhancement to an existing functionality (not big enough 
   consider as a stroy).
* `t.Story`: A user story.
* `t.Epic`: A feature that is worth many user stories.

**Other**

* `forFirstTimers` : To do as the first issue for new developers. One developer
  should not do more than one of these.
* `forContributors` : More suitable for contributors.


