import { defineConfig } from 'vitest/config';

export default defineConfig({
  test: {
    isolate: true,
    snapshotSerializers: [
      'jest-preset-angular/build/serializers/html-comment',
      'jest-preset-angular/build/serializers/ng-snapshot',
      'jest-preset-angular/build/serializers/no-ng-attributes',
    ],
    coverage: {
      include: [
        'chunk-*.js',
        'spec-*.js',
        'src/web/app/**/*.ts',
        'src/web/environments/**/*.ts',
        'src/web/services/**/*.ts',
      ],
      exclude: ['src/web/**/*.spec.ts', 'src/web/test-helpers/**', 'src/web/services/test-data/**'],
      excludeAfterRemap: true,
      reportsDirectory: './coverage',
      reporter: ['lcov', 'text-summary'],
    },
  },
});
