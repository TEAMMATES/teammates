import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { finalize } from 'rxjs/operators';
import { ErrorReportComponent } from './components/error-report/error-report.component';
import { SimpleModalType } from './components/simple-modal/simple-modal-type';
import { ErrorMessageOutput } from './error-message-output';
import { environment } from '../environments/environment';
import { AccountService } from '../services/account.service';
import { AuthService } from '../services/auth.service';
import { CourseService } from '../services/course.service';
import { NavigationService } from '../services/navigation.service';
import { SimpleModalService } from '../services/simple-modal.service';
import { TimezoneService } from '../services/timezone.service';
import { AuthInfo, JoinStatus } from '../types/api-output';

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
  userId: string = '';

  private backendUrl: string = environment.backendUrl;

  constructor(private route: ActivatedRoute,
              private accountService: AccountService,
              private courseService: CourseService,
              private navigationService: NavigationService,
              private authService: AuthService,
              private simpleModalService: SimpleModalService,
              private timezoneService: TimezoneService,
              private ngbModal: NgbModal) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe((queryParams: any) => {
      this.entityType = queryParams.entitytype;
      this.key = queryParams.key;
      this.isCreatingAccount = queryParams.iscreatingaccount === 'true';

      // Create account request can only come from instructor.
      if (this.isCreatingAccount) {
        this.entityType = 'instructor';
      }

      const nextUrl: string = `${window.location.pathname}${window.location.search.replace(/&/g, '%26')}`;
      this.authService.getAuthUser(undefined, nextUrl).subscribe((auth: AuthInfo) => {
        if (!auth.user) {
          this.isLoading = false;
          if (this.entityType === 'student') {
            window.location.href = `${this.backendUrl}${auth.studentLoginUrl}`;
          } else if (this.entityType === 'instructor') {
            window.location.href = `${this.backendUrl}${auth.instructorLoginUrl}`;
          }
          return;
        }
        this.userId = auth.user.id;

        this.courseService
          .getJoinCourseStatus(this.key, this.entityType, this.isCreatingAccount)
          .subscribe({
            next: (resp: JoinStatus) => {
              this.hasJoined = resp.hasJoined;
              if (this.hasJoined) {
                // The regkey has been used; simply redirect the user to their home page,
                // regardless of whether the regkey matches or not.
                this.navigationService.navigateByURL(`/web/${this.entityType}/home`);
              } else {
                this.isLoading = false;
              }
            },
            error: (resp: ErrorMessageOutput) => {
              if (resp.status === 404) {
                this.validUrl = false;
                this.isLoading = false;
                return;
              }
              const modalRef: any = this.ngbModal.open(ErrorReportComponent);
              modalRef.componentInstance.requestId = resp.error.requestId;
              modalRef.componentInstance.errorMessage = resp.error.message;
            },
          });
      });
    });
  }

  /**
   * Joins the course.
   */
  joinCourse(): void {
    this.courseService.joinCourse(this.key, this.entityType).subscribe({
      next: () => {
        this.navigationService.navigateByURL(`/web/${this.entityType}`);
      },
      error: (resp: ErrorMessageOutput) => {
        const errorMessage = resp.error.message;

        if (resp.status >= 500) {
          const modalRef: any = this.ngbModal.open(ErrorReportComponent);
          modalRef.componentInstance.requestId = resp.error.requestId;
          modalRef.componentInstance.errorMessage = errorMessage;
        } else {
          this.simpleModalService.openInformationModal('ERROR',
              SimpleModalType.DANGER, errorMessage);
        }
      },
    });
  }

  /**
   * Creates an account.
   * Account is only created after instructor joins for the first time.
   */
  createAccount(): void {
    this.isLoading = true;
    this.accountService
      .createAccount(this.key, this.timezoneService.guessTimezone())
      .pipe(finalize(() => {
        this.isLoading = false;
      }))
      .subscribe({
        next: () => {
          this.navigationService.navigateByURL('/web/instructor');
        },
        error: (resp: ErrorMessageOutput) => {
          if (resp.status === 404) {
            this.validUrl = false;
          } else {
            const modalRef: any = this.ngbModal.open(ErrorReportComponent);
            modalRef.componentInstance.requestId = resp.error.requestId;
            modalRef.componentInstance.errorMessage = resp.error.message;
          }
        },
      });
  }

}
