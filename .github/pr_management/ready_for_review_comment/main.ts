import * as core from '@actions/core'
import * as github from '@actions/github';
import { postComment, validateChecksOnPrHead, dropOngoingLabel, addAppropriateReviewLabel, reviewKeywords, getCurrentIssue } from "../common"

// params to set for api requests
// references: https://github.com/actions/toolkit/blob/main/packages/github/src/context.ts 

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
    if (!isValidPRStatus()) return;

    if (!await isValidAuthor()) return;

    const { didChecksRunSuccessfully, errMessage } = await validateChecksOnPrHead();

    if (!didChecksRunSuccessfully) {
        await postComment(`${errMessage}\n Please comment \`${reviewKeywords}\` when you're ready to request a review again.`);
        return false;
    }

    return true;
}


function isValidPRStatus() {
    // nothing stops this workflow from running on PRs of specific labels
    core.warning("No pr validation has been set");
    return true;
}

async function isValidAuthor() {
    const commentAuthor : string = github.context.payload.comment.user.login; // https://docs.github.com/en/developers/webhooks-and-events/webhooks/webhook-events-and-payloads#issue_comment
    const prAuthor : string = await getCurrentIssue().then(res => res.data.user.login);

    core.info(`Author of comment that triggered this workflow: ${commentAuthor}.\n` +
        `Author of pr that this comment was added to: ${prAuthor}.\n`+
        `Is it a match? ${prAuthor === commentAuthor}`)

    return prAuthor === commentAuthor;
}

run();
