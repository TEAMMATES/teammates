import * as core from '@actions/core'
import * as github from '@actions/github';
import { log } from '../../logger'

const token = core.getInput("repo-token");
const octokit = github.getOctokit(token);

const owner = github.context.repo.owner;
const repo = github.context.repo.repo;
const issue_number = github.context.issue.number;

/**
 * Gets details from the pulls api about the issue that triggered this workflow. 
 */
export async function getCurrentPR() { // todo return type
    return await octokit.rest.pulls.get({
        owner,
        repo,
        pull_number: issue_number,
    })
    .then(res => res.data) // res has other properties like status, headers, url, and data. the details of the pr is in the data property.
    .catch(err => {
        log.info(err, "error getting pr (issue) that triggered this workflow"); 
        throw err;
    });
}

/**
 * Gets the current PR's head commit's SHA. 
 * (Current PR means that the issue_number is taken from the github.context object)
 */
export async function getCurrentPRHeadSha() {
    const pr = await octokit.rest.pulls.get({
        owner,
        repo,
        pull_number: issue_number,
    }).catch(err => {throw err;});

    const sha = pr.data.head.sha;
    core.info(`PR head sha obtained for pr #${issue_number}: ${sha}`)

    return sha;
}


export async function isPrDraft() : Promise<boolean> {
    return await getCurrentPR()
        .then(pr => pr.draft)
        .catch(err => {throw err});
}