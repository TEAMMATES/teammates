/**
 * Detects comments requesting issue assignment and posts an auto-reply
 * for such comments.
 */
module.exports = async ({ github, context, core }) => {
    const REPLY_TEMPLATE =
`Hi @{username}, it looks like you are asking to be assigned to this issue. Thank you for your interest in contributing to TEAMMATES!

Please note that we do not generally assign issues to external contributors, but **feel free to open a PR**!
Please review our [Contributing Guidelines](https://teammates.github.io/teammates/contributing/guidelines.html) and [Development Workflow](https://teammates.github.io/teammates/contributing/development-workflow.html) before getting started. A few things to note:

- **You do not need to be assigned to an issue to work on it**. We only assign issues to core team members.
- Indicate your interest by commenting on the issue thread to avoid duplicated effort.
- Avoid working on issues that are already assigned, labelled on hold or core team only, or have open PRs.
- You may discuss alternative solutions on the issue thread before starting work — this reduces the chance of a rejected fix. But please note that we do not have the resources to offer detailed individual guidance.`;

    const ASSIGNMENT_REQUEST_PATTERNS = [
        // Patterns like: "please assign me", "can I be assigned", "could you assign"
        /(please|pls|can|could|may|would|want|like|love|be).*ass?i?gn(ed)?/i,

        // Patterns like: "assign me", "assign this to me", "assgn to me"
        /ass?i?gn(ed)?.*(me|myself)/i,
    ];

    function isAssignmentRequest(commentBody) {
        const normalizedComment = commentBody.toLowerCase();
        return ASSIGNMENT_REQUEST_PATTERNS.some(pattern => pattern.test(normalizedComment));
    }

    function createReplyMessage(username) {
        return REPLY_TEMPLATE.replace('{username}', username);
    }

    async function postReply(message) {
        return await github.rest.issues.createComment({
            owner: context.repo.owner,
            repo: context.repo.repo,
            issue_number: context.issue.number,
            body: message,
        });
    }

    try {
        const comment = context.payload.comment;
        const commentBody = comment.body;
        const commentUsername = comment.user.login;

        core.info(`Analyzing comment from @${commentUsername}`);
        core.debug(`Comment text: ${commentBody.substring(0, 100)}...`);

        if (!isAssignmentRequest(commentBody)) {
            return;
        }

        const replyMessage = createReplyMessage(commentUsername);
        await postReply(replyMessage);
    } catch (error) {
        core.setFailed(`Action failed: ${error.message}`);
        if (error.stack) {
            core.debug(error.stack);
        }
    }
};
