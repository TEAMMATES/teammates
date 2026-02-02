import { NgModule } from '@angular/core';

/**
 * This package is ESM only since v14.3, which causes issues when importing this module.
 * As a workaround, we mock the module here until we can update the testing framework to support ESM properly.
 */
export class HotTableRegisterer {}

export const HotTableModule = NgModule({})(class HotTableModule {});
