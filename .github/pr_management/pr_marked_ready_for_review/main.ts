import * as core from '@actions/core'
import * as github from '@actions/github';
import { log, postComment, validateChecksOnPrHead, addOngoingLabel, dropOngoingLabel, toReviewLabel, ongoingLabel, removeLabel, finalReviewLabel, getSortedListOfEventsOnIssue, addLabel, toMergeLabel, getSortedListOfComments, addAppropriateReviewLabel } from "../common";

const token = core.getInput("repo-token");
const octokit = github.getOctokit(token);

// params to set for api requests
const owner = github.context.repo.owner; 
const repo = github.context.repo.repo;
const issue_number = github.context.issue.number;

const furtherInstructions = "Please comment `@bot ready for review` when you've passed all checks, resolved merge conflicts and are ready to request a review."

async function run() {
    if (await isPrDraft()) return; // needed because synchronise event triggers this workflow on even draft PRs

    const prLabels : string[] = await octokit.rest.issues.get({
        owner,
        repo, 
        issue_number
    })
    .then(res => res.data.labels.map((label: {name: string}) => label.name)) 
    .then(l => log.info(l, `labels returned for pr ${issue_number}`))
    .catch(err => {core.info(err); throw err});

    const { didChecksRunSuccessfully, errMessage } = await validateChecksOnPrHead();

    if (didChecksRunSuccessfully) {
        if (hasToReviewLabel(prLabels) || hasFinalReviewLabel(prLabels) || hasToMergeLabel(prLabels)) {
            core.info("Already has a review label or toMerge label and checks are passing, nothing to be done here. exiting...")
            return;
        }

        // ongoing and ready-for-review prs mean that user was previously told to state when it's ready for review
        if (hasOngoingLabel(prLabels) && isOnSynchronise()) {
            core.info("Waiting for user to manually state ready to review. exiting...");
            return;
        }

        // if checks pass on 'pr open' event, or on 'convert to ready for review' event, 
        // then add review label (and drop ongoing if it exists)
        if (hasOngoingLabel(prLabels)) { 
            await dropOngoingLabel();
        }

        await addAppropriateReviewLabel();
        
    } else { 
        if (hasOngoingLabel(prLabels) && await wasAuthorLinkedToFailingChecks()) {
            core.info("PR has the ongoing label and author has previously been notified, exiting...")
            return;
        } 
        
        // remove the following labels if the pr currently has them
        await removeLabel(finalReviewLabel);
        await removeLabel(toReviewLabel);

        await addOngoingLabel();
        await postComment(errMessage + "\n" + furtherInstructions);
    }
}

run();

///// HELPER FUNCTIONS /////

// checks if the currently running action get triggered by an on synchronise event
function isOnSynchronise() {
    log.info(github.context.payload.action, "what triggered this run");
    return github.context.payload.action === "synchronize";
}

function hasLabel(arrayOfLabels : Array<string>,  label : string) : boolean{
    return arrayOfLabels.findIndex(l => l === label) !== -1;
}

function hasOngoingLabel(arrayOfLabels : Array<string>) {
    return hasLabel(arrayOfLabels, ongoingLabel);
}

function hasToReviewLabel(arrayOfLabels : Array<string>) {
    return hasLabel(arrayOfLabels, toReviewLabel);
}

function hasFinalReviewLabel(arrayOfLabels : Array<string>) {
    return hasLabel(arrayOfLabels, finalReviewLabel);
}

function hasToMergeLabel(arrayOfLabels : Array<string>) {
    return hasLabel(arrayOfLabels, toMergeLabel);
}

/**
 * Checks if the bot did post a comment notifying the author of failing checks, from the last time the s.Ongoing label was applied.
 * This function is necessary for this case: 
 * A draft pr has an ongoing label -> author converts to ready for review but there's failing checks. The bot should comment once (i think).
 * 
 * There are two rest requests in this function itself, and this file is ran on every commit
 */
async function wasAuthorLinkedToFailingChecks() : Promise<boolean> {
    const events = await getSortedListOfEventsOnIssue();

    const labelEvent = events.find(e => e.event === "labeled" && e.label?.name == ongoingLabel);

    if (!labelEvent) {
        core.warning("Some wrong assumption may have been made or the API used to fetch the PRs may have changed. This function should have been called only on PRs that are assigned the label.")
        return true; // skip adding a comment 
    }

    const comments = await getSortedListOfComments(labelEvent.created_at);

    const checksFailedComment = comments.find(c => c.body.search("There were failing checks found"));

    log.info(checksFailedComment, "checksFailedComment");

    return !!checksFailedComment;
}


async function isPrDraft() {
    return await octokit.rest.pulls.get({
        owner,
        repo,
        pull_number: issue_number,
    })
    .then(res => {
        log.info(res.data.draft, `is pr ${issue_number} draft`)
        return res.data.draft;
    })
    .catch(err => {log.info(err, "Error getting the pr that triggered this workflow"); throw err;});
}
