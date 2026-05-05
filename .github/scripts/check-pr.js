// PR checks for TEAMMATES contribution guidelines; used by .github/workflows/pr.yml.
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

  // Detect package-lock.json changes that aren't accompanied by package.json changes.
  // Walks paginated file lists so PRs touching > 100 files still get an accurate signal.
  const changedPaths = new Set();
  const perPage = 100;
  for (let page = 1; ; page += 1) {
    const files = await github.rest.pulls.listFiles({
      owner: context.repo.owner,
      repo: context.repo.repo,
      pull_number: context.issue.number,
      per_page: perPage,
      page,
    });
    for (const f of files.data) {
      changedPaths.add(f.filename);
    }
    if (files.data.length < perPage) {
      break;
    }
  }
  const lockfileBasename = 'package-lock.json';
  const manifestBasename = 'package.json';
  const changedLockDirs = new Set();
  for (const p of changedPaths) {
    const lastSlash = p.lastIndexOf('/');
    const basename = lastSlash === -1 ? p : p.slice(lastSlash + 1);
    if (basename === lockfileBasename) {
      changedLockDirs.add(lastSlash === -1 ? '' : p.slice(0, lastSlash));
    }
  }
  const changedManifestDirs = new Set();
  for (const p of changedPaths) {
    const lastSlash = p.lastIndexOf('/');
    const basename = lastSlash === -1 ? p : p.slice(lastSlash + 1);
    if (basename === manifestBasename) {
      changedManifestDirs.add(lastSlash === -1 ? '' : p.slice(0, lastSlash));
    }
  }
  const orphanLockDirs = [...changedLockDirs].filter((d) => !changedManifestDirs.has(d));
  const isLockfileChangeValid = orphanLockDirs.length === 0;

  if (isTitleValid && isDescriptionValid && isIssueOpen && isLockfileChangeValid) {
    return;
  }
  let body = `Hi @${pr.data.user.login}, thank you for your interest in contributing to TEAMMATES!
              However, your PR does not appear to follow our [contributing guidelines](https://teammates.github.io/teammates/contributing/guidelines.html):\n\n`;
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
    const formatted = orphanLockDirs
      .map((d) => (d === '' ? '`package-lock.json`' : `\`${d}/package-lock.json\``))
      .join(', ');
    body +=
      `- ${formatted} changed without a corresponding \`package.json\` change. ` +
      'Lockfile updates should accompany dependency changes in `package.json`; if this PR is intentionally only refreshing the lockfile, please call that out in the description.\n';
  }
  body += '\nPlease address the above before we proceed to review your PR.';
  await github.rest.issues.createComment({
    issue_number: context.issue.number,
    owner: context.repo.owner,
    repo: context.repo.repo,
    body,
  });
};
