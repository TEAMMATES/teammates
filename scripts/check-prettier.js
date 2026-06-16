#!/usr/bin/env node

const fs = require('node:fs/promises');
const path = require('node:path');
const fg = require('fast-glob');
const prettier = require('prettier');

const repoRoot = path.resolve(__dirname, '..');
const ignorePaths = ['.prettierignore', '.gitignore'].map((ignorePath) => path.join(repoRoot, ignorePath));
const yellow = (text) => `\u001B[33m${text}\u001B[39m`;

async function getPrettierFiles() {
  const entries = await fg('**/*', {
    cwd: repoRoot,
    dot: true,
    ignore: ['**/node_modules/**', '**/.git/**', '**/.hg/**', '**/.svn/**'],
    onlyFiles: true,
    unique: true,
  });

  const files = [];
  for (const entry of entries) {
    const filePath = path.join(repoRoot, entry);
    const fileInfo = await prettier.getFileInfo(filePath, {
      ignorePath: ignorePaths,
      resolveConfig: true,
      withNodeModules: false,
    });

    if (!fileInfo.ignored && fileInfo.inferredParser) {
      files.push(filePath);
    }
  }

  return files;
}

function printFailureMessage() {
  console.error(
    [
      '',
      'Prettier found formatting issues.',
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
  console.log('Checking formatting...');

  const files = await getPrettierFiles();
  const unformattedFiles = [];

  for (const filePath of files) {
    const options = (await prettier.resolveConfig(filePath)) ?? {};
    const fileContent = await fs.readFile(filePath, 'utf8');
    const isFormatted = await prettier.check(fileContent, {
      ...options,
      filepath: filePath,
    });

    if (!isFormatted) {
      unformattedFiles.push(path.relative(repoRoot, filePath));
    }
  }

  if (unformattedFiles.length === 0) {
    console.log('All matched files use Prettier code style!');
    return;
  }

  for (const file of unformattedFiles) {
    console.warn(`[${yellow('warn')}] ${file}`);
  }

  printFailureMessage();
  process.exitCode = 1;
}

main().catch((error) => {
  console.error(`Unable to run Prettier: ${error.message}`);
  process.exit(1);
});
