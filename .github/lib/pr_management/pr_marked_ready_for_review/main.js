"use strict";
var __awaiter = (this && this.__awaiter) || function (thisArg, _arguments, P, generator) {
    function adopt(value) { return value instanceof P ? value : new P(function (resolve) { resolve(value); }); }
    return new (P || (P = Promise))(function (resolve, reject) {
        function fulfilled(value) { try { step(generator.next(value)); } catch (e) { reject(e); } }
        function rejected(value) { try { step(generator["throw"](value)); } catch (e) { reject(e); } }
        function step(result) { result.done ? resolve(result.value) : adopt(result.value).then(fulfilled, rejected); }
        step((generator = generator.apply(thisArg, _arguments || [])).next());
    });
};
Object.defineProperty(exports, "__esModule", { value: true });
const core = require("@actions/core");
const github = require("@actions/github");
const common_1 = require("../common");
const token = core.getInput("repo-token");
const octokit = github.getOctokit(token);
// params to set for api requests
const owner = github.context.repo.owner;
const repo = github.context.repo.repo;
const issue_number = github.context.issue.number;
const furtherInstructions = "Please comment `@bot ready for review` when you've passed all checks, resolved merge conflicts and are ready to request a review.";
function run() {
    return __awaiter(this, void 0, void 0, function* () {
        common_1.log.info(github.context.action, "action");
        common_1.log.info(github.context.payload.action, "payload action");
        if (!(yield isPRMarkedReadyForReview()))
            return; // needed because synchronise event triggers this workflow on even draft PRs
        const prLabels = yield octokit.rest.issues.get({
            owner,
            repo,
            issue_number
        })
            .then(res => res.data.labels.map(label => label.name || label)) // label may be of type string instead of an object so need this ||
            .then(l => common_1.log.info(l, `labels returned for pr ${issue_number}`))
            .catch(err => { core.info(err); throw err; });
        const { didChecksRunSuccessfully, errMessage } = yield common_1.validateChecksOnPrHead();
        if (didChecksRunSuccessfully) {
            if (hasLabel(prLabels, "s.ToReview")) {
                core.info("already has review label and checks are passing, nothing to be done here. exiting...");
                return;
            }
            if (hasLabel(prLabels, "s.Ongoing") && isOnSynchronise()) {
                core.info("Waiting for user to manually state ready to review. exiting...");
                return;
            }
            // if checks pass on 'pr open' event, or on 'convert to ready for review' event, 
            // then add review label (and drop ongoing if it exists)
            if (hasLabel(prLabels, "s.Ongoing")) {
                yield common_1.dropOngoingLabel();
            }
            yield common_1.addToReviewLabel();
        }
        else {
            // for prs labelled as ongoing, for all other event types except on synchronise, 
            // we can be sure that the author hasn't been notified of the failing checks by the bot
            if (hasLabel(prLabels, "s.Ongoing") && isOnSynchronise() && (yield wasAuthorLinkedToFailingChecks())) {
                core.info("PR has the ongoing label and author has been notified, exiting...");
                return;
            }
            if (hasLabel(prLabels, "s.ToReview")) {
                yield common_1.dropToReviewLabelAndAddOngoing();
            }
            else if (!hasLabel(prLabels, "s.ToReview") && !hasLabel(prLabels, "s.Ongoing")) {
                yield common_1.addOngoingLabel();
            }
            yield common_1.postComment(errMessage + "\n" + furtherInstructions);
        }
    });
}
run();
///// HELPER FUNCTIONS /////
/* did the currently running action get triggered by an on synchronise event */
function isOnSynchronise() {
    common_1.log.info(github.context.payload.action, "what triggered this run");
    return github.context.payload.action === "synchronize";
}
function hasLabel(arrayOfLabels, label) {
    return arrayOfLabels.findIndex(l => l === label) !== -1;
}
/**
 * Checks if the bot did post a comment notifying the author of failing checks, from the last time the s.Ongoing label was applied.
 * This function is necessary for this case:
 * A draft pr has an ongoing label -> author converts to ready for review but there's failing checks. The bot should comment once (i think).
 *
 * There are two rest requests in this function itself, and this file is ran on every commit
 */
function wasAuthorLinkedToFailingChecks() {
    return __awaiter(this, void 0, void 0, function* () {
        // sort by latest event first, so that we consider the last time that the toReview label was added
        const sortFn = (a, b) => {
            if (!a.created_at || !b.created_at)
                return 1; // move back
            return Date.parse(b.created_at) - Date.parse(a.created_at);
        };
        // get an array of events for the current issue (https://octokit.github.io/rest.js/v18#issues-list-events)
        const events = yield octokit.rest.issues.listEvents({
            owner,
            repo,
            issue_number,
        })
            .then(res => res.data.sort(sortFn))
            .catch(err => {
            throw err;
        });
        const labelEvent = events.find(e => { var _a; return e.event === "labeled" && ((_a = e.label) === null || _a === void 0 ? void 0 : _a.name) == "s.Ongoing"; });
        if (!labelEvent) {
            core.warning("Some wrong assumption may have been made or the API used to fetch the PRs may have changed. This function should have been called only on PRs that are assigned the label.");
            return true; // skip adding a comment 
        }
        // // get an array of events for the current issue (https://octokit.github.io/rest.js/v18#issues-list-events)
        const comments = yield octokit.rest.issues.listComments({
            owner,
            repo,
            issue_number,
            since: labelEvent.created_at
        })
            .then(res => res.data.sort(sortFn))
            .catch(err => {
            throw err;
        });
        const checksFailedComment = comments.find(c => c.body.search("There were failing checks found"));
        common_1.log.info(checksFailedComment, "checksFailedComment");
        return !!checksFailedComment;
    });
}
function isPRMarkedReadyForReview() {
    return __awaiter(this, void 0, void 0, function* () {
        return yield octokit.rest.pulls.get({
            owner,
            repo,
            pull_number: issue_number,
        })
            .then(res => {
            common_1.log.info(res.data.draft, `is pr ${issue_number} draft`);
            return !res.data.draft;
        })
            .catch(err => { common_1.log.info(err, "Error getting the pr that triggered this workflow"); throw err; });
    });
}
