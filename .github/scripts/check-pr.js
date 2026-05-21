// PR checks for TEAMMATES contribution guidelines; used by .github/workflows/pr.yml.
//
// The script posts at most one persistent comment per PR (identified by the
// COMMENT_SENTINEL below) and updates it on each run instead of stacking new
// comments. When there are no outstanding issues the comment is rewritten to
// reflect the all-clear state. When at least one violation is detected the
// workflow exits non-zero so the check shows up as failed on the PR.
const COMMENT_SENTINEL = '<!-- teammates-pr-checker:v1 -->';

const buildComment = (login, problems) => {
  const intro = `Hi @${login}, thank you for your interest in contributing to TEAMMATES!`;
  if (problems.length === 0) {
    return (
      `${COMMENT_SENTINEL}\n` +
      `${intro}\n\nNo outstanding [contributing guidelines](https://teammates.github.io/teammates/contributing/guidelines.html) issues remain on this PR.`
    );
  }
  const items = problems.map((line) => `- ${line}`).join('\n');
  return (
    `${COMMENT_SENTINEL}\n` +
    `${intro}\nHowever, your PR does not appear to follow our [contributing guidelines](https://teammates.github.io/teammates/contributing/guidelines.html):\n\n` +
    `${items}\n\nPlease address the above before we proceed to review your PR.`
  );
};

const findExistingComment = async ({ github, owner, repo, issue_number }) => {
  // Walk the comment list and look for our sentinel. The PR is usually small
  // enough that the first page covers it, but iterate just in case.
  let page = 1;
  for (;;) {
    const { data } = await github.rest.issues.listComments({
      owner,
      repo,
      issue_number,
      per_page: 100,
      page,
    });
    const match = data.find((c) => typeof c.body === 'string' && c.body.includes(COMMENT_SENTINEL));
    if (match) {
      return match;
    }
    if (data.length < 100) {
      return null;
    }
    page += 1;
  }
};

module.exports = async ({ github, context }) => {
  const pr = await github.rest.pulls.get({
    owner: context.repo.owner,
    repo: context.repo.repo,
    pull_number: context.issue.number,
  });
  const isTitleValid = /^\[#\d+\] /.test(pr.data.title);
  const isDescriptionValid = /([Ff]ix(es|ed)?|[Cc]lose(s|d)?|[Rr]esolve(s|d)?|[Pp]art [Oo]f) #\d+/.test(pr.data.body);
  const descriptionRegex = /(?:[Ff]ix(?:es|ed)?|[Cc]lose(?:s|d)?|[Rr]esolve(?:s|d)?|[Pp]art [Oo]f) #(\d+)/;
  const extractIssueNumber = (description) => {
    const match = description.match(descriptionRegex);
    return match ? parseInt(match[1], 10) : null;
  };
  const issueNumber = extractIssueNumber(pr.data.body);
  let isIssueOpen = false;
  if (issueNumber) {
    const issue = await github.rest.issues.get({
      owner: 'TEAMMATES',
      repo: 'teammates',
      issue_number: issueNumber,
    });
    isIssueOpen = issue.data.state === 'open';
  }

  const problems = [];
  if (!isTitleValid) {
    problems.push('Title must start with the issue number the PR is fixing in square brackets, e.g. `[#<issue-number>]`');
  }
  if (!isDescriptionValid) {
    problems.push(
      'Description must reference the issue number the PR is fixing, e.g. `Fixes #<issue-number>` (or `Part of #<issue-number>` if the PR does not address the issue fully)',
    );
  }
  if (!isIssueOpen && issueNumber) {
    problems.push(`The issue referenced in the description (#${issueNumber}) is not open.`);
  }

  const owner = context.repo.owner;
  const repo = context.repo.repo;
  const issue_number = context.issue.number;
  const existing = await findExistingComment({ github, owner, repo, issue_number });
  const body = buildComment(pr.data.user.login, problems);

  if (existing) {
    if (existing.body !== body) {
      await github.rest.issues.updateComment({
        owner,
        repo,
        comment_id: existing.id,
        body,
      });
    }
  } else if (problems.length > 0) {
    // Only post a fresh comment when there is something to flag; the all-clear
    // path with no prior comment stays silent to avoid surprising contributors
    // whose PRs were always compliant.
    await github.rest.issues.createComment({
      owner,
      repo,
      issue_number,
      body,
    });
  }

  if (problems.length > 0) {
    // Fail the workflow so the check appears red on the PR until the
    // contributor addresses the listed violations.
    throw new Error(`PR template violations remain: ${problems.length}`);
  }
};
