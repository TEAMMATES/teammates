import { Component, OnInit, inject } from '@angular/core';
import { ActivatedRoute, Params } from '@angular/router';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap/modal';
import { ErrorReportComponent } from './components/error-report/error-report.component';
import { SimpleModalType } from './components/simple-modal/simple-modal-type';
import { ErrorMessageOutput } from './error-message-output';
import { AuthService } from '../services/auth.service';
import { CourseService } from '../services/course.service';
import { NavigationService } from '../services/navigation.service';
import { SimpleModalService } from '../services/simple-modal.service';
import { AuthInfo, CourseJoinKeyAccess, CourseJoinKeyAccessDecision } from '../types/api-output';
import { LoadingSpinnerDirective } from './components/loading-spinner/loading-spinner.directive';

/**
 * User join page component.
 */
@Component({
  selector: 'tm-user-join-page',
  templateUrl: './user-join-page.component.html',
  imports: [LoadingSpinnerDirective],
})
export class UserJoinPageComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly courseService = inject(CourseService);
  private readonly navigationService = inject(NavigationService);
  private readonly authService = inject(AuthService);
  private readonly simpleModalService = inject(SimpleModalService);
  private readonly ngbModal = inject(NgbModal);

  isLoading = true;
  hasJoined = false;
  validUrl = true;
  entityType = '';
  key = '';
  accountEmail = '';

  ngOnInit(): void {
    this.route.queryParams.subscribe((queryParams: Params) => {
      this.entityType = queryParams['entityType'];
      this.key = queryParams['key'];

      const nextUrl = `${globalThis.location.pathname}${globalThis.location.search.replaceAll('&', '%26')}`;
      this.authService.getAuthUser(nextUrl).subscribe((auth: AuthInfo) => {
        if (!auth.user) {
          this.isLoading = false;
          this.navigationService.navigateByURL(`/web/login?nextUrl=${encodeURIComponent(nextUrl)}`);
          return;
        }
        this.accountEmail = auth.user.accountEmail;

        this.courseService.getCourseJoinKeyValidity({ key: this.key }).subscribe({
          next: (resp: CourseJoinKeyAccess) => {
            switch (resp.decision) {
              case CourseJoinKeyAccessDecision.ALREADY_JOINED:
              case CourseJoinKeyAccessDecision.SIGN_IN_REQUIRED:
                this.navigationService.navigateByURL(`/web/login?nextUrl=${encodeURIComponent(nextUrl)}`);
                break;
              case CourseJoinKeyAccessDecision.VALID:
                this.hasJoined = false;
                this.isLoading = false;
                break;
              default:
                this.validUrl = false;
                this.isLoading = false;
            }
          },
          error: (resp: ErrorMessageOutput) => {
            const modalRef: NgbModalRef = this.ngbModal.open(ErrorReportComponent);
            modalRef.componentInstance.requestId = resp.headers?.get('X-Request-Id');
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
    this.courseService.joinCourse({ key: this.key }).subscribe({
      next: () => {
        this.authService.clearAuthCache();
        this.navigationService.navigateByURL(`/web/${this.entityType}`);
      },
      error: (resp: ErrorMessageOutput) => {
        const errorMessage = resp.error.message;

        if (resp.status >= 500) {
          const modalRef = this.ngbModal.open(ErrorReportComponent);
          modalRef.componentInstance.requestId = resp.headers?.get('X-Request-Id');
          modalRef.componentInstance.errorMessage = errorMessage;
        } else {
          this.simpleModalService.openInformationModal('ERROR', SimpleModalType.DANGER, errorMessage);
        }
      },
    });
  }
}
