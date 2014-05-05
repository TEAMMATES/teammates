#TEAMMATES Development Process

##Roles
* `Dev` - Issue owner who fixes the issue. Can be a Committer or a Contributor.
* `Reviewer` - Assigned per issue. Usually, a core team member.
* `Team lead` - Responsible for keeping all tests green, applying patches.
* `PM` (Project Manager) - General project coordination and deploying to live server

##Workflow
**Diagram incorrect, to be updated**

<img src='http://teammatesv4.appspot.com/dev/images/workflow.png' width='600'>

###Fixing Issues

Role: Dev

{If you need any help regarding the workflow, please post in the 
[teammates-contributors Google group](https://groups.google.com/forum/?fromgroups#!forum/teammates-contributors) .}

1. Select an issue to handle. Get it assigned to you. 
   Contributors can request for an issue to be assigned to you by posting a comment under the issue in concern.

2. Optionally, you can discuss alternative solutions before choosing one to implement. 
   This can be done through Issue tracker. 
   Such a discussion reduces the chance of the fix being rejected later.

3. Label the issue as `s.Started` (short for `stauts:Started`).

4. Update your local repo (the one you created when setting up the project on your computer) 
   with the latest version of the code from the committer repo.
    
    > `git pull`

5. Start a new branch named `Issue{IssueNumber}`. 
   If you are already working in a branch, remember to switch to the `master` 
   before creating the new branch. e.g.,
    
    ```
    //switch to master (if required)
    git checkout master
    //create new branch and switch to it at the same time
    git checkout -b Issue{issue number}
    //e.g. git checkout -b Issue236

6. Fix the issue.
   * Have a look at our coding and testing best practices (links given [here](../README.md)) before you start your first issue.
   * Keep in mind that we have 'reference' code that has extra explanatory notes to help new developers. These are listed in the 'Coding Best Practices' document.
   * You may commit as many times as you wish while you are fixing the code. 
       * You may push these to the committer repo too. 
       * Try to keep the branch reasonably clean, but don't fuss too much about it. 
         The branch will be deleted after merging.
     

7. When the work is ready for review:

   * Sync with the committer repo: While you were fixing the issue, others 
   might have pushed new code to the committer repo. In that case, update your repo with any new changes from committer repo and merge those updates to your branch
        
   ```
   git pull 
   git merge master
   git commit -a -m "your commit message"
   ```
        
   * Format the code: Select the code segments you modified and apply the code 
     formatting function of Eclipse (`Source --> Format`). 
     This is to ensure that the code is properly formatted. 
     You may tweak the code further to improve readability as auto-format 
     doesn't always result in a good layout.
        
   * Ensure _dev green_ (i.e., all tests are passing on dev server).
     
     >If your new code might behave differently on a remote server than how it 
     behaves on the dev server, ensure staging green 
     (i.e., all tests are passing against the modified app running on your own 
     GAE staging server).
        
   * Push your branch to the committer repo, if you haven't done that already.
   
   * Create a pull request.
   
   * Go to the corresponding issue and change the status to `s.ReadyForReview`
   
   

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
Issue classification:

**Urgency**

* `p.Urgent`( short for `Priority-Urgent`): Would like to handle in the very next release.
* `p.High`: Enhances user experience significantly, would like to do in the next few releases.
* `p.Medium`: Marginal impact on user experience.
* `p.Low`: Very little impact, unlikely to do in the near future.
* `p.Zero`: Unlikely to do, ever.

**Difficulty**

* `d.Low`: Minor change. No need to modify tests.
* `d.Medium`: Small, mostly-localized change. Usually requires changes to tests.
* `d.High`: Requires multiple, possibly non-localized changes. Requires changes to tests and possibly new tests.
* `d.VeryHigh`: Requires wide ranging tests, new tests and possibly, changes to the data schema.

<img src='../src/main/webapp/dev/images/IssueLifecycle.png' width='600'>

Given above is an illustration of the issue lifecycle. 
Colors indicate which roles are involved in which states/transitions. 
