#!/usr/bin/env node

const { spawnSync } = require('node:child_process');
const path = require('node:path');

const prettierBin = require.resolve('prettier/bin/prettier.cjs');
const result = spawnSync(process.execPath, [prettierBin, '.', '--check'], {
  cwd: path.resolve(__dirname, '..'),
  stdio: 'inherit',
});

if (result.error) {
  console.error(`Unable to run Prettier: ${result.error.message}`);
  process.exit(1);
}

const exitCode = typeof result.status === 'number' ? result.status : 1;

if (exitCode !== 0) {
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

process.exit(exitCode);
