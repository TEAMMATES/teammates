import {
  Component,
  Directive,
  ElementRef,
  EventEmitter,
  HostListener,
  Input,
  Output,
} from '@angular/core';
import { Title } from '@angular/platform-browser';
import { ActivatedRoute, NavigationEnd, Router } from '@angular/router';
import uaParser from 'ua-parser-js';
import { environment } from '../environments/environment';

import { fromEvent, merge, Observable, of } from 'rxjs';
import { mapTo } from 'rxjs/operators';

const DEFAULT_TITLE: string = 'TEAMMATES - Online Peer Feedback/Evaluation System for Student Team Projects';

/**
 * Base skeleton for all pages.
 */
@Component({
  selector: 'tm-page',
  templateUrl: './page.component.html',
  styleUrls: ['./page.component.scss'],
})
export class PageComponent {

  @Input() isFetchingAuthDetails: boolean = false;
  @Input() studentLoginUrl: string = '';
  @Input() instructorLoginUrl: string = '';
  @Input() user: string = '';
  @Input() isStudent: boolean = false;
  @Input() isInstructor: boolean = false;
  @Input() isAdmin: boolean = false;
  @Input() isValidUser: boolean = false;
  @Input() pageTitle: string = '';
  @Input() hideAuthInfo: boolean = false;
  @Input() navItems: any[] = [];
  @Input() institute: string = '';

  isCollapsed: boolean = true;
  isUnsupportedBrowser: boolean = false;
  isCookieDisabled: boolean = false;
  browser: string = '';
  isNetworkOnline$: Observable<boolean>;
  version: string = environment.version;
  logoutUrl: string = `${environment.backendUrl}/logout`;

  /**
   * Minimum versions of browsers supported.
   *
   * Angular browser support: https://angular.io/guide/browser-support
   *
   * Bootstrap 4 browser support: https://getbootstrap.com/docs/4.0/getting-started/browsers-devices/
   */
  minimumVersions: { [key: string]: number } = {
    Chrome: 45,
    IE: 10,
    Firefox: 40,
    Safari: 7,
    // Opera: ??
  };

  constructor(private router: Router, private route: ActivatedRoute, private title: Title) {
    this.checkBrowserVersion();
    this.router.events.subscribe((val: any) => {
      if (val instanceof NavigationEnd) {
        window.scrollTo(0, 0); // reset viewport
        let r: ActivatedRoute = this.route;
        while (r.firstChild) {
          r = r.firstChild;
        }
        r.data.subscribe((resp: any) => {
          this.pageTitle = resp.pageTitle;
          this.title.setTitle(resp.htmlTitle || DEFAULT_TITLE);
        });
      }
    });
    if (environment.frontendUrl) {
      this.logoutUrl += `?frontendUrl=${environment.frontendUrl}`;
    }

    this.isNetworkOnline$ = merge(
        of(navigator.onLine),
        fromEvent(window, 'online').pipe(mapTo(true)),
        fromEvent(window, 'offline').pipe(mapTo(false)),
    );
  }

  private checkBrowserVersion(): void {
    const browser: any = uaParser(navigator.userAgent).browser;
    this.browser = `${browser.name} ${browser.version}`;
    this.isUnsupportedBrowser = !this.minimumVersions[browser.name]
        || this.minimumVersions[browser.name] > parseInt(browser.major, 10);
    this.isCookieDisabled = !navigator.cookieEnabled;
  }
}

/**
 * Directive to emit an event if a click occurred outside the element.
 */
@Directive({ selector: '[tmClickOutside]' })
export class ClickOutsideDirective {
  @Output() tmClickOutside: EventEmitter<MouseEvent> = new EventEmitter<MouseEvent>();

  constructor(private elementRef: ElementRef) {}

  /**
   * Method to execute when any part of the document is clicked.
   */
  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent): void {
    const targetElement: HTMLElement = event.target as HTMLElement;

    // Check if the click was outside the element
    if (targetElement && !this.elementRef.nativeElement.contains(targetElement)) {
      this.tmClickOutside.emit(event);
    }
  }
}
