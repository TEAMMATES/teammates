#!/usr/bin/env node
/**
 * Wrapper around `prettier --check` that prints a remediation hint when
 * formatting issues are detected, so contributors know how to fix them.
 */

import { spawnSync } from 'node:child_process';

const result = spawnSync('npx', ['prettier', '.', '--check'], { stdio: 'inherit', shell: true });

if (result.status !== 0) {
  console.error('\nFormatting issues detected. To fix them, run:\n\n  npm run format\n');
  process.exit(result.status ?? 1);
}
