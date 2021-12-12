import * as core from '@actions/core'
import * as github from '@actions/github';

const token = core.getInput("repo-token");
const octokit = github.getOctokit(token);

// references: https://github.com/actions/toolkit/blob/main/packages/github/src/context.ts 
const owner = github.context.repo.owner;
const repo = github.context.repo.repo;

// https://octokit.github.io/rest.js/v18#checks-list-for-ref
export async function getListOfChecks(ref : string) { // todo return type
    const checkRunsArr = await octokit.rest.checks.listForRef({
        owner,
        repo,
        ref: ref,
    })
    .then(res => {
        core.info(`received the list of checks with response ${res.status}`);
        return res.data.check_runs;
    })
    .catch(err => {throw err});

    // extra logging
    checkRunsArr.forEach(checkRun => {
        core.info(`current status for "${checkRun.name}": ${checkRun.status}`);
    });

    return checkRunsArr;
}