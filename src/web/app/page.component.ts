import { Component, Input } from '@angular/core';
import { Router } from '@angular/router';
import * as uaParser from 'ua-parser-js';
import { NavigationService } from './navigation.service';

/**
 * Base skeleton for all pages.
 */
@Component({
  selector: 'tm-page',
  templateUrl: './page.component.html',
  styleUrls: ['./page.component.scss'],
})
export class PageComponent {

  @Input() studentLoginUrl: string = '';
  @Input() instructorLoginUrl: string = '';
  @Input() isStudent: boolean = false;
  @Input() isInstructor: boolean = false;
  @Input() isAdmin: boolean = false;
  @Input() isValidUser: boolean = false;
  @Input() logoutUrl: string = '';
  @Input() navItems: any[] = [];

  isCollapsed: boolean = true;
  isUnsupportedBrowser: boolean = false;
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

  constructor(private router: Router, private navigationService: NavigationService) {
    this.checkBrowserVersion();
  }

  private checkBrowserVersion(): void {
    const browser: any = uaParser(navigator.userAgent).browser;
    this.browser = `${browser.name} ${browser.version}`;
    this.isUnsupportedBrowser = !this.minimumVersions[browser.name]
        || this.minimumVersions[browser.name] > parseInt(browser.major, 10);
  }

  /**
   * Navigates user to another page.
   */
  navigateTo(url: string, event: any): void {
    this.navigationService.navigateTo(this.router, url, event);
  }

}
