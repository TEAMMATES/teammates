import * as core from '@actions/core'
import * as github from '@actions/github';
import { addOngoingLabel, log, ongoingLabel } from '../common';

const token = core.getInput("repo-token");
const octokit = github.getOctokit(token);

// params to set for api requests
// check https://github.com/actions/toolkit/blob/main/packages/github/src/context.ts
const owner = github.context.repo.owner; 
const repo = github.context.repo.repo;
const issue_number = github.context.issue.number;

async function run() {
    try {
        const needsLabelling = await isDraftAndNotLabelledOngoing();
        
        if (!needsLabelling) {
            core.info("needs no labelling, ending.")
            return;
        }

        await addOngoingLabel();
    } catch (ex) {
        core.info(ex);
        core.setFailed(ex.message);
    }
}

async function isDraftAndNotLabelledOngoing() {
    return await octokit.rest.pulls.get({
        owner,
        repo,
        pull_number: issue_number,
    })
    .then(res => {
        log.info(res.data.draft, `is pr ${issue_number} draft`)
        return res.data.draft && res.data.labels.find(l => l.name === ongoingLabel) === undefined;
    })
    .catch(err => {log.info(err, "error getting pr (issue) that triggered this workflow"); throw err;});
}

run();