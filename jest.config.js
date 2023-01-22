module.exports = {
  collectCoverageFrom: [
    'src/web/app/**/*.ts',
    '!src/web/app/**/*.module.ts',
    'src/web/environments/**/*.ts',
    'src/web/services/**/*.ts',
  ],
  coverageDirectory: './coverage',
  coverageReporters: ['lcov', 'text-summary'],
  setupFiles: [
    './jest-setup.ts',
  ],
  moduleNameMapper: {
    d3: '<rootDir>/node_modules/d3/dist/d3.min.js',
  },
  globalSetup: 'jest-preset-angular/global-setup',
};
