import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { AccountService } from '../../services/account.service';
import { CourseService } from '../../services/course.service';
import { NavigationService } from '../../services/navigation.service';
import { TimezoneService } from '../../services/timezone.service';
import { AccountRequest, AccountRequestStatus } from '../../types/api-output';
import { ErrorMessageOutput } from '../error-message-output';
import { LoadingSpinnerDirective } from '../components/loading-spinner/loading-spinner.directive';
import { AuthService } from '../../services/auth.service';

/**
 * Instructor welcome page component shown after an account request is approved.
 */
@Component({
  selector: 'tm-instructor-welcome-page',
  templateUrl: './instructor-welcome-page.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [LoadingSpinnerDirective],
})
export class InstructorWelcomePageComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly authService = inject(AuthService);
  private readonly accountService = inject(AccountService);
  private readonly courseService = inject(CourseService);
  private readonly navigationService = inject(NavigationService);
  private readonly timezoneService = inject(TimezoneService);

  readonly isLoading = signal(true);
  readonly accountRequest = signal<AccountRequest | null>(null);
  readonly isInvalidLink = signal(false);
  readonly isCreatingCourse = signal(false);

  ngOnInit(): void {
    const accountRequestId = this.route.snapshot.queryParamMap.get('accountRequestId');

    if (!accountRequestId) {
      this.isInvalidLink.set(true);
      this.isLoading.set(false);
      return;
    }

    this.accountService.getAccountRequest(accountRequestId).subscribe({
      next: (accountRequest: AccountRequest) => {
        if (accountRequest.registeredAt) {
          this.navigationService.navigateByURL('/web/instructor/home');
          return;
        }
        if (accountRequest.status !== AccountRequestStatus.APPROVED) {
          this.isInvalidLink.set(true);
          this.isLoading.set(false);
          return;
        }
        this.accountRequest.set(accountRequest);
        this.isLoading.set(false);
      },
      error: (_resp: ErrorMessageOutput) => {
        this.isInvalidLink.set(true);
        this.isLoading.set(false);
      },
    });
  }

  getStarted(): void {
    const accountRequest = this.accountRequest();
    if (!accountRequest) {
      return;
    }

    this.isCreatingCourse.set(true);
    this.courseService
      .createDemoCourse({
        accountRequestId: accountRequest.accountRequestId,
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
          this.accountRequest.set(null);
        },
      });
  }
}
