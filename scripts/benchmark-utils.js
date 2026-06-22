const path = require('node:path');

const DEFAULT_PORT = 8080;
const DEFAULT_RUNS = 50;
const DEFAULT_WARMUP = 5;
const DEFAULT_OUTPUT_DIR = path.join('build', 'benchmark-results');
const DEFAULT_OUTPUT_FILE = 'result.json';
const DEFAULT_COMPARE_BEFORE = 'before';
const DEFAULT_COMPARE_AFTER = 'after';
const API_PREFIX = '/webapi';
const DEFAULT_ENDPOINT = 'sessions';
const DEFAULT_METHOD = 'GET';
const DEFAULT_ACCOUNT = 'app.admin@gmail.com';
const DEFAULT_BACKDOOR_KEY = 'samplekey';
const DEFAULT_CSRF_KEY = 'samplekey';
const AUTH_COOKIE_NAME = 'AUTH-TOKEN';
const USER_COOKIE_ENDPOINT = '/webapi/cookie';

class BenchmarkUtils {
  static getCompareFiles(argv, index) {
    const before = argv[index + 1];
    const after = argv[index + 2];
    if (!before || before.startsWith('-')) {
      return {
        files: [DEFAULT_COMPARE_BEFORE, DEFAULT_COMPARE_AFTER],
        consumed: 0,
      };
    }
    if (!after || after.startsWith('-')) {
      throw new Error('--compare expects either no names or two names.');
    }
    return {
      files: [before, after],
      consumed: 2,
    };
  }

  static buildLocalBaseUrl(port) {
    return `http://localhost:${port}`;
  }

  static parsePort(value) {
    const parsed = BenchmarkUtils.parsePositiveInteger(value, '--port');
    if (parsed > 65535) {
      throw new Error('--port must be between 1 and 65535.');
    }
    return parsed;
  }

  static parsePositiveInteger(value, flag) {
    const parsed = Number.parseInt(value, 10);
    if (!Number.isInteger(parsed) || parsed <= 0) {
      throw new Error(`${flag} must be a positive integer.`);
    }
    return parsed;
  }

  static parseNonNegativeInteger(value, flag) {
    const parsed = Number.parseInt(value, 10);
    if (!Number.isInteger(parsed) || parsed < 0) {
      throw new Error(`${flag} must be a non-negative integer.`);
    }
    return parsed;
  }

  static parseNameValue(value, flag) {
    if (!value || !value.includes('=')) {
      throw new Error(`${flag} must be in name=value format.`);
    }
    const index = value.indexOf('=');
    return [value.slice(0, index), value.slice(index + 1)];
  }

  static buildUrl(baseUrl, args) {
    const url = new URL(BenchmarkUtils.normalizeEndpoint(args.endpoint), BenchmarkUtils.withTrailingSlash(baseUrl));
    for (const [name, value] of args.params) {
      url.searchParams.set(name, value);
    }
    return url;
  }

  static normalizeEndpoint(endpoint) {
    const path = endpoint.startsWith('/') ? endpoint : `/${endpoint}`;
    if (path === API_PREFIX || path.startsWith(`${API_PREFIX}/`)) {
      return path;
    }
    return `${API_PREFIX}${path}`;
  }

  static withTrailingSlash(url) {
    return url.endsWith('/') ? url : `${url}/`;
  }

  static buildHeaders(args, cookie) {
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

  static summarize(values) {
    const sorted = [...values].sort((a, b) => a - b);
    const total = sorted.reduce((sum, value) => sum + value, 0);
    return {
      min: sorted[0],
      max: sorted[sorted.length - 1],
      mean: total / sorted.length,
      measuredDurationMs: total,
      reqPerSec: (sorted.length * 1000) / total,
    };
  }

  static formatMs(value) {
    return `${value.toFixed(1)} ms`;
  }

  static formatReqPerSec(value) {
    return `${value.toFixed(1)} req/s`;
  }

  static formatCopyLine(result) {
    return [
      `mean_ms=${result.mean.toFixed(1)}`,
      `min_ms=${result.min.toFixed(1)}`,
      `max_ms=${result.max.toFixed(1)}`,
      `req_per_sec=${result.reqPerSec.toFixed(1)}`,
      `bytes=${result.responseBytes}`,
    ].join(' ');
  }

  static formatComparisonMs(before, after, higherIsBetter) {
    return `${BenchmarkUtils.formatMs(before)} -> ${BenchmarkUtils.formatMs(after)} (${BenchmarkUtils.formatChange(
      before,
      after,
      higherIsBetter,
    )})`;
  }

  static formatComparisonReqPerSec(before, after, higherIsBetter) {
    return `${BenchmarkUtils.formatReqPerSec(before)} -> ${BenchmarkUtils.formatReqPerSec(
      after,
    )} (${BenchmarkUtils.formatChange(before, after, higherIsBetter)})`;
  }

  static formatChange(before, after, higherIsBetter) {
    const percent = ((after - before) / before) * 100;
    const improved = higherIsBetter ? percent > 0 : percent < 0;
    const declined = higherIsBetter ? percent < 0 : percent > 0;
    if (!Number.isFinite(percent) || percent === 0) {
      return 'no change';
    }
    const direction = improved ? 'improved' : declined ? 'declined' : 'changed';
    return `${direction} by ${Math.abs(percent).toFixed(1)}%`;
  }

  static roundOneDecimal(value) {
    return Number(value.toFixed(1));
  }

  static resolveResultFile(filePath, outputDir) {
    if (BenchmarkUtils.isExplicitJsonFile(filePath)) {
      return BenchmarkUtils.withJsonExtension(filePath);
    }
    return path.join(outputDir || DEFAULT_OUTPUT_DIR, filePath, DEFAULT_OUTPUT_FILE);
  }

  static isExplicitJsonFile(filePath) {
    return path.isAbsolute(filePath) || path.dirname(filePath) !== '.' || path.extname(filePath) === '.json';
  }

  static withJsonExtension(filePath) {
    return path.extname(filePath) ? filePath : `${filePath}.json`;
  }

  static async getHeadersForBaseUrl(baseUrl, args) {
    const cookie = await BenchmarkUtils.getLocalBackdoorCookie(baseUrl, args);
    return BenchmarkUtils.buildHeaders(args, `${AUTH_COOKIE_NAME}=${cookie}`);
  }

  static async getLocalBackdoorCookie(baseUrl, args) {
    const url = new URL(USER_COOKIE_ENDPOINT, BenchmarkUtils.withTrailingSlash(baseUrl));
    url.searchParams.set('accountemail', args.account);

    const response = await BenchmarkUtils.fetchOrThrow(url, {
      method: 'POST',
      headers: {
        Accept: 'application/json',
        'Backdoor-Key': args.backdoorKey || DEFAULT_BACKDOOR_KEY,
        'CSRF-Key': args.csrfKey || DEFAULT_CSRF_KEY,
      },
    });
    const body = await response.text();
    if (!response.ok) {
      throw new Error(
        `Failed to get local backdoor cookie from ${url}: HTTP ${response.status}: ${body.slice(0, 500)}`,
      );
    }

    const parsedBody = JSON.parse(body);
    if (!parsedBody.message) {
      throw new Error(`Backdoor cookie response from ${url} did not contain a message field.`);
    }
    return parsedBody.message;
  }

  static async fetchOrThrow(url, options) {
    try {
      return await fetch(url, options);
    } catch (error) {
      throw new Error(`Could not connect to ${url}. Is the server running? (${error.message})`);
    }
  }

  static printUsageAndExit(exitCode) {
    console.log(`Usage:
npm run benchmark -- [path] [options]

Options:
  path                       Endpoint path under /webapi. Default: ${DEFAULT_ENDPOINT}
  -p, --port <port>          Local server port. Default: ${DEFAULT_PORT}
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
  -l, --label before|after   Save result to the fixed before or after slot under ${DEFAULT_OUTPUT_DIR}.
  --compare [before] [after] Compare saved benchmark results. Defaults to ${DEFAULT_COMPARE_BEFORE} and ${DEFAULT_COMPARE_AFTER}.
  --entity <value>           Convenience alias for -q entitytype=<value>.

Example workflow:
  1. Save the baseline result
     npm run benchmark -- ${DEFAULT_ENDPOINT} --entity instructor -q isinrecyclebin=false -l before

  2. Save the optimized result
     npm run benchmark -- ${DEFAULT_ENDPOINT} --entity instructor -q isinrecyclebin=false -l after

  3. Compare before vs after
     npm run benchmark -- --compare

Other examples:
  Student sessions
     npm run benchmark -- ${DEFAULT_ENDPOINT} --entity student

  POST with JSON
     npm run benchmark -- ${DEFAULT_ENDPOINT} -X POST -H Content-Type=application/json -d '{"key":"value"}'
`);
    process.exit(exitCode);
  }
}

module.exports = {
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
};
