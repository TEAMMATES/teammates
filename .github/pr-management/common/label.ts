import * as core from '@actions/core' // todo settle logger later
import { finalReviewLabel, toReviewLabel } from './const'
import { getSortedListOfEventsOnIssue, addLabel } from './github-manager/issues';

/**
 * Adds the last review label that was added to the pr, if any is found, else adds the toReviewLabel.
 */
 export async function addAppropriateReviewLabel() {
    const eventsArr = await getSortedListOfEventsOnIssue();

    // if a previous review label was found, re-add that label
    for (const e of eventsArr) {
        if (e.event !== "labeled") continue;

        if (e.label?.name == finalReviewLabel) {
            await addLabel(finalReviewLabel);
            core.info(`${finalReviewLabel} was the last found review label on this PR, so adding it back.`);
            return;
        }

        if (e.label?.name == toReviewLabel) {
            await addLabel(toReviewLabel);
            core.info(`${toReviewLabel} was the last found review label on this PR, so adding it back.`);
            return;
        }   
    };

    // if no previous review label was found, add toReviewLabel
    await addLabel(toReviewLabel);
}
