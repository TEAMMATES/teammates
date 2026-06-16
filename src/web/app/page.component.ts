import { Location, NgStyle } from '@angular/common';
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
  signal,
} from '@angular/core';
import { Title } from '@angular/platform-browser';
import { ActivatedRoute, NavigationEnd, Router, RouterOutlet, Data } from '@angular/router';
import { NgbDropdown, NgbDropdownToggle, NgbDropdownMenu } from '@ng-bootstrap/ng-bootstrap/dropdown';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap/modal';
import { environment } from '../environments/environment';
import { StatusMessageService } from '../services/status-message.service';
import { AuthInfo, NotificationTargetUser } from '../types/api-output';
import { LoaderBarComponent } from './components/loader-bar/loader-bar.component';
import { LoadingSpinnerDirective } from './components/loading-spinner/loading-spinner.directive';
import { NotificationBannerComponent } from './components/notification-banner/notification-banner.component';
import { TeammatesRouterDirective } from './components/teammates-router/teammates-router.directive';
import { Toast } from './components/toast/toast';
import { ToastComponent } from './components/toast/toast.component';
import { NavItem } from './page.model';
import { AuthService } from '../services/auth.service';
import { finalize } from 'rxjs/operators';

const DEFAULT_TITLE = 'TEAMMATES - Online Peer Feedback/Evaluation System for Student Team Projects';

/**
 * Directive to emit an event if a click occurred outside the element.
 */
@Directive({ selector: '[tmClickOutside]' })
export class ClickOutsideDirective {
  private readonly elementRef = inject(ElementRef);

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
  ],
})
export class PageComponent implements OnInit {
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);
  private readonly title = inject(Title);
  private readonly ngbModal = inject(NgbModal);
  private readonly statusMessageService = inject(StatusMessageService);
  private readonly authService = inject(AuthService);

  // enum
  NotificationTargetUser!: typeof NotificationTargetUser;

  readonly isFetchingAuthDetails = signal(false);
  readonly user = signal('');
  readonly isStudent = signal(false);
  readonly isInstructor = signal(false);
  readonly isAdmin = signal(false);
  readonly isMaintainer = signal(false);
  @Input() notificationTargetUser: NotificationTargetUser = NotificationTargetUser.GENERAL;
  @Input() pageTitle = '';
  @Input() navItems: NavItem[] = [];

  readonly isNetworkOnline = signal(navigator.onLine);
  readonly isCookieEnabled = signal(navigator.cookieEnabled);

  isCollapsed = true;
  version: string = environment.version;
  logoutUrl = `${environment.backendUrl}/logout`;
  toast: Toast | null = null;

  constructor() {
    const location = inject(Location);

    this.NotificationTargetUser = NotificationTargetUser;
    this.router.events.subscribe((val) => {
      if (val instanceof NavigationEnd) {
        window.scrollTo(0, 0); // reset viewport
        this.toast = null; // reset toast
        let r: ActivatedRoute = this.route;
        while (r.firstChild) {
          r = r.firstChild;
        }
        r.data.subscribe((resp: Data) => {
          this.pageTitle = resp['pageTitle'];
          this.title.setTitle(resp['htmlTitle'] ?? DEFAULT_TITLE);
        });
      }
    });
    if (environment.frontendUrl) {
      this.logoutUrl += `?frontendUrl=${environment.frontendUrl}`;
    }

    globalThis.addEventListener('online', () => this.isNetworkOnline.set(true));
    globalThis.addEventListener('offline', () => this.isNetworkOnline.set(false));

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
    this.isFetchingAuthDetails.set(true);
    this.authService
      .getAuthUser(this.router.url)
      .pipe(
        finalize(() => {
          this.isFetchingAuthDetails.set(false);
        }),
      )
      .subscribe({
        next: (authInfo: AuthInfo) => {
          const user = authInfo.user;
          if (user) {
            let accountEmail = user.accountEmail;
            if (authInfo.masquerade) {
              accountEmail += ' (M)';
            }
            this.user.set(accountEmail);
            this.isStudent.set(user.isStudent);
            this.isInstructor.set(user.isInstructor);
            this.isAdmin.set(user.isAdmin);
            this.isMaintainer.set(user.isMaintainer);
          } else {
            this.user.set('');
            this.isStudent.set(false);
            this.isInstructor.set(false);
            this.isAdmin.set(false);
            this.isMaintainer.set(false);
          }
        },
        error: () => {
          // Do nothing, the user will be treated as not logged in.
        },
      });
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
    this.authService.clearAuthCache();
    globalThis.location.href = this.logoutUrl;
  }
}
