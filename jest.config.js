module.exports = {
  preset: 'jest-preset-angular',
  setupFilesAfterEnv: [
    '<rootDir>/jest-setup.ts',
  ],
  testPathIgnorePatterns: [
    '<rootDir>/node_modules/',
    '<rootDir>/dist/',
  ],
  collectCoverageFrom: [
    'src/web/app/**/*.ts',
    '!src/web/app/**/*.module.ts',
    'src/web/environments/**/*.ts',
    'src/web/services/**/*.ts',
  ],
  coverageDirectory: './coverage',
  coverageReporters: ['lcov', 'text-summary'],
  moduleNameMapper: {
    d3: '<rootDir>/node_modules/d3/dist/d3.min.js',
    'lodash-es': 'lodash',
    '^@handsontable/angular-wrapper$': '<rootDir>/src/web/test-helpers/mock-handsontable.ts'
  },
  globals: {
    'ts-jest': {
      tsconfig: '<rootDir>/tsconfig.spec.json',
      stringifyContentPathRegex: '\\.(html|svg)$',
    },
  },
};
