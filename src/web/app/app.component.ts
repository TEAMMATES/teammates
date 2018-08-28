import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { NavigationService } from './navigation.service';
import * as uaParser from 'ua-parser-js';

/**
 * Root application page.
 */
@Component({
  selector: 'tm-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
})
export class AppComponent implements OnInit {

  /**
   * Used by ng-bootstrap to determine if the navbar needs to be collapsed.
   */
  public isCollapsed: boolean = true;

  /**
   * True if the browser used by user is not supported.
   */
  public isUnsupportedBrowser: boolean;

  /**
   * Browser used by user.
   */
  public browser: string;

  /**
   * Minimum versions of browsers supported.
   *
   * Angular browser support: https://angular.io/guide/browser-support
   *
   * Bootstrap 4 browser support: https://getbootstrap.com/docs/4.0/getting-started/browsers-devices/
   */
  public minimumVersions: any = {
    Chrome: 45,
    IE: 10,
    Firefox: 40,
    Safari: 7,
    // Opera: ??
  };

  constructor(private router: Router, private navigationService: NavigationService) {}

  private checkBrowserVersion(): void {
    const browser: any = uaParser(navigator.userAgent).browser;
    this.browser = `${browser.name} ${browser.version}`;
    this.isUnsupportedBrowser = !this.minimumVersions[browser.name]
        || this.minimumVersions[browser.name] > parseInt(browser.major, 10);
  }

  /**
   * Navigates user to another page.
   */
  public navigateTo(url: string, event: any): void {
    this.navigationService.navigateTo(this.router, url, event);
  }

  public ngOnInit(): void {
    this.checkBrowserVersion();
  }

}
