/**
 * Posts a label-specific contributor note on issues tagged with
 * `good first issue` or `help wanted`.
 */
module.exports = async ({ github, context, core }) => {
  const labelName = context.payload.label?.name;

  function buildBody(paragraphs) {
    return paragraphs.join('\n');
  }

  const COMMENT_TEXT_BY_LABEL = {
    'good first issue': buildBody([
      '**Good First Issue - Notes for Contributors**',
      '',
      'This issue is for **first-time contributors only**. If you are new to TEAMMATES, feel free to submit a PR for this issue.',
      '*Please note that we allow only one `good first issue` per contributor.* If you have already made a prior contribution to TEAMMATES, you may wish to take a look at issues with the `help wanted` tag instead.',
      '',
      "**We do not assign issues to contributors**. If you would like to pick up this issue, do post a comment below to express your interest and check if there is anyone else who is already working on the issue. We will do our best to reply and give you the go-ahead, but if we don't, feel free to submit a PR as long as there is no one else working on it.",
      '',
      '**To get started**, do read through our [contributing guidelines](https://teammates.github.io/teammates/contributing/guidelines.html) carefully before making a PR.',
      '',
      'If you need any clarifications on our [developer guide](https://teammates.github.io/teammates/index.html), or are facing issues that are not found in our [troubleshooting guide](https://teammates.github.io/teammates/troubleshooting-guide.html), please [post a message in our discussion forum](https://github.com/TEAMMATES/teammates/discussions).',
    ]),
    'help wanted': buildBody([
      '**Help Wanted - Notes for Contributors**',
      '',
      'This issue is open for contributions from anyone who would like to help.',
      '',
      '**We do not assign issues to contributors**. If you would like to pick up this issue, do post a comment below to express your interest and check if there is anyone else who is already working on the issue. We will do our best to reply and give you the go-ahead, but if we do not, feel free to submit a PR as long as there is no one else working on it.',
      '',
      '**To get started**, do read through our [contributing guidelines](https://teammates.github.io/teammates/contributing/guidelines.html) carefully before making a PR.',
      '',
      'If you need any clarifications on our [developer guide](https://teammates.github.io/teammates/index.html), or are facing issues that are not found in our [troubleshooting guide](https://teammates.github.io/teammates/troubleshooting-guide.html), please [post a message in our discussion forum](https://github.com/TEAMMATES/teammates/discussions).',
    ]),
  };

  const body = COMMENT_TEXT_BY_LABEL[labelName];

  if (!body) {
    core.info(`Skipping unsupported label: ${labelName}`);
    return;
  }

  await github.rest.issues.createComment({
    issue_number: context.issue.number,
    owner: context.repo.owner,
    repo: context.repo.repo,
    body,
  });
};
