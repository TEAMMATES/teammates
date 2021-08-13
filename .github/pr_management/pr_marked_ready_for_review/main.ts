import core = require("@actions/core");
import github = require("@actions/github");
import { log, dropToReviewLabelAndAddOngoing, addToReviewLabel, postComment, validateChecksOnPrHead, addOngoingLabel } from "../common";

const token = core.getInput("repo-token");
const octokit = github.getOctokit(token);

// params to set for api requests
const owner = github.context.repo.owner; 
const repo = github.context.repo.repo;
const issue_number = github.context.issue.number;

const furtherInstructions = "Please comment `@bot ready for review` when you've passed all checks, resolved merge conflicts and are ready to request a review."

async function run() {
    log.info(github.context.action, "action");
    log.info(github.context.payload.action, "payload action");
    
    if (!(await isPRMarkedReadyForReview())) return; // needed because synchronise event triggers this workflow on even draft PRs

    const prLabels : string[] = await octokit.rest.issues.get({
        owner,
        repo, 
        issue_number
    })
    .then(res => res.data.labels.map(label => label.name || label)) // label may be of type string instead of an object so need this ||
    .then(l => log.info(l, `labels returned for pr ${issue_number}`))
    .catch(err => {core.info(err); throw err});

    const { didChecksRunSuccessfully, errMessage } = await validateChecksOnPrHead();

    if (didChecksRunSuccessfully) {
        if (!hasLabel(prLabels, "s.Ongoing") && !hasLabel(prLabels, "s.ToReview")) { // todo check correct pr
            await addToReviewLabel();
        } else if (hasLabel(prLabels, "s.Ongoing")) {
            core.info("Waiting for user to manually state ready to review. exiting...");
        }
    } else { 
        // for prs labelled as ongoing, for all other event types except on synchronise, 
        // we can be sure that the author hasn't been notified of the failing checks by the bot
        if (hasLabel(prLabels, "s.Ongoing") && isOnSynchronise() && await wasAuthorLinkedToFailingChecks()) {
            core.info("PR has the ongoing label and author has been notified, exiting...")
            return;
        } 
        
        if (hasLabel(prLabels, "s.ToReview")) {
            await dropToReviewLabelAndAddOngoing();
        } else if (!hasLabel(prLabels, "s.ToReview") && !hasLabel(prLabels, "s.Ongoing")) {
            await addOngoingLabel();
        }

        await postComment(errMessage + "\n" + furtherInstructions);
    }
}

run();

///// HELPER FUNCTIONS /////
/* did the currently running action get triggered by an on synchronise event */
function isOnSynchronise() {
    log.info(github.context.payload.action, "what triggered this run");
    return github.context.payload.action === "synchronize";
}

function hasLabel(arrayOfLabels : Array<string>,  label) : boolean{
    return arrayOfLabels.findIndex(l => l ===label) !== -1;
}

/**
 * Checks if the bot did post a comment notifying the author of failing checks, from the last time the s.Ongoing label was applied.
 * This function is necessary for this case: 
 * A draft pr has an ongoing label -> author converts to ready for review but there's failing checks. The bot should comment once (i think).
 * 
 * There are two rest requests in this function itself, and this file is ran on every commit
 */
async function wasAuthorLinkedToFailingChecks() : Promise<boolean> {
    // sort by latest event first, so that we consider the last time that the toReview label was added
    const sortFn = (a, b) => {
        if (!a.created_at || !b.created_at) return 1; // move back
        return Date.parse(b.created_at) - Date.parse(a.created_at)
    }

    // get an array of events for the current issue (https://octokit.github.io/rest.js/v18#issues-list-events)
    const events = await octokit.rest.issues.listEvents({
        owner,
        repo,
        issue_number,
    })
    .then(res => res.data.sort(sortFn))
    .catch(err => {
        throw err;
    });
    
    const labelEvent = events.find(e => e.event === "labeled" && e.label?.name == "s.Ongoing");

    if (!labelEvent) {
        core.warning("Some wrong assumption may have been made or the API used to fetch the PRs may have changed. This function should have been called only on PRs that are assigned the label.")
        return true; // skip adding a comment 
    }

    // // get an array of events for the current issue (https://octokit.github.io/rest.js/v18#issues-list-events)
    const comments = await octokit.rest.issues.listComments({
        owner,
        repo,
        issue_number,
        since: labelEvent.created_at
    })
    .then(res => res.data.sort(sortFn))
    .catch(err => {
        throw err;
    });

    const checksFailedComment = comments.find(c => c.body.search("There were failing checks found"));

    log.info(checksFailedComment, "checksFailedComment");

    return !!checksFailedComment;
}


async function isPRMarkedReadyForReview() {
    return await octokit.rest.pulls.get({
        owner,
        repo,
        pull_number: issue_number,
    })
    .then(res => {
        log.info(res.data.draft, `is pr ${issue_number} draft`)
        return !res.data.draft;
    })
    .catch(err => {log.info(err, "Error getting the pr that triggered this workflow"); throw err;});
}