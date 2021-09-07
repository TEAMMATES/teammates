import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { finalize } from 'rxjs/operators';
import { environment } from '../environments/environment';
import { AccountService } from '../services/account.service';
import { AuthService } from '../services/auth.service';
import { CourseService } from '../services/course.service';
import { NavigationService } from '../services/navigation.service';
import { AuthInfo, JoinStatus, MessageOutput } from '../types/api-output';
import { ErrorReportComponent } from './components/error-report/error-report.component';
import { ErrorMessageOutput } from './error-message-output';

/**
 * User join page component.
 */
@Component({
  selector: 'tm-user-join-page',
  templateUrl: './user-join-page.component.html',
  styleUrls: ['./user-join-page.component.scss'],
})
export class UserJoinPageComponent implements OnInit {

  isLoading: boolean = true;
  isCreatingAccount: boolean = false;
  hasJoined: boolean = false;
  validUrl: boolean = true;
  entityType: string = '';
  key: string = '';
  institute: string = '';
  mac: string = '';
  userId: string = '';

  private backendUrl: string = environment.backendUrl;

  constructor(private route: ActivatedRoute,
              private router: Router,
              private accountService: AccountService,
              private courseService: CourseService,
              private navigationService: NavigationService,
              private authService: AuthService,
              private ngbModal: NgbModal) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe((queryParams: any) => {
      this.entityType = queryParams.entitytype;
      this.key = queryParams.key;
      this.institute = queryParams.instructorinstitution;
      this.mac = queryParams.mac;
      this.isCreatingAccount = queryParams.iscreatingaccount === 'true';

      if (this.key == null || (this.institute != null && this.mac == null)) {
        this.validUrl = false;
        return;
      }

      if (this.isCreatingAccount) {
        this.setupForCreateAccount();
      } else {
        this.setupForJoinCourse();
      }
    });
  }

  /**
   * Setup page for instructor to join course.
   */
  setupForJoinCourse(): void {
    this.courseService.getJoinCourseStatus(this.key, this.entityType).subscribe((resp: JoinStatus) => {
      this.hasJoined = resp.hasJoined;
      this.userId = resp.userId || '';
      if (this.hasJoined && this.userId) {
        // The regkey has been used and there is a logged in user.
        // Simply redirect the user to their home page, regardless of whether the regkey matches or not.
        this.navigationService.navigateByURL(this.router, `/web/${this.entityType}/home`);
      } else {
        this.isLoading = false;
      }
    }, (resp: ErrorMessageOutput) => {
      if (resp.status === 403) {
        this.isLoading = false;
        const nextUrl: string = `${window.location.pathname}${window.location.search.replace(/&/g, '%26')}`;
        this.authService.getAuthUser(undefined, nextUrl).subscribe((auth: AuthInfo) => {
          if (!auth.user) {
            window.location.href = `${this.backendUrl}${auth.studentLoginUrl}`;
          }
        });
      } else {
        const modalRef: any = this.ngbModal.open(ErrorReportComponent);
        modalRef.componentInstance.requestId = resp.error.requestId;
        modalRef.componentInstance.errorMessage = resp.error.message;
      }
    });
  }

  /**
   * Setup page for instructor to create account.
   */
  setupForCreateAccount(): void {
    const nextUrl: string = `${window.location.pathname}${window.location.search.replace(/&/g, '%26')}`;
    this.authService.getAuthUser(undefined, nextUrl).subscribe((resp: AuthInfo) => {
      this.userId = resp.user?.id || '';

      if (!resp.user) {
        window.location.href = `${this.backendUrl}${resp.instructorLoginUrl}`;
      }

      if (resp.user?.isInstructor) {
        // User already has instructor account
        this.navigationService.navigateByURL(this.router, '/web/instructor');
      }

      this.isLoading = false;
    });
  }

  /**
   * Joins the course.
   */
  joinCourse(): void {

    this.courseService.joinCourse(this.key, this.entityType, this.institute, this.mac).subscribe(() => {
      this.navigationService.navigateByURL(this.router, `/web/${this.entityType}`);
    }, (resp: ErrorMessageOutput) => {
      const modalRef: any = this.ngbModal.open(ErrorReportComponent);
      modalRef.componentInstance.requestId = resp.error.requestId;
      modalRef.componentInstance.errorMessage = resp.error.message;
    });
  }

  /**
   * Creates an account.
   */
  createAccount(): void {
    this.isLoading = true;
    this.accountService
      .createAccount(this.key)
      .pipe(finalize(() => (this.isLoading = false)))
      .subscribe(
        (_resp: MessageOutput) => {
          this.navigationService.navigateByURL(this.router, '/web/instructor');
        },
        (resp: ErrorMessageOutput) => {
          if (resp.status === 404) {
            this.validUrl = false;
          } else {
            const modalRef: any = this.ngbModal.open(ErrorReportComponent);
            modalRef.componentInstance.requestId = resp.error.requestId;
            modalRef.componentInstance.errorMessage = resp.error.message;
          }
        },
      );
  }

}
