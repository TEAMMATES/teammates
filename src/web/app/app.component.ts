import { Component } from '@angular/core';
import { NgbTooltipConfig } from '@ng-bootstrap/ng-bootstrap';

/**
 * Root application page.
 */
@Component({
  selector: 'tm-root',
  template: '<router-outlet></router-outlet>',
})
export class AppComponent {
  constructor(tooltipConfig: NgbTooltipConfig) {
    tooltipConfig.openDelay = 400;
    tooltipConfig.closeDelay = 100;
    tooltipConfig.triggers = 'hover';
  }
}
