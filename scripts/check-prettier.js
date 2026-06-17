#!/usr/bin/env node

const fs = require('node:fs/promises');
const path = require('node:path');
const fg = require('fast-glob');
const prettier = require('prettier');
const { Concurrency } = require('./concurrency');
const { ProgressBar } = require('./progress-bar');

const repoRoot = path.resolve(__dirname, '..');
const ignorePaths = ['.prettierignore', '.gitignore'].map((ignorePath) => path.join(repoRoot, ignorePath));
const concurrency = new Concurrency();

const yellow = (text) => `\u001B[33m${text}\u001B[39m`;

async function getPrettierFiles() {
  const entries = await fg('**/*', {
    cwd: repoRoot,
    dot: true,
    ignore: ['**/node_modules/**', '**/.git/**', '**/.hg/**', '**/.svn/**'],
    onlyFiles: true,
    unique: true,
  });

  const files = await concurrency.map(entries, async (entry) => {
    const filePath = path.join(repoRoot, entry);
    const fileInfo = await prettier.getFileInfo(filePath, {
      ignorePath: ignorePaths,
      withNodeModules: false,
    });

    if (!fileInfo.ignored && fileInfo.inferredParser) {
      return filePath;
    }

    return null;
  });

  return files.filter(Boolean);
}

function printFailureMessage() {
  console.error(
    [
      '',
      '✖ Prettier found formatting issues.',
      '',
      'To fix this automatically, run:',
      '',
      '    npm run format',
      '',
      'Then rerun your previous command.',
      '',
    ].join('\n'),
  );
}

async function main() {
  console.log('Gathering files...');

  const files = await getPrettierFiles();

  console.log(`\nFound ${files.length} files.`);

  let completedFiles = 0;
  const progressBar = new ProgressBar();

  console.log('\nChecking formatting...');
  progressBar.render(0, files.length);

  const results = await concurrency.map(files, async (filePath) => {
    const [options, fileContent] = await Promise.all([
      prettier.resolveConfig(filePath, { useCache: true }),
      fs.readFile(filePath, 'utf8'),
    ]);
    const isFormatted = await prettier.check(fileContent, {
      ...(options ?? {}),
      filepath: filePath,
    });

    completedFiles += 1;
    progressBar.render(completedFiles, files.length);

    return isFormatted ? null : path.relative(repoRoot, filePath);
  });

  progressBar.clear();

  const unformattedFiles = results.filter(Boolean);

  if (unformattedFiles.length === 0) {
    console.log('\n✔ All files use Prettier code style!');
    return;
  }

  for (const file of unformattedFiles) {
    console.warn(`[${yellow('warn')}] ${file}`);
  }

  printFailureMessage();
  process.exitCode = 1;
}

main().catch((error) => {
  console.error(`✖ Unable to run Prettier: ${error.message}`);
  process.exit(1);
});
