import * as core from '@actions/core';
import * as github from '@actions/github';
import { validateChecksOnPrHead } from '../common/checksValidation';
import { ongoingLabel, reviewKeywords } from '../common/const';
import { getCurrentIssue, postComment, removeLabel } from '../common/github-manager/issues';
import { addAppropriateReviewLabel } from '../common/label';

/**
 * This is the main function of this file.
 */
async function run() {
    try {
        // all comments trigger this workflow
        const doesCommentContainKeywords = filterCommentBody();
        if (!doesCommentContainKeywords) return;

        const valid : boolean = await validate();
        if (!valid) return;

        await removeLabel(ongoingLabel);
        await addAppropriateReviewLabel();
    } catch (ex) {
        core.info(ex);
        core.setFailed(ex.message);
    }
}

// returns whether the comment body that triggered this workflow has the exact keywords
function filterCommentBody() : boolean {
    const issueComment = github.context.payload.comment.body;
    const hasKeywords = issueComment.search(reviewKeywords) !== -1;

    core.info(`issueComment: ${issueComment}`);
    core.info(`were keywords found in issue? ${hasKeywords}`);

    return hasKeywords;
}

/**
 * Wrapper function for all validation related logic to perform. If any fail, 
 * this function will handle posting a comment.
 * @returns boolean of whether all validation checks are passing
 */
async function validate() : Promise<boolean> {
    if (!isValidPRStatus()) return false;

    if (!await isValidAuthor()) return false;

    const { didChecksRunSuccessfully, errMessage } = await validateChecksOnPrHead();

    if (!didChecksRunSuccessfully) {
        await postComment(
            `${errMessage}\nPlease comment \`${reviewKeywords}\` when you're ready to request a review again.`);
        return false;
    }

    return true;
}


// to validated if the pr that the workflow is running on is valid, or if it should be skipped
function isValidPRStatus() : boolean { // TODO check: if no validation needed, remove...
    core.warning("No pr validation has been set");
    return true;
}



async function isValidAuthor() : Promise<boolean> {
    // https://docs.github.com/en/developers/webhooks-and-events/webhooks/webhook-events-and-payloads#issue_comment
    const commentAuthor : string = github.context.payload.comment.user.login; 
    const prAuthor : string = await getCurrentIssue().then(res => res.data.user.login);

    core.info(`Author of comment that triggered this workflow: ${commentAuthor}.\n` +
        `Author of pr that this comment was added to: ${prAuthor}.\n`+
        `Is it a match? ${prAuthor === commentAuthor}`)

    return prAuthor === commentAuthor;
}

run();
