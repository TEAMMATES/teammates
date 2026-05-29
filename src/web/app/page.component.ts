import { Location, NgStyle, AsyncPipe } from '@angular/common';
import {
  Component,
  Directive,
  ElementRef,
  EventEmitter,
  HostListener,
  Input,
  OnInit,
  Output,
  forwardRef,
  inject,
} from '@angular/core';
import { Title } from '@angular/platform-browser';
import { ActivatedRoute, NavigationEnd, Router, RouterOutlet } from '@angular/router';
import { NgbDropdown, NgbDropdownToggle, NgbDropdownMenu } from '@ng-bootstrap/ng-bootstrap/dropdown';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap/modal';
import { fromEvent, merge, Observable, of } from 'rxjs';
import { map } from 'rxjs/operators';
import uaParser from 'ua-parser-js';
import { environment } from '../environments/environment';
import { StatusMessageService } from '../services/status-message.service';
import { AuthInfo, NotificationTargetUser } from '../types/api-output';
import { LoaderBarComponent } from './components/loader-bar/loader-bar.component';
import { LoadingSpinnerDirective } from './components/loading-spinner/loading-spinner.directive';
import { NotificationBannerComponent } from './components/notification-banner/notification-banner.component';
import { TeammatesRouterDirective } from './components/teammates-router/teammates-router.directive';
import { Toast } from './components/toast/toast';
import { ToastComponent } from './components/toast/toast.component';
import { AuthService } from '../services/auth.service';

const DEFAULT_TITLE = 'TEAMMATES - Online Peer Feedback/Evaluation System for Student Team Projects';

/**
 * Directive to emit an event if a click occurred outside the element.
 */
@Directive({ selector: '[tmClickOutside]' })
export class ClickOutsideDirective {
  private elementRef = inject(ElementRef);

  @Output() tmClickOutside: EventEmitter<MouseEvent> = new EventEmitter<MouseEvent>();

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

/**
 * Base skeleton for all pages.
 */
@Component({
  selector: 'tm-page',
  templateUrl: './page.component.html',
  styleUrls: ['./page.component.scss'],
  imports: [
    forwardRef(() => ClickOutsideDirective),
    TeammatesRouterDirective,
    NgStyle,
    NgbDropdown,
    NgbDropdownToggle,
    NgbDropdownMenu,
    LoaderBarComponent,
    ToastComponent,
    NotificationBannerComponent,
    LoadingSpinnerDirective,
    RouterOutlet,
    AsyncPipe,
  ],
})
export class PageComponent implements OnInit {
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private title = inject(Title);
  private ngbModal = inject(NgbModal);
  private statusMessageService = inject(StatusMessageService);
  private authService = inject(AuthService);

  // enum
  NotificationTargetUser!: typeof NotificationTargetUser;

  isFetchingAuthDetails = false;
  user = '';
  isStudent = false;
  isInstructor = false;
  isAdmin = false;
  isMaintainer = false;
  @Input() isAuthNeeded = true;
  @Input() authInfo: AuthInfo | null = null;
  @Input() notificationTargetUser: NotificationTargetUser = NotificationTargetUser.GENERAL;
  @Input() pageTitle = '';
  @Input() hideAuthInfo = false;
  @Input() navItems: any[] = [];

  isCollapsed = true;
  isUnsupportedBrowser = false;
  isCookieDisabled = false;
  browser = '';
  isNetworkOnline$: Observable<boolean>;
  version: string = environment.version;
  logoutUrl = `${environment.backendUrl}/logout`;
  toast: Toast | null = null;

  /**
   * Minimum versions of browsers supported.
   *
   * Angular browser support: https://angular.io/guide/browser-support
   *
   * Bootstrap 5 browser support: https://getbootstrap.com/docs/5.2/getting-started/browsers-devices/
   */
  minimumVersions: Record<string, number> = {
    Chrome: 87,
    Firefox: 86,
    Safari: 13,
    Edge: 88,
  };

  constructor() {
    const location = inject(Location);

    this.NotificationTargetUser = NotificationTargetUser;
    this.checkBrowserVersion();
    this.router.events.subscribe((val: any) => {
      if (val instanceof NavigationEnd) {
        window.scrollTo(0, 0); // reset viewport
        this.toast = null; // reset toast
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
      fromEvent(window, 'online').pipe(map(() => true)),
      fromEvent(window, 'offline').pipe(map(() => false)),
    );

    // Close open modal(s) when moving backward or forward through history in the browser page
    location.subscribe(() => {
      if (this.ngbModal.hasOpenModals()) {
        this.ngbModal.dismissAll();
      }
    });

    this.statusMessageService.getToastEvent().subscribe((toast: Toast) => {
      this.toast = toast;
    });
  }

  ngOnInit(): void {
    const user = this.authInfo?.user;
    if (user) {
      this.user = user.id;
      if (this.authInfo?.masquerade) {
        this.user += ' (M)';
      }
      this.isStudent = user.isStudent;
      this.isInstructor = user.isInstructor;
      this.isAdmin = user.isAdmin;
      this.isMaintainer = user.isMaintainer;
    } else {
      this.isStudent = false;
      this.isInstructor = false;
      this.isAdmin = false;
      this.isMaintainer = false;
    }
  }

  private checkBrowserVersion(): void {
    const browser: any = uaParser(navigator.userAgent).browser;
    this.browser = `${browser.name} ${browser.version}`;
    this.isUnsupportedBrowser =
      !this.minimumVersions[browser.name] || this.minimumVersions[browser.name] > parseInt(browser.major, 10);
    this.isCookieDisabled = !navigator.cookieEnabled;
  }

  /**
   * Method to toggle the isCollapsed property when an item on the navbar is clicked,
   * when the user is using a mobile device.
   */
  toggleCollapse(): void {
    // Check if the device is a mobile device
    if (window.innerWidth < 992) {
      this.isCollapsed = !this.isCollapsed;
    }
  }

  /**
   * Method that checks if current page has active modals and close them.
   */
  closeModal(): void {
    if (this.ngbModal.hasOpenModals()) {
      this.ngbModal.dismissAll();
    }
  }

  /**
   * Method to get the url of the current route.
   */
  getUrl(): string {
    return this.router.url;
  }

  logout(): void {
    window.location.href = this.logoutUrl;
    this.authService.clearAuthCache();
  }

  get isValidUser(): boolean {
    const hasRole = this.hasRole;
    const isLoggedIn = !!this.user;

    // Logged in but not known to TEAMMATES
    if (isLoggedIn && !hasRole) {
      return false;
    }

    // Public pages
    if (!this.isAuthNeeded) {
      return true;
    }

    return isLoggedIn && hasRole;
  }

  get hasRole(): boolean {
    return this.isStudent || this.isInstructor || this.isAdmin || this.isMaintainer;
  }
}
