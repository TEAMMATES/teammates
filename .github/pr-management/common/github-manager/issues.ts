//// abstractions for adding and dropping labels

import * as core from '@actions/core'
import * as github from '@actions/github';
import { log } from '../../logger';
const token = core.getInput("repo-token");
const octokit = github.getOctokit(token);

const owner = github.context.repo.owner;
const repo = github.context.repo.repo;
const issue_number = github.context.issue.number;
const actor = github.context.actor;

export async function addLabel(labelName: string) {
    await octokit.rest.issues.addLabels({
        owner,
        repo,
        issue_number,
        labels: [labelName],
    })
    .then(res => log.info(res.status, `added ${labelName} label with status`))
    .catch(err => log.info(err, "error adding label"));
}

export async function removeLabel(labelName: string) {
    await octokit.rest.issues.removeLabel({
        owner,
        repo,
        issue_number,
        name: labelName,
    })
    .then(res => log.info(res.status, `removing label ${res.status} with status`))
    .catch(err => log.info(err, "error removing label (label may not have been applied)"));
}

/**
 * 
 * @returns A string array containing the names of the labels that are applied to the current PR
 */
export async function getCurrentPrLabels() : Promise<string[]> {
    return await octokit.rest.issues.get({
        owner,
        repo, 
        issue_number
    })
    .then(res => {
        // todo this function flattens the labels i think. not sure if it should be doing it at this level.
        const labels = res.data.labels.map((label: {name: string}) => label.name)
        core.info(`labels returned for pr ${issue_number}: ${labels}`)
        return labels;        
    }) 
    .catch(err => {core.error(err); throw err});
}


//// comments related functions
export async function postComment(message : string) {
    const commentBody = `Hi @${actor}, please note the following. ${message}`;

    await octokit.rest.issues.createComment({
        owner,
        repo,
        issue_number,
        body: commentBody,
    })
    .then(res => core.info(`Commented with status: ${res.status}:\n${res.data.body}\n`))
    .catch(err => core.error(err))
}

const sortByLastCreated = (a : {created_at: string}, b : {created_at: string}) : number => {
    if (!a.created_at || !b.created_at) return 1; // move back if created_at property is missing
    return Date.parse(b.created_at) - Date.parse(a.created_at)
}

/**
 * Returns an array of all the events on this issue, sorted in descending order of the created_at property. 
 * https://octokit.github.io/rest.js/v18#issues-list-events
 */
 export async function getSortedListOfEventsOnIssue() { // todo return type?
    return await octokit.rest.issues.listEvents({
        owner,
        repo,
        issue_number,
    })
    .then(res => res.data.sort(sortByLastCreated))
    .catch(err => {throw err;});
}

/**
 * Returns an array of events for the current issue, sorted in descending order of the created_at property.
 * https://octokit.github.io/rest.js/v18#issues-list-events
 */ 
export async function getSortedListOfComments(sinceTimeStamp : string) { // todo return type?
    return await octokit.rest.issues.listComments({
        owner,
        repo,
        issue_number,
        since: sinceTimeStamp
    })
    .then(res => res.data.sort(sortByLastCreated))
    .catch(err => {
        throw err;
    });
}

// todo return type?
export async function getCurrentIssue() {
    return octokit.rest.issues.get({
        owner, 
        repo,
        issue_number,
    }).catch(err => {throw err});
}
