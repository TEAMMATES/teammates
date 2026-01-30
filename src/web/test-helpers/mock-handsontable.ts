const { NgModule } = require('@angular/core');

/**
 * This package is ESM only since v14.3, which causes issues when importing this module.
 * As a workaround, we mock the module here until we can update the testing framework to support ESM properly.
 */
class HotTableRegisterer {}

const HotTableModule = NgModule({})(class HotTableModule {});

module.exports = {
  HotTableRegisterer,
  HotTableModule,
};
