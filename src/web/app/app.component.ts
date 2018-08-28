import { Component } from '@angular/core';
import * as uaParser from 'ua-parser-js';

/**
 * Root application page.
 */
@Component({
  selector: 'tm-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
})
export class AppComponent {

  /**
   * Used by ng-bootstrap to determine if the navbar needs to be collapsed.
   */
  isCollapsed: boolean = true;

  /**
   * True if the browser used by user is not supported.
   */
  isUnsupportedBrowser: boolean = false;

  /**
   * True if the browser used by user does not enable cookies.
   */
  isCookieDisabled: boolean = false;

  /**
   * Browser used by user.
   */
  browser: string = '';

  /**
   * Minimum versions of browsers supported.
   *
   * Angular browser support: https://angular.io/guide/browser-support
   *
   * Bootstrap 4 browser support: https://getbootstrap.com/docs/4.0/getting-started/browsers-devices/
   */
  minimumVersions: any = {
    Chrome: 45,
    IE: 10,
    Firefox: 40,
    Safari: 7,
    // Opera: ??
  };

  constructor() {
    this.checkBrowserVersion();
  }

  private checkBrowserVersion(): void {
    const browser: any = uaParser(navigator.userAgent).browser;
    this.browser = `${browser.name} ${browser.version}`;
    this.isUnsupportedBrowser = !this.minimumVersions[browser.name]
        || this.minimumVersions[browser.name] > parseInt(browser.major, 10);
    this.isCookieDisabled = !navigator.cookieEnabled;
  }

}
