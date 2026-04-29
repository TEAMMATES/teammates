import { Component, Input, NgModule } from '@angular/core';

/**
 * This package is ESM only since v14.3, which causes issues when importing this module.
 * As a workaround, we mock the module here until we can update the testing framework to support ESM properly.
 */

export const NON_COMMERCIAL_LICENSE = 'non-commercial-and-evaluation';

@Component({
  // eslint-disable-next-line @angular-eslint/component-selector
  selector: 'hot-table',
  template: '',
  standalone: true,
})
export class HotTableComponent {
  @Input() settings: any;
  @Input() data: any;
  hotInstance = null;
}

export const HotTableModule = NgModule({ imports: [HotTableComponent], exports: [HotTableComponent] })(
  class HotTableModule {},
);
