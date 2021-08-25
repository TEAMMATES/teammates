/**
 * Collection of common functions to use in this folder
 */
import * as core from '@actions/core'
import * as github from '@actions/github';
const token = core.getInput("repo-token");
const octokit = github.getOctokit(token);

const owner = github.context.repo.owner;
const repo = github.context.repo.repo;
const issue_number = github.context.issue.number;
const actor = github.context.actor;

export const ongoingLabel = "s.Ongoing";
export const toReviewLabel = "s.ToReview";

//// variables to configure
const usualTimeForChecksToRun = 10 * 60 * 1000; // min * sec * ms

/* this list of names of excluded checks is to prevent cyclical checking when checking for passing runs. 
note: each string needs to match the jobs.<id>.name property in yaml files */ 
const excludedChecksNames = {
    "Handle PR that may be draft": 1,
    "Handle PR that may be ready for review": 1,
};

export async function wereReviewCommentsAdded(pr, sinceTimeStamp: string) {
    validateTimeStamp(sinceTimeStamp);

    const comments = await octokit.rest.pulls.listReviewComments({
        owner,
        repo,
        pull_number: pr.number,
        since: sinceTimeStamp, 
    }).then(res => {
        core.info("these comments were retrieved\n" + res);
        return res;
    }).catch(err => {
        throw err;
    });

    return comments.data.length > 0;
}

function validateTimeStamp(timeStamp: string) {
    try {
        Date.parse(timeStamp);
    } catch (err) {
        throw new Error(`the sinceTimeStamp argument passed is an invalid timestamp`);
    }
}

//// abstractions for adding and dropping labels

export async function addOngoingLabel() {
    await addLabel(ongoingLabel);
}

export async function addToReviewLabel() {
    await addLabel(toReviewLabel);
}

export async function dropOngoingLabelAndAddToReview() {
    await removeLabel(ongoingLabel);
    await addLabel(toReviewLabel);
}

export async function dropToReviewLabelAndAddOngoing() {
    await removeLabel(toReviewLabel);
    await addLabel(ongoingLabel);
}

export async function dropOngoingLabel() {
    await removeLabel(ongoingLabel);
}

async function addLabel(labelName: string) {
    await octokit.rest.issues
        .addLabels({
            owner,
            repo,
            issue_number,
            labels: [labelName],
        })
        .then(res => log.info(res.status, `added ${labelName} label with status`))
        .catch(err => log.info(err, "error adding label"));
}

async function removeLabel(labelName: string) {
    await octokit.rest.issues.removeLabel({
        owner,
        repo,
        issue_number,
        name: labelName,
    })
    .then(res => logInfo(res.status, `removing label ${res.status} with status`))
    .catch(err => logInfo(err, "error removing label (label may not have been applied)"));
}

export async function sleep(ms : number) {
    core.info(`sleeping for ${ms} milliseconds...`);
    return new Promise(resolve => setTimeout(resolve, ms));
}

////// functions to help with logging //////
/* these functions log using the core module but with the format "label: itemToLog". 
They also return the variable being logged for convenience */
export const log = { info: logInfo, warn: logWarn, jsonInfo: jsonInfo };

function logInfo(toPrint, label) {
    core.info(`${label}: ${toPrint}`);
    return toPrint;
}

function jsonInfo(jsonToPrint: JSON, label) {
    core.info(`${label}: ${JSON.stringify(jsonToPrint)}`);
}

function logWarn(toPrint, label) {
    core.warning(`${label}: ${toPrint}`);
}

//// comments related functions
export async function postComment(message) {
    const commentBody = `Hi @${actor}, please note the following. ${message}`;

    await octokit.rest.issues.createComment({
        owner,
        repo,
        issue_number,
        body: commentBody,
    })
    .then(res => core.info(`Commented:\n ${res.data.body}\n with status ${res.status}`))
    .catch(err => core.error(err))
}

//// check runs related functions

export async function validateChecksOnPrHead() {
    const sha = await getPRHeadShaForIssueNumber(issue_number);
    return await validateChecks(sha);
}

async function validateChecks(validateForRef: string)
: Promise<{ didChecksRunSuccessfully: boolean; errMessage: string }> {

    core.info(`validating checks on ref: ${validateForRef}...`);

    let areChecksOngoing = true;
    let listChecks;

    // wait for the checks to complete before proceeding
    while (areChecksOngoing) {
        // https://octokit.github.io/rest.js/v18#checks-list-for-ref
        listChecks = await octokit.rest.checks.listForRef({
            owner,
            repo,
            ref: validateForRef,
        });

        // array of check runs, may include the workflow that is running the current file
        const checkRunsArr = listChecks.data.check_runs;

        checkRunsArr.forEach(checkRun => {
            core.info(`current status for "${checkRun.name}": ${checkRun.status}`);
        });

        // find checks that are not completed and sleep while waiting for completion
        const res = checkRunsArr.find(
            checkRun => checkRun.status !== "completed" && !(checkRun.name in excludedChecksNames)
        );
        if (res !== undefined) {
            await sleep(usualTimeForChecksToRun);
            continue;
        }

        areChecksOngoing = false;
    }

    const checkRunsArr = listChecks.data.check_runs;

    // format the conclusions of the check runs
    let conclusionsDetails = "";

    listChecks.data.check_runs.forEach(checkRun => {
        if (checkRun.status !== "completed") {
            conclusionsDetails += `${checkRun.name}'s completion status was ignored because this check is found in the excluded checks list\n`;
        } else {
            conclusionsDetails += `${checkRun.name} has ended with the conclusion: \`${checkRun.conclusion}\`. [Here are the details.](${checkRun.details_url})\n`;
        }
    });

    const didChecksRunSuccessfully = !checkRunsArr.find(
        checkRun => checkRun.conclusion !== "success" && !(checkRun.name in excludedChecksNames)
    );
    const errMessage = `There were failing checks found. \n${conclusionsDetails}`;

    log.info(didChecksRunSuccessfully, "didChecksRunSuccessfully");
    log.info(conclusionsDetails, "conclusions of checks\n");

    return { didChecksRunSuccessfully, errMessage };
}

// event payload that triggers this pull request does not contain this info about the PR, so must use rest api again
async function getPRHeadShaForIssueNumber(pull_number) {
    const pr = await octokit.rest.pulls.get({
        owner,
        repo,
        pull_number,
    }).catch(err => {throw err;});

    const sha = pr.data.head.sha;
    log.info(sha, "sha");
    return sha;
}
