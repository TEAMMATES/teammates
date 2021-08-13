import core = require("@actions/core");
import github = require("@actions/github");
import { addOngoingLabel, log } from "../common";

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
        log.info(res.data.labels, "details of existing labels")
        return res.data.draft && res.data.labels.find(l => l.name === "s.Ongoing") === undefined;
    })
    .catch(err => {log.info(err, "error getting pr (issue) that triggered this workflow"); throw err;});
}

run();