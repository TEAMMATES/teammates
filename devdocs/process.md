#TEAMMATES Development Process

##Roles
* `Dev` - Issue owner who fixes the issue. Can be a Committer or a Contributor.
* `Reviewer` - Assigned per issue. Usually, a core team member.
* `Team lead` - Responsible for keeping all tests green, merging pull requests.
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
   
   * Create a pull request. For the pull request name, copy paste the relevant
     issue name.<br>
     e.g. ` Incorrect error message when adding an existing instructor #1760`<br>
     In the comment, mention the issue number. Doing so will create an 
     automatic reference from the issue to the pull request.<br>
     e.g. ` For #1760`  
   
   

###Reviewing a fix
Role: reviewer

TBD

###Applying a fix
Role: team lead

TBD

###Deploying fixes
Role: PM

TBD
   

### Issue Lifecycle
<img src='../src/main/webapp/dev/images/IssueLifecycle.png' width='600'>

Given above is an illustration of the issue lifecycle. 
Colors indicate which roles are involved in which states/transitions. 

####Issue Labels

**Status**
* Open issues
    * No status: New issue. 
    * `s.Accepted`: Accepted as a valid issue.
    * `s.Fixing` : The issue is being worked on.
    * `s.PendingReview`: Waiting for the review
    * `s.ReadyToMerge`: Reviewer accepted the changes. Ready to be merged.
    * `s.Merging`: Someone is trying to merge 
      (use this label if there's a risk of multiple persons trying to merge at the same time) 
* Closed issues
    * `s.WontFix`: The issue is valid, but we have decided not to fix it.
    * `s.Duplicate`, `s.Invalid`: Self explanatory.
  
**Urgency**

* `p.Urgent`(short for `Priority-Urgent`): Would like to handle in the very next release.
* `p.High`: Enhances user experience significantly, would like to do in the next few releases.
* `p.Medium`: Marginal impact on user experience.
* `p.Low`: Very little impact, unlikely to do in the near future.
* `p.Zero`: Unlikely to do, ever.

**Difficulty**

* `d.Low`: Minor change. No need to modify tests.
* `d.Medium`: Small, mostly-localized change. Usually requires changes to tests.
* `d.High`: Requires multiple, possibly non-localized changes. Requires changes to tests and possibly new tests.
* `d.VeryHigh`: Requires wide ranging tests, new tests and possibly, changes to the data schema.

**Aspect**

* `a.Admin`,`a.Scalability` etc. : Used to categorize issues based on the aspect
  it tackles. This is useful when a developer is focusing on a specific aspect.

**Type**

* `t.Bug`
* `t.Enhancement`
* `t.Task`: Other work items such as updating documentation.
* `t.Question`: A question asked by someone. 

**Under the feature of ..**

* `u.easierEnroll`, `u.MCQ` etc.: Used for grouping issues under a bigger feature.

