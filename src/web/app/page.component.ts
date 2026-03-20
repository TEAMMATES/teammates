import { Location, NgStyle, AsyncPipe } from '@angular/common';
import { Component, Directive, ElementRef, EventEmitter, HostListener, Input, Output, TemplateRef, ViewChild, forwardRef } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { ActivatedRoute, NavigationEnd, Router, RouterOutlet } from '@angular/router';
import { NgbModal, NgbDropdown, NgbDropdownToggle, NgbDropdownMenu, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { fromEvent, merge, Observable, of } from 'rxjs';
import { map } from 'rxjs/operators';
import uaParser from 'ua-parser-js';
import { environment } from '../environments/environment';
import { AuthService } from '../services/auth.service';
import { StatusMessageService } from '../services/status-message.service';
import { NotificationTargetUser } from '../types/api-output';
import { LoaderBarComponent } from './components/loader-bar/loader-bar.component';
import { LoadingSpinnerDirective } from './components/loading-spinner/loading-spinner.directive';
import { NotificationBannerComponent } from './components/notification-banner/notification-banner.component';
import { TeammatesRouterDirective } from './components/teammates-router/teammates-router.directive';
import { Toast } from './components/toast/toast';
import { ToastComponent } from './components/toast/toast.component';

const DEFAULT_TITLE: string = 'TEAMMATES - Online Peer Feedback/Evaluation System for Student Team Projects';

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
export class PageComponent {

  // enum
  NotificationTargetUser: typeof NotificationTargetUser = NotificationTargetUser;

  @Input() isFetchingAuthDetails: boolean = false;
  @Input() studentLoginUrl: string = '';
  @Input() instructorLoginUrl: string = '';
  @Input() user: string = '';
  @Input() isStudent: boolean = false;
  @Input() isInstructor: boolean = false;
  @Input() isAdmin: boolean = false;
  @Input() isMaintainer: boolean = false;
  @Input() isValidUser: boolean = false;
  @Input() notificationTargetUser: NotificationTargetUser = NotificationTargetUser.GENERAL;
  @Input() pageTitle: string = '';
  @Input() hideAuthInfo: boolean = false;
  @Input() navItems: any[] = [];

  @ViewChild('providerModal') providerModal!: TemplateRef<any>;

  isCollapsed: boolean = true;
  isUnsupportedBrowser: boolean = false;
  isCookieDisabled: boolean = false;
  browser: string = '';
  isNetworkOnline$: Observable<boolean>;
  version: string = environment.version;
  logoutUrl: string = `${environment.backendUrl}/logout`;
  toast: Toast | null = null;

  private currentRole: 'student' | 'instructor' | null = null;
  private providerModalRef: NgbModalRef | null = null;
  private backendUrl: string = environment.backendUrl;

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

  constructor(private router: Router, private route: ActivatedRoute, private title: Title,
              private ngbModal: NgbModal, location: Location,
              private statusMessageService: StatusMessageService, private authService: AuthService) {
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

  private checkBrowserVersion(): void {
    const browser: any = uaParser(navigator.userAgent).browser;
    this.browser = `${browser.name} ${browser.version}`;
    this.isUnsupportedBrowser = !this.minimumVersions[browser.name]
        || this.minimumVersions[browser.name] > parseInt(browser.major, 10);
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
    if (environment.firebaseConfig?.projectId) {
      this.authService.logout().then(() => {
        window.location.href = this.logoutUrl;
      });
    } else {
      window.location.href = this.logoutUrl;
    }
  }

  /**
   * Opens the auth provider selection modal for the given role.
   * @param role - 'student' or 'instructor'
   */
  openProviderModal(role: 'student' | 'instructor'): void {
    this.currentRole = role;
    this.providerModalRef = this.ngbModal.open(this.providerModal, { centered: true });
  }

  /**
   * Logs in with the selected auth provider and role.
   * Constructs the appropriate login URL with provider parameter and redirects.
   * @param provider - 'google' or 'entra'
   */
  loginWithProvider(provider: 'google' | 'entra'): void {
    if (!this.currentRole) {
      this.statusMessageService.showErrorToast('Role not selected');
      return;
    }

    // Construct the next URL based on role
    const nextUrlMap: Record<string, string> = {
      student: '/web/student/home',
      instructor: '/web/instructor/home',
    };
    
    const nextUrl = nextUrlMap[this.currentRole] || '/';

    // Construct login URL with provider parameter
    const loginUrl = `${this.backendUrl}/login?provider=${provider}&nextUrl=${encodeURIComponent(nextUrl)}`;

    // Close modal and redirect
    if (this.providerModalRef) {
      this.providerModalRef.close();
    }
    this.redirectTo(loginUrl);
  }

  redirectTo(url: string): void {
    window.location.assign(url);
  }
}
