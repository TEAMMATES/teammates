import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { NavigationService } from './navigation.service';
import * as uaParser from 'ua-parser-js';

@Component({
  selector: 'tm-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
})
export class AppComponent implements OnInit {

  isCollapsed = true;
  isUnsupportedBrowser: boolean;
  browser: string;
  minimumVersions = {
    Chrome: 15,
    IE: 9,
    Firefox: 12,
    Safari: 4,
    // Opera: ??
  };

  checkBrowserVersion() {
    const browser = uaParser(navigator.userAgent).browser;
    this.browser = `${browser.name} ${browser.version}`;
    this.isUnsupportedBrowser = !this.minimumVersions[browser.name]
        || this.minimumVersions[browser.name] > parseInt(browser.major, 10);
  }

  navigateTo(url: string, event: any) {
    this.navigationService.navigateTo(this.router, url, event);
  }

  constructor(private router: Router, private navigationService: NavigationService) {}

  ngOnInit() {
    this.checkBrowserVersion();
  }

}
