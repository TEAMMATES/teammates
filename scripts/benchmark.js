#!/usr/bin/env node

/**
 * Benchmarks one API endpoint on a running TEAMMATES server.
 *
 * Usage:
 *   npm run benchmark -- [path] [options]
 * 
 * Help:
 *  npm run benchmark -- --help
 */

const fs = require('node:fs');
const path = require('node:path');
const { performance } = require('node:perf_hooks');

const {
  BenchmarkUtils,
  DEFAULT_ACCOUNT,
  DEFAULT_COMPARE_AFTER,
  DEFAULT_COMPARE_BEFORE,
  DEFAULT_ENDPOINT,
  DEFAULT_METHOD,
  DEFAULT_OUTPUT_DIR,
  DEFAULT_PORT,
  DEFAULT_RUNS,
  DEFAULT_WARMUP,
} = require('./benchmark-utils');
const { ProgressBar } = require('./progress-bar');

function parseArgs(argv) {
  const args = {
    runs: DEFAULT_RUNS,
    warmup: DEFAULT_WARMUP,
    endpoint: DEFAULT_ENDPOINT,
    method: DEFAULT_METHOD,
    baseUrl: BenchmarkUtils.buildLocalBaseUrl(DEFAULT_PORT),
    account: DEFAULT_ACCOUNT,
    params: [],
    headers: [],
  };

  for (let i = 0; i < argv.length; i += 1) {
    const arg = argv[i];
    const next = argv[i + 1];
    switch (arg) {
      case '-p':
      case '--port':
        args.baseUrl = BenchmarkUtils.buildLocalBaseUrl(BenchmarkUtils.parsePort(next));
        i += 1;
        break;
      case '-e':
      case '--ep':
        args.endpoint = next;
        i += 1;
        break;
      case '-X':
      case '--request':
        args.method = next.toUpperCase();
        i += 1;
        break;
      case '--entity':
        args.params.push(['entitytype', next]);
        i += 1;
        break;
      case '-a':
      case '--acc':
        args.account = next;
        i += 1;
        break;
      case '--bd':
        args.backdoorKey = next;
        i += 1;
        break;
      case '--csrf':
        args.csrfKey = next;
        i += 1;
        break;
      case '-l':
      case '--label':
        args.label = next;
        i += 1;
        break;
      case '--compare':
        args.compareFiles = BenchmarkUtils.getCompareFiles(argv, i);
        i += args.compareFiles.consumed;
        break;
      case '-d':
      case '--data':
        args.body = next;
        i += 1;
        break;
      case '--data-file':
        args.body = fs.readFileSync(next, 'utf8');
        i += 1;
        break;
      case '-r':
      case '--runs':
        args.runs = BenchmarkUtils.parsePositiveInteger(next, '--runs');
        i += 1;
        break;
      case '-w':
      case '--warmup':
        args.warmup = BenchmarkUtils.parseNonNegativeInteger(next, '--warmup');
        i += 1;
        break;
      case '-q':
      case '--data-urlencode':
        args.params.push(BenchmarkUtils.parseNameValue(next, '-q'));
        i += 1;
        break;
      case '-H':
      case '--header':
        args.headers.push(BenchmarkUtils.parseNameValue(next, '--header'));
        i += 1;
        break;
      case '--help':
      case '-h':
        BenchmarkUtils.printUsageAndExit(0);
        break;
      default:
        if (!arg.startsWith('-')) {
          args.endpoint = arg;
          break;
        }
        throw new Error(`Unknown argument: ${arg}`);
    }
  }

  if (args.compareFiles) {
    return args;
  }

  if (args.label && ![DEFAULT_COMPARE_BEFORE, DEFAULT_COMPARE_AFTER].includes(args.label)) {
    throw new Error(`-l must be either ${DEFAULT_COMPARE_BEFORE} or ${DEFAULT_COMPARE_AFTER}.`);
  }

  if (!['GET', 'POST', 'PUT', 'DELETE'].includes(args.method)) {
    throw new Error('-X must be one of GET, POST, PUT, DELETE.');
  }

  return args;
}

async function runBenchmark(label, url, headers, method, body, runs, warmup) {
  const timings = [];
  let responseBytes = 0;
  const progressBar = new ProgressBar();
  const totalRequests = warmup + runs;
  let completedRequests = 0;

  progressBar.render(completedRequests, totalRequests);
  try {
    for (let i = 0; i < warmup; i += 1) {
      await timedRequest(url, headers, method, body);
      completedRequests += 1;
      progressBar.render(completedRequests, totalRequests);
    }

    for (let i = 0; i < runs; i += 1) {
      const result = await timedRequest(url, headers, method, body);
      timings.push(result.durationMs);
      responseBytes = result.responseBytes;
      completedRequests += 1;
      progressBar.render(completedRequests, totalRequests);
    }
  } finally {
    progressBar.clear();
  }

  return {
    label,
    url: url.toString(),
    method,
    runs,
    warmup,
    responseBytes,
    ...BenchmarkUtils.summarize(timings),
  };
}

async function timedRequest(url, headers, method, body) {
  const start = performance.now();
  const response = await BenchmarkUtils.fetchOrThrow(url, {
    method,
    headers,
    body: method === 'GET' || method === 'DELETE' ? undefined : body,
  });
  const responseBody = await response.text();
  const durationMs = performance.now() - start;

  if (!response.ok) {
    throw new Error(`${method} ${url} failed with HTTP ${response.status}: ${responseBody.slice(0, 500)}`);
  }

  return {
    durationMs,
    responseBytes: Buffer.byteLength(responseBody),
  };
}

function printReport(result, args) {
  console.log(
    [
      '',
      'API benchmark',
      '=============',
      `${result.method} ${result.url}`,
      `Runs: ${result.runs} measured, ${result.warmup} warmup`,
      `Account: ${args.account}`,
      '',
      'Latency',
      `  mean  ${BenchmarkUtils.formatMs(result.mean)}`,
      `  min   ${BenchmarkUtils.formatMs(result.min)}`,
      `  max   ${BenchmarkUtils.formatMs(result.max)}`,
      '',
      'Throughput',
      `  ${BenchmarkUtils.formatReqPerSec(result.reqPerSec)}`,
      '',
      'Response',
      `  ${result.responseBytes} bytes`,
      '',
      'Copy',
      `  ${BenchmarkUtils.formatCopyLine(result)}`,
    ].join('\n'),
  );
}

function writeResultFile(label, result, args) {
  const resolvedPath = BenchmarkUtils.resolveResultFile(label);
  const savedResult = {
    version: 1,
    label,
    savedAt: new Date().toISOString(),
    method: result.method,
    url: result.url,
    account: args.account,
    runs: result.runs,
    warmup: result.warmup,
    responseBytes: result.responseBytes,
    minMs: BenchmarkUtils.roundOneDecimal(result.min),
    meanMs: BenchmarkUtils.roundOneDecimal(result.mean),
    maxMs: BenchmarkUtils.roundOneDecimal(result.max),
    reqPerSec: BenchmarkUtils.roundOneDecimal(result.reqPerSec),
  };

  fs.mkdirSync(path.dirname(resolvedPath), { recursive: true });
  fs.writeFileSync(resolvedPath, `${JSON.stringify(savedResult, null, 2)}\n`);
  return resolvedPath;
}

function readResultFile(filePath, outputDir = DEFAULT_OUTPUT_DIR) {
  const resolvedPath = BenchmarkUtils.resolveResultFile(filePath, outputDir);
  const result = JSON.parse(fs.readFileSync(resolvedPath, 'utf8'));
  const requiredFields = ['method', 'url', 'runs', 'warmup', 'meanMs', 'minMs', 'maxMs', 'reqPerSec', 'responseBytes'];
  for (const field of requiredFields) {
    if (result[field] === undefined) {
      throw new Error(`${resolvedPath} is missing benchmark field: ${field}`);
    }
  }
  return {
    ...result,
    label: result.label || resolvedPath,
  };
}

function printComparison(before, after) {
  const lines = [
    '',
    'Benchmark comparison',
    '====================',
    'Before',
    `  ${before.method} ${before.url}`,
    `  Runs: ${before.runs} measured, ${before.warmup} warmup`,
    '',
    'After',
    `  ${after.method} ${after.url}`,
    `  Runs: ${after.runs} measured, ${after.warmup} warmup`,
  ];
  lines.push(
    '',
    'Latency',
    `  mean  ${BenchmarkUtils.formatComparisonMs(before.meanMs, after.meanMs, false)}`,
    `  min   ${BenchmarkUtils.formatComparisonMs(before.minMs, after.minMs, false)}`,
    `  max   ${BenchmarkUtils.formatComparisonMs(before.maxMs, after.maxMs, false)}`,
    '',
    'Throughput',
    `  ${BenchmarkUtils.formatComparisonReqPerSec(before.reqPerSec, after.reqPerSec, true)}`,
    '',
    'Response',
    `  ${before.responseBytes} -> ${after.responseBytes} bytes`,
  );
  console.log(lines.join('\n'));
}

async function main() {
  try {
    const args = parseArgs(process.argv.slice(2));
    if (args.compareFiles) {
      const before = readResultFile(args.compareFiles.files[0]);
      const after = readResultFile(args.compareFiles.files[1]);
      printComparison(before, after);
      return;
    }

    const url = BenchmarkUtils.buildUrl(args.baseUrl, args);
    const headers = await BenchmarkUtils.getHeadersForBaseUrl(args.baseUrl, args);
    const result = await runBenchmark(
      args.label || 'current',
      url,
      headers,
      args.method,
      args.body,
      args.runs,
      args.warmup,
    );
    printReport(result, args);
    if (args.label) {
      const savedPath = writeResultFile(args.label, result, args);
      console.log('');
      console.log(`Saved: ${savedPath}`);
    }
  } catch (error) {
    console.error(error.message);
    console.error('Run with --help for usage.');
    process.exit(1);
  }
}

main();
