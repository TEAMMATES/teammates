// PR checks for TEAMMATES contribution guidelines; used by .github/workflows/pr.yml.

const BOT_COMMENT_MARKER = '<!-- teammates-pr-checker -->';

const getOrphanLockfiles = (changedPaths) => {
  const dirOf = (filePath) => {
    const idx = filePath.lastIndexOf('/');
    return idx === -1 ? '' : filePath.slice(0, idx);
  };
  const baseOf = (filePath) => {
    const idx = filePath.lastIndexOf('/');
    return idx === -1 ? filePath : filePath.slice(idx + 1);
  };
  const changedLockDirs = new Set();
  const changedManifestDirs = new Set();
  for (const filePath of changedPaths) {
    const base = baseOf(filePath);
    if (base === 'package-lock.json') {
      changedLockDirs.add(dirOf(filePath));
    } else if (base === 'package.json') {
      changedManifestDirs.add(dirOf(filePath));
    }
  }
  return [...changedLockDirs]
    .filter((dir) => !changedManifestDirs.has(dir))
    .sort()
    .map((dir) => (dir === '' ? 'package-lock.json' : `${dir}/package-lock.json`));
};

const findBotComment = async ({ github, context }) => {
  let page = 1;
  while (true) {
    const { data: comments } = await github.rest.issues.listComments({
      owner: context.repo.owner,
      repo: context.repo.repo,
      issue_number: context.issue.number,
      per_page: 100,
      page,
    });
    const botComment = comments.find(
      (c) => c.user?.login === 'github-actions[bot]' && c.body.includes(BOT_COMMENT_MARKER),
    );
    if (botComment) return botComment;
    if (comments.length < 100) return null;
    page += 1;
  }
};

const upsertComment = async ({ github, context, body }) => {
  const existingComment = await findBotComment({ github, context });
  if (existingComment) {
    await github.rest.issues.updateComment({
      owner: context.repo.owner,
      repo: context.repo.repo,
      comment_id: existingComment.id,
      body,
    });
  } else {
    await github.rest.issues.createComment({
      issue_number: context.issue.number,
      owner: context.repo.owner,
      repo: context.repo.repo,
      body,
    });
  }
};

const checkPr = async ({ github, context, core }) => {
  const pr = await github.rest.pulls.get({
    owner: context.repo.owner,
    repo: context.repo.repo,
    pull_number: context.issue.number,
  });
  const isTitleValid = /^\[#\d+\] /.test(pr.data.title);
  const isDescriptionValid = /([Ff]ix(es|ed)?|[Cc]lose(s|d)?|[Rr]esolve(s|d)?|[Pp]art [Oo]f) #\d+/.test(pr.data.body);
  const descriptionRegex = /(?:[Ff]ix(?:es|ed)?|[Cc]lose(?:s|d)?|[Rr]esolve(?:s|d)?|[Pp]art [Oo]f) #(\d+)/;
  const extractIssueNumber = (description) => {
    const match = description?.match(descriptionRegex);
    return match ? parseInt(match[1], 10) : null;
  };
  const issueNumber = extractIssueNumber(pr.data.body);

  const changedPaths = new Set();
  let page = 1;
  while (true) {
    const { data: files } = await github.rest.pulls.listFiles({
      owner: context.repo.owner,
      repo: context.repo.repo,
      pull_number: context.issue.number,
      per_page: 100,
      page,
    });
    for (const file of files) {
      changedPaths.add(file.filename);
    }
    if (files.length < 100) {
      break;
    }
    page += 1;
  }
  const orphanLockfiles = getOrphanLockfiles(changedPaths);
  const isLockfileChangeValid = orphanLockfiles.length === 0;

  let isIssueOpen = false;
  if (issueNumber) {
    const issue = await github.rest.issues.get({
      owner: 'TEAMMATES',
      repo: 'teammates',
      issue_number: issueNumber,
    });
    isIssueOpen = issue.data.state === 'open';
  }

  const hasViolations = !isTitleValid || !isDescriptionValid || (issueNumber && !isIssueOpen) || !isLockfileChangeValid;

  if (!hasViolations) {
    // All checks passed — update existing comment to reflect resolution, if one exists.
    const existingComment = await findBotComment({ github, context });
    if (existingComment) {
      await github.rest.issues.updateComment({
        owner: context.repo.owner,
        repo: context.repo.repo,
        comment_id: existingComment.id,
        body: `${BOT_COMMENT_MARKER}\nHi @${pr.data.user.login}, all PR guideline checks have passed. Thank you for your contribution! :tada:`,
      });
    }
    return;
  }

  let body =
    `${BOT_COMMENT_MARKER}\nHi @${pr.data.user.login}, thank you for your interest in contributing to TEAMMATES!\n` +
    `However, your PR does not appear to follow our [contributing guidelines](https://teammates.github.io/teammates/contributing/guidelines.html):\n\n`;
  if (!isTitleValid) {
    body += '- Title must start with the issue number the PR is fixing in square brackets, e.g. `[#<issue-number>]`\n';
  }
  if (!isDescriptionValid) {
    body +=
      '- Description must reference the issue number the PR is fixing, e.g. `Fixes #<issue-number>` (or `Part of #<issue-number>` if the PR does not address the issue fully)\n';
  }
  if (!isIssueOpen && issueNumber) {
    body += `- The issue referenced in the description (#${issueNumber}) is not open.\n`;
  }
  if (!isLockfileChangeValid) {
    body += `- This PR contains changes to \`package-lock.json\` without corresponding \`package.json\` changes. Please revert the \`package-lock.json\` changes.\n`;
  }
  body += '\nPlease address the above before we proceed to review your PR.';

  await upsertComment({ github, context, body });

  core.setFailed('PR does not follow contribution guidelines. See the comment on the PR for details.');
};

module.exports = checkPr;
module.exports.getOrphanLockfiles = getOrphanLockfiles;
