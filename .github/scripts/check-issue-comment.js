/**
 * Assignment Request Detector
 *
 * This script detects comments requesting issue assignment and posts an auto-reply.
 * Used by GitHub Actions workflow.
 *
 * @param {Object} params - GitHub Actions context
 * @param {Object} params.github - Octokit instance for GitHub API
 * @param {Object} params.context - GitHub Actions context (event payload, repo info, etc.)
 * @param {Object} params.core - GitHub Actions core utilities
 */
module.exports = async ({ github, context, core }) => {

    // ============================================================================
    // CONFIGURATION
    // ============================================================================

    const ASSIGNMENT_REQUEST_PATTERNS = [
        // Patterns like: "please assign me", "can I be assigned", "could you assign"
        /\b(please|can|could|may|would|want|like)\b.*\b(assign(ed)?|assi?gn|assgn)\b/i,

        // Patterns like: "assign me", "assign this to me", "assgn to me"
        /\b(assign(ed)?|assi?gn|assgn)\b.*(me|to me|this to me|myself|I)\b/i,
    ];
    const REPLY_TEMPLATE = `Hi @{username}, it looks like you are asking to be assigned to this issue. Thank you for your interest in contributing to TEAMMATES!

Please note that we do not generally assign issues to external contributors, but feel free to open a PR!
Please review our [Contributing Guidelines](https://teammates.github.io/teammates/contributing/guidelines.html) and [Development Workflow](https://teammates.github.io/teammates/contributing/development-workflow.html) before getting started. A few things to note:

- You do not need to be assigned to an issue to work on it. We only assign issues to core team members.
- Indicate your interest by commenting on the issue thread to avoid duplicated effort.
- Avoid working on issues that are already assigned, labelled on hold or core team only, or have open PRs.
- You may discuss alternative solutions on the issue thread before starting work — this reduces the chance of a rejected fix. But please note that we do not have the resources to offer detailed individual guidance.`;


    // ============================================================================
    // HELPER FUNCTIONS
    // ============================================================================

    function isAssignmentRequest(commentBody) {
        const normalizedComment = commentBody.toLowerCase();
        return ASSIGNMENT_REQUEST_PATTERNS.some(pattern => {
            const match = pattern.test(normalizedComment);
            if (match) {
                core.debug(`Matched pattern: ${pattern}`);
            }
            return match;
        });
    }

    function createReplyMessage(username) {
        return REPLY_TEMPLATE.replace('{username}', username);
    }

    async function postComment(message) {
        return await github.rest.issues.createComment({
            owner: context.repo.owner,
            repo: context.repo.repo,
            issue_number: context.issue.number,
            body: message,
        });
    }


    // ============================================================================
    // MAIN LOGIC
    // ============================================================================

    try {
        const comment = context.payload.comment;
        const issue = context.payload.issue;
        if (!comment || !issue) {
            core.setFailed('Missing comment or issue data in event payload');
            return;
        }

        const commentBody = comment.body || '';
        const commenter = comment.user.login;

        core.info(`Analyzing comment from @${commenter}`);
        core.debug(`Comment text: ${commentBody.substring(0, 100)}...`);

        if (!isAssignmentRequest(commentBody)) {
            return;
        }

        const replyMessage = createReplyMessage(commenter);
        await postComment(replyMessage);
    } catch (error) {
        core.setFailed(`Action failed: ${error.message}`);
        if (error.stack) {
            core.debug(error.stack);
        }
    }
};