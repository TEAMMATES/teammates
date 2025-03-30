module.exports = {
  preset: 'jest-preset-angular',
  setupFilesAfterEnv: ['<rootDir>/jest-setup.ts'],
  testEnvironment: 'jsdom',
  globals: {
    'ts-jest': {
      tsconfig: '<rootDir>/tsconfig.spec.json',
      stringifyContentPathRegex: '\\.html$',
      useESM: true,
    },
  },
  moduleFileExtensions: ['ts', 'html', 'js', 'json', 'mjs'],
  moduleNameMapper: {
    '^src/(.*)$': '<rootDir>/src/$1',
    '^app/(.*)$': '<rootDir>/src/app/$1',
    '^assets/(.*)$': '<rootDir>/src/assets/$1',
    '^environments/(.*)$': '<rootDir>/src/environments/$1',
  },
  transformIgnorePatterns: ['node_modules/(?!@angular|rxjs|@ng-bootstrap|zone.js)'],
  transform: {
    '^.+\\.(ts|js|mjs|html|svg)$': ['jest-preset-angular', {
      tsconfig: '<rootDir>/tsconfig.spec.json',
      stringifyContentPathRegex: '\\.html$',
      useESM: true,
    }],
  },
  collectCoverageFrom: [
    'src/web/app/**/*.ts',
    '!src/web/app/**/*.module.ts',
    'src/web/environments/**/*.ts',
    'src/web/services/**/*.ts',
  ],
  coverageDirectory: './coverage',
  coverageReporters: ['lcov', 'text-summary'],
};
