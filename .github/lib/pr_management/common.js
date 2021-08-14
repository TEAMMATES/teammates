"use strict";
var __awaiter = (this && this.__awaiter) || function (thisArg, _arguments, P, generator) {
    function adopt(value) { return value instanceof P ? value : new P(function (resolve) { resolve(value); }); }
    return new (P || (P = Promise))(function (resolve, reject) {
        function fulfilled(value) { try { step(generator.next(value)); } catch (e) { reject(e); } }
        function rejected(value) { try { step(generator["throw"](value)); } catch (e) { reject(e); } }
        function step(result) { result.done ? resolve(result.value) : adopt(result.value).then(fulfilled, rejected); }
        step((generator = generator.apply(thisArg, _arguments || [])).next());
    });
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.validateChecksOnPrHead = exports.postComment = exports.log = exports.sleep = exports.dropOngoingLabel = exports.dropToReviewLabelAndAddOngoing = exports.dropOngoingLabelAndAddToReview = exports.addToReviewLabel = exports.addOngoingLabel = exports.wereReviewCommentsAdded = void 0;
/**
 * Collection of common functions to use in this folder
 */
const core = require("@actions/core");
const github = require("@actions/github");
const token = core.getInput("repo-token");
const octokit = github.getOctokit(token);
const owner = github.context.repo.owner;
const repo = github.context.repo.repo;
const issue_number = github.context.issue.number;
const actor = github.context.actor;
//// variables to configure
// todo change in teammates
const usualTimeForChecksToRun = 5000; // 20 * 60 * 1000; // min * sec * ms
// to prevent cyclical checking when checking for passing runs. note: needs to match names assigned by workflow files
const excludedChecksNames = {
    "Handle PR that may be draft": 1,
    "Handle PR that may be ready for review": 1,
};
function wereReviewCommentsAdded(pr, sinceTimeStamp) {
    return __awaiter(this, void 0, void 0, function* () {
        isValidTimestamp(sinceTimeStamp);
        const comments = yield octokit.rest.pulls
            .listReviewComments({
            owner,
            repo,
            pull_number: pr.number,
            since: sinceTimeStamp, // todo unsure if this works as expected --> test
        })
            .then(res => {
            core.info("these comments were retrieved\n" + res);
            return res;
        })
            .catch(err => {
            throw err;
        });
        return comments.data.length > 0;
    });
}
exports.wereReviewCommentsAdded = wereReviewCommentsAdded;
function isValidTimestamp(sinceTimeStamp) {
    try {
        Date.parse(sinceTimeStamp);
    }
    catch (err) {
        throw new Error(`the sinceTimeStamp argument passed is an invalid timestamp`);
    }
}
//// abstractions for adding and dropping labels
function addOngoingLabel() {
    return __awaiter(this, void 0, void 0, function* () {
        yield addLabel("s.Ongoing");
    });
}
exports.addOngoingLabel = addOngoingLabel;
function addToReviewLabel() {
    return __awaiter(this, void 0, void 0, function* () {
        yield addLabel("s.ToReview");
    });
}
exports.addToReviewLabel = addToReviewLabel;
function dropOngoingLabelAndAddToReview() {
    return __awaiter(this, void 0, void 0, function* () {
        yield removeLabel("s.Ongoing");
        yield addLabel("s.ToReview");
    });
}
exports.dropOngoingLabelAndAddToReview = dropOngoingLabelAndAddToReview;
function dropToReviewLabelAndAddOngoing() {
    return __awaiter(this, void 0, void 0, function* () {
        yield removeLabel("s.ToReview");
        yield addLabel("s.Ongoing");
    });
}
exports.dropToReviewLabelAndAddOngoing = dropToReviewLabelAndAddOngoing;
function dropOngoingLabel() {
    return __awaiter(this, void 0, void 0, function* () {
        yield removeLabel("s.Ongoing");
    });
}
exports.dropOngoingLabel = dropOngoingLabel;
function addLabel(labelName) {
    return __awaiter(this, void 0, void 0, function* () {
        yield octokit.rest.issues
            .addLabels({
            owner,
            repo,
            issue_number,
            labels: [labelName],
        })
            .then(res => exports.log.info(res.status, `added ${labelName} label with status`))
            .catch(err => exports.log.info(err, "error adding label"));
    });
}
function removeLabel(labelName) {
    return __awaiter(this, void 0, void 0, function* () {
        yield octokit.rest.issues
            .removeLabel({
            owner,
            repo,
            issue_number,
            name: [labelName], // todo check if this works
        })
            .then(res => logInfo(res.status, `removing label ${res.status} with status`))
            .catch(err => logInfo(err, "error removing label (label may not have been applied)"));
    });
}
function sleep(ms) {
    return __awaiter(this, void 0, void 0, function* () {
        core.info(`sleeping for ${ms} milliseconds...`);
        return new Promise(resolve => setTimeout(resolve, ms));
    });
}
exports.sleep = sleep;
////// functions to help with logging //////
/* these functions log using the core module but with the format "label: itemToLog".
They also return the variable being logged for convenience */
exports.log = { info: logInfo, warn: logWarn, jsonInfo: jsonInfo };
function logInfo(toPrint, label) {
    core.info(`${label}: ${toPrint}`);
    return toPrint;
}
function jsonInfo(jsonToPrint, label) {
    core.info(`${label}: ${JSON.stringify(jsonToPrint)}`);
}
function logWarn(toPrint, label) {
    core.warning(`${label}: ${toPrint}`);
}
//// comments
function postComment(message) {
    return __awaiter(this, void 0, void 0, function* () {
        const commentBody = `Hi ${actor}, please note the following. ${message}`;
        yield octokit.rest.issues.createComment({
            owner,
            repo,
            issue_number,
            body: commentBody,
        })
            .then(res => core.info(`Commented:\n ${res.data.body}\n with status ${res.status}`))
            .catch(err => core.error(err));
    });
}
exports.postComment = postComment;
//// checks stuff
function validateChecksOnPrHead() {
    return __awaiter(this, void 0, void 0, function* () {
        const sha = yield getPRHeadShaForIssueNumber(issue_number);
        return yield validateChecks(sha);
    });
}
exports.validateChecksOnPrHead = validateChecksOnPrHead;
function validateChecks(validateForRef) {
    return __awaiter(this, void 0, void 0, function* () {
        core.info(`validating checks on ref: ${validateForRef}...`);
        let areChecksOngoing = true;
        let listChecks;
        // wait for the checks to complete before proceeding
        while (areChecksOngoing) {
            // https://octokit.github.io/rest.js/v18#checks-list-for-ref
            listChecks = yield octokit.rest.checks.listForRef({
                owner,
                repo,
                ref: validateForRef,
            });
            // array of check runs, may include the workflow that is running the current file
            const checkRunsArr = listChecks.data.check_runs;
            // todo [low] change to core.debug
            checkRunsArr.forEach(checkRun => {
                core.info(`current status for "${checkRun.name}": ${checkRun.status}`);
            });
            // find checks that are not completed and sleep while waiting for completion
            const res = checkRunsArr.find(checkRun => checkRun.status !== "completed" && !(checkRun.name in excludedChecksNames));
            if (res !== undefined) {
                yield sleep(usualTimeForChecksToRun);
                continue;
            }
            areChecksOngoing = false;
        }
        const checkRunsArr = listChecks.data.check_runs;
        // format the conclusions of the check runs
        let conclusionsDetails = "";
        listChecks.data.check_runs.forEach(checkRun => {
            if (checkRun.status !== "completed") {
                conclusionsDetails += `${checkRun.name}'s completion status was ignored because this check is found the excluded checks list\n`;
            }
            else {
                conclusionsDetails += `${checkRun.name} has ended with the conclusion: \`${checkRun.conclusion}\`. [Here are the details.](${checkRun.details_url})\n`;
            }
        });
        const didChecksRunSuccessfully = !checkRunsArr.find(checkRun => checkRun.conclusion !== "success" && !(checkRun.name in excludedChecksNames));
        const errMessage = `There were failing checks found. \n${conclusionsDetails}`;
        exports.log.info(didChecksRunSuccessfully, "didChecksRunSuccessfully");
        exports.log.info(conclusionsDetails, "conclusions of checks\n");
        return { didChecksRunSuccessfully, errMessage };
    });
}
// event payload that triggers this pull request does not contain this info about the PR, so must use rest api again
function getPRHeadShaForIssueNumber(pull_number) {
    return __awaiter(this, void 0, void 0, function* () {
        const pr = yield octokit.rest.pulls.get({
            owner,
            repo,
            pull_number,
        }).catch(err => { throw err; });
        const sha = pr.data.head.sha;
        exports.log.info(sha, "sha");
        return sha;
    });
}
