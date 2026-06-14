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
  console.error('');
  console.error('Prettier found formatting issues.');
  console.error('');
  console.error('To automatically fix them, run:');
  console.error('');
  console.error('  npm run format');
  console.error('');
  console.error('Then rerun your previous command.');
  console.error('');
}

process.exit(exitCode);
