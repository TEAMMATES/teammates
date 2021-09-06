import * as core from '@actions/core'
import * as github from '@actions/github';
import { log, postComment, validateChecksOnPrHead, dropOngoingLabel, addAppropriateReviewLabel } from "../common"
const reviewKeywords = "@bot ready for review";

// params to set for api requests
// check https://github.com/actions/toolkit/blob/main/packages/github/src/context.ts to figure out what's being responded
const issue_number = github.context.issue.number;

/**
 * This is the main function of this file
 */
async function run() {
    try {
        // all comments trigger this workflow
        const doesCommentContainKeywords = filterCommentBody();
        if (!doesCommentContainKeywords) return;

        const valid : boolean = await validate();
        if (!valid) return;

        await dropOngoingLabel();
        await addAppropriateReviewLabel();
    } catch (ex) {
        core.info(ex);
        core.setFailed(ex.message);
    }
}

// return if comment body has the exact keywords
function filterCommentBody() : boolean {
    const issueComment = github.context.payload.comment.body;
    const hasKeywords = issueComment.search(reviewKeywords) !== -1;

    core.info(`issueComment: ${issueComment}`);
    core.info(`were keywords found in issue? ${hasKeywords}`);

    return hasKeywords;
}

/**
 * Wrapper function for all validation related checks. If any fail, this function handles adding the comment 
 * @returns boolean of whether all validation checks 
 */
async function validate() : Promise<boolean> {
    if (!validatePRStatus()) return;

    const { didChecksRunSuccessfully, errMessage } = await validateChecksOnPrHead();

    if (!didChecksRunSuccessfully) {
        await postComment(`${errMessage}\n Please comment \`${reviewKeywords}\` when you're ready to request a review again.`);
        return false;
    }

    return true;
}


function validatePRStatus() {
    // nothing stops this github bot from running on comments on closed PRs or PR of specific labels
    core.warning("No pr validation has been set");
    return true;
}

run();
