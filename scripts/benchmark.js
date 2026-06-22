#!/usr/bin/env node

/**
 * Benchmarks one API endpoint on a running TEAMMATES server.
 *
 * Start the version you want to measure, then run for example:
 *
 * npm run benchmark -- \
 *   sessions \
 *   -q entitytype=instructor \
 *   -r 100 \
 *   -w 10
 *
 * The --acc shortcut fetches an AUTH-TOKEN via the dev backdoor and is
 * intentionally restricted to localhost/127.0.0.1/::1 targets.
 */

const fs = require('node:fs');
const { performance } = require('node:perf_hooks');

const DEFAULT_PORT = 8080;
const DEFAULT_RUNS = 50;
const DEFAULT_WARMUP = 5;
const API_PREFIX = '/webapi';
const DEFAULT_ENDPOINT = 'sessions';
const DEFAULT_METHOD = 'GET';
const DEFAULT_ACCOUNT = 'app.admin@gmail.com';
const DEFAULT_BACKDOOR_KEY = 'samplekey';
const DEFAULT_CSRF_KEY = 'samplekey';
const AUTH_COOKIE_NAME = 'AUTH-TOKEN';
const USER_COOKIE_ENDPOINT = '/webapi/cookie';

function parseArgs(argv) {
  const args = {
    runs: DEFAULT_RUNS,
    warmup: DEFAULT_WARMUP,
    endpoint: DEFAULT_ENDPOINT,
    method: DEFAULT_METHOD,
    baseUrl: buildLocalBaseUrl(DEFAULT_PORT),
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
        args.baseUrl = buildLocalBaseUrl(parsePort(next));
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
        args.runs = parsePositiveInteger(next, '--runs');
        i += 1;
        break;
      case '-w':
      case '--warmup':
        args.warmup = parseNonNegativeInteger(next, '--warmup');
        i += 1;
        break;
      case '-q':
      case '--data-urlencode':
        args.params.push(parseNameValue(next, '-q'));
        i += 1;
        break;
      case '-H':
      case '--header':
        args.headers.push(parseNameValue(next, '--header'));
        i += 1;
        break;
      case '--help':
      case '-h':
        printUsageAndExit(0);
        break;
      default:
        if (!arg.startsWith('-')) {
          args.endpoint = arg;
          break;
        }
        throw new Error(`Unknown argument: ${arg}`);
    }
  }

  if (!['GET', 'POST', 'PUT', 'DELETE'].includes(args.method)) {
    throw new Error('-X must be one of GET, POST, PUT, DELETE.');
  }

  return args;
}

function buildLocalBaseUrl(port) {
  return `http://localhost:${port}`;
}

function parsePort(value) {
  const parsed = parsePositiveInteger(value, '--port');
  if (parsed > 65535) {
    throw new Error('--port must be between 1 and 65535.');
  }
  return parsed;
}

function parsePositiveInteger(value, flag) {
  const parsed = Number.parseInt(value, 10);
  if (!Number.isInteger(parsed) || parsed <= 0) {
    throw new Error(`${flag} must be a positive integer.`);
  }
  return parsed;
}

function parseNonNegativeInteger(value, flag) {
  const parsed = Number.parseInt(value, 10);
  if (!Number.isInteger(parsed) || parsed < 0) {
    throw new Error(`${flag} must be a non-negative integer.`);
  }
  return parsed;
}

function parseNameValue(value, flag) {
  if (!value || !value.includes('=')) {
    throw new Error(`${flag} must be in name=value format.`);
  }
  const index = value.indexOf('=');
  return [value.slice(0, index), value.slice(index + 1)];
}

function buildUrl(baseUrl, args) {
  const url = new URL(normalizeEndpoint(args.endpoint), withTrailingSlash(baseUrl));
  for (const [name, value] of args.params) {
    url.searchParams.set(name, value);
  }
  return url;
}

function normalizeEndpoint(endpoint) {
  const path = endpoint.startsWith('/') ? endpoint : `/${endpoint}`;
  if (path === API_PREFIX || path.startsWith(`${API_PREFIX}/`)) {
    return path;
  }
  return `${API_PREFIX}${path}`;
}

function withTrailingSlash(url) {
  return url.endsWith('/') ? url : `${url}/`;
}

function buildHeaders(args, cookie) {
  const headers = {
    Accept: 'application/json',
  };
  if (cookie) {
    headers.Cookie = cookie;
  }
  if (args.body) {
    headers['Content-Type'] = 'application/json';
  }
  for (const [name, value] of args.headers) {
    headers[name] = value;
  }
  return headers;
}

async function getHeadersForBaseUrl(baseUrl, args) {
  const cookie = await getLocalBackdoorCookie(baseUrl, args);
  return buildHeaders(args, `${AUTH_COOKIE_NAME}=${cookie}`);
}

async function getLocalBackdoorCookie(baseUrl, args) {
  const url = new URL(USER_COOKIE_ENDPOINT, withTrailingSlash(baseUrl));
  url.searchParams.set('accountemail', args.account);

  const response = await fetchOrThrow(url, {
    method: 'POST',
    headers: {
      Accept: 'application/json',
      'Backdoor-Key': args.backdoorKey || DEFAULT_BACKDOOR_KEY,
      'CSRF-Key': args.csrfKey || DEFAULT_CSRF_KEY,
    },
  });
  const body = await response.text();
  if (!response.ok) {
    throw new Error(`Failed to get local backdoor cookie from ${url}: HTTP ${response.status}: ${body.slice(0, 500)}`);
  }

  const parsedBody = JSON.parse(body);
  if (!parsedBody.message) {
    throw new Error(`Backdoor cookie response from ${url} did not contain a message field.`);
  }
  return parsedBody.message;
}

async function runBenchmark(label, url, headers, method, body, runs, warmup) {
  for (let i = 0; i < warmup; i += 1) {
    await timedRequest(url, headers, method, body);
  }

  const timings = [];
  let responseBytes = 0;
  for (let i = 0; i < runs; i += 1) {
    const result = await timedRequest(url, headers, method, body);
    timings.push(result.durationMs);
    responseBytes = result.responseBytes;
  }

  return {
    label,
    url: url.toString(),
    runs,
    responseBytes,
    ...summarize(timings),
  };
}

async function timedRequest(url, headers, method, body) {
  const start = performance.now();
  const response = await fetchOrThrow(url, {
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

async function fetchOrThrow(url, options) {
  try {
    return await fetch(url, options);
  } catch (error) {
    throw new Error(`Could not connect to ${url}. Is the server running? (${error.message})`);
  }
}

function summarize(values) {
  const sorted = [...values].sort((a, b) => a - b);
  return {
    min: sorted[0],
    p50: percentile(sorted, 50),
    p95: percentile(sorted, 95),
    max: sorted[sorted.length - 1],
    mean: sorted.reduce((sum, value) => sum + value, 0) / sorted.length,
  };
}

function percentile(sortedValues, percentileValue) {
  if (sortedValues.length === 1) {
    return sortedValues[0];
  }
  const index = (percentileValue / 100) * (sortedValues.length - 1);
  const lower = Math.floor(index);
  const upper = Math.ceil(index);
  if (lower === upper) {
    return sortedValues[lower];
  }
  const weight = index - lower;
  return sortedValues[lower] * (1 - weight) + sortedValues[upper] * weight;
}

function printReport(result) {
  console.log('');
  console.log('API benchmark');
  console.log('=============');
  console.log(`URL: ${result.url}`);
  console.log('');
  console.table([formatResult(result)]);
}

function formatResult(result) {
  return {
    version: result.label,
    runs: result.runs,
    bytes: result.responseBytes,
    min: formatMs(result.min),
    p50: formatMs(result.p50),
    p95: formatMs(result.p95),
    max: formatMs(result.max),
    mean: formatMs(result.mean),
  };
}

function formatMs(value) {
  return `${value.toFixed(1)} ms`;
}

function printUsageAndExit(exitCode) {
  console.log(`Usage:
node scripts/benchmark.js [path] [options]

Options:
  path                       Endpoint path under /webapi. Default: ${DEFAULT_ENDPOINT}
  -p, --port <port>          Local server port. Default: ${DEFAULT_PORT}
  -e, --ep <path>            Endpoint path. Useful when you prefer flags over positional args.
  -X, --request <method>     GET, POST, PUT, or DELETE. Default: ${DEFAULT_METHOD}
  -q name=value              Query parameter. Can be repeated.
  --data-urlencode name=value
                             Curl-style alias for -q.
  -H, --header name=value    Request header. Can be repeated.
  -d, --data <json>          Request body for POST/PUT.
  --data-file <path>         Request body file for POST/PUT.
  -r, --runs <n>             Measured requests. Default: ${DEFAULT_RUNS}
  -w, --warmup <n>           Warmup requests. Default: ${DEFAULT_WARMUP}
  -a, --acc <email>          Account used to fetch AUTH-TOKEN via /webapi/cookie. Default: ${DEFAULT_ACCOUNT}
  --bd <key>                 Backdoor key for --acc. Default: ${DEFAULT_BACKDOOR_KEY}
  --csrf <key>               CSRF bypass key for --acc. Default: ${DEFAULT_CSRF_KEY}
  --entity <value>           Convenience alias for -q entitytype=<value>.

Examples:
  npm run benchmark -- sessions -q entitytype=student
  npm run benchmark -- sessions -p 8081 -q entitytype=instructor -q isinrecyclebin=false
  npm run benchmark -- sessions -X POST -H Content-Type=application/json -d '{"key":"value"}'
`);
  process.exit(exitCode);
}

async function main() {
  try {
    const args = parseArgs(process.argv.slice(2));
    const url = buildUrl(args.baseUrl, args);
    const headers = await getHeadersForBaseUrl(args.baseUrl, args);
    const result = await runBenchmark('current', url, headers, args.method, args.body, args.runs, args.warmup);
    printReport(result);
  } catch (error) {
    console.error(error.message);
    console.error('Run with --help for usage.');
    process.exit(1);
  }
}

main();
