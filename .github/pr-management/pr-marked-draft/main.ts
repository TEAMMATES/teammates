import * as core from '@actions/core';
import { ongoingLabel } from '../common/const';
import { addLabel, getCurrentPR } from '../common/github-manager/interface';

/**
 * This is the main function of this file.
 */
async function run() {
    try {
        const needsLabelling = await doesPrNeedLabelling();
        
        if (!needsLabelling) {
            core.info("needs no labelling, ending.")
            return;
        }

        await addLabel(ongoingLabel);
    } catch (ex) {
        core.info(ex);
        core.setFailed(ex.message);
    }
}

async function doesPrNeedLabelling() : Promise<boolean> {
    // get PR for this issue
    const pr = await getCurrentPR().catch(err => {throw err});

    if (!pr.draft) {
        core.info("pr is not a draft");
        return false;
    }

    const hasOngoingLabel = pr.labels.find(l => l.name === ongoingLabel) != undefined;

    if (hasOngoingLabel) {
        core.info("pr has ongoing label");
        return false;
    }

    return true; 
}

run();
