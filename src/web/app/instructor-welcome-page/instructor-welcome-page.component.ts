import { ChangeDetectionStrategy, Component, Input, OnInit, inject, signal } from '@angular/core';
import { AccountService } from '../../services/account.service';
import { CourseService } from '../../services/course.service';
import { NavigationService } from '../../services/navigation.service';
import { TimezoneService } from '../../services/timezone.service';
import { AccountVerificationRequest, AccountVerificationRequestStatus } from '../../types/api-output';
import { ErrorMessageOutput } from '../error-message-output';
import { LoadingSpinnerDirective } from '../components/loading-spinner/loading-spinner.directive';
import { AuthService } from '../../services/auth.service';

/**
 * Instructor welcome page component shown after an account verification request is approved.
 */
@Component({
  selector: 'tm-instructor-welcome-page',
  templateUrl: './instructor-welcome-page.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [LoadingSpinnerDirective],
})
export class InstructorWelcomePageComponent implements OnInit {
  private readonly authService = inject(AuthService);
  private readonly accountService = inject(AccountService);
  private readonly courseService = inject(CourseService);
  private readonly navigationService = inject(NavigationService);
  private readonly timezoneService = inject(TimezoneService);

  @Input({ required: true }) accountVerificationRequestId!: string;

  readonly isLoading = signal(true);
  readonly accountVerificationRequest = signal<AccountVerificationRequest | null>(null);
  readonly isInvalidLink = signal(false);
  readonly isCreatingCourse = signal(false);

  ngOnInit(): void {
    const accountVerificationRequestId: string = this.accountVerificationRequestId;

    if (!accountVerificationRequestId) {
      this.isInvalidLink.set(true);
      this.isLoading.set(false);
      return;
    }

    this.accountService.getAccountVerificationRequest(accountVerificationRequestId).subscribe({
      next: (accountVerificationRequest: AccountVerificationRequest) => {
        if (accountVerificationRequest.createdDemoCourseAt) {
          this.navigationService.navigateByURL('/web/instructor/home');
          return;
        }
        if (accountVerificationRequest.status !== AccountVerificationRequestStatus.APPROVED) {
          this.isInvalidLink.set(true);
          this.isLoading.set(false);
          return;
        }
        this.accountVerificationRequest.set(accountVerificationRequest);
        this.isLoading.set(false);
      },
      error: (_resp: ErrorMessageOutput) => {
        this.isInvalidLink.set(true);
        this.isLoading.set(false);
      },
    });
  }

  getStarted(): void {
    const accountVerificationRequest = this.accountVerificationRequest();
    if (!accountVerificationRequest) {
      return;
    }

    this.isCreatingCourse.set(true);
    this.courseService
      .createDemoCourse({
        accountVerificationRequestId: accountVerificationRequest.accountVerificationRequestId,
        timezone: this.timezoneService.guessTimezone(),
      })
      .subscribe({
        next: () => {
          this.authService.clearAuthCache();
          this.navigationService.navigateByURL('/web/instructor/home');
        },
        error: (_resp: ErrorMessageOutput) => {
          this.isCreatingCourse.set(false);
          this.isInvalidLink.set(true);
          this.accountVerificationRequest.set(null);
        },
      });
  }
}
