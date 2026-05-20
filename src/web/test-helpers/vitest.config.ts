import { defineConfig } from 'vitest/config';

export default defineConfig({
  test: {
    isolate: true,
    snapshotSerializers: [
      'jest-preset-angular/build/serializers/html-comment',
      'jest-preset-angular/build/serializers/ng-snapshot',
      'jest-preset-angular/build/serializers/no-ng-attributes',
    ],
  },
});
