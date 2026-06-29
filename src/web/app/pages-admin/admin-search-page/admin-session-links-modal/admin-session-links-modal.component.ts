import { NgClass } from '@angular/common';
import { ChangeDetectionStrategy, Component, Input, OnInit, computed, inject, signal } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap/modal';
import { AjaxLoadingComponent } from '../../../components/ajax-loading/ajax-loading.component';
import { SubmissionStatusNamePipe } from '../../../components/teammates-common/submission-status-name.pipe';
import { StatusMessageService } from '../../../../services/status-message.service';
import { TimezoneService } from '../../../../services/timezone.service';
import { UserService } from '../../../../services/user.service';
import {
  FeedbackSessionSubmissionStatus,
  SessionLinks,
  SessionResultLink,
  SessionSubmissionLink,
} from '../../../../types/api-output';
import { ErrorMessageOutput } from '../../../error-message-output';

/**
 * Displays lazily-loaded session links for an admin user search result.
 */
@Component({
  selector: 'tm-admin-session-links-modal',
  templateUrl: './admin-session-links-modal.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [NgClass, AjaxLoadingComponent, SubmissionStatusNamePipe],
})
export class AdminSessionLinksModalComponent implements OnInit {
  private readonly userService = inject(UserService);
  private readonly statusMessageService = inject(StatusMessageService);
  private readonly timezoneService = inject(TimezoneService);

  readonly activeModal = inject(NgbActiveModal);

  @Input()
  userId = '';

  @Input()
  userName = '';

  readonly sessionLinks = signal<SessionLinks | null>(null);
  readonly isLoading = signal(true);
  readonly submissionLinks = computed(() => this.sessionLinks()?.submissionLinks ?? []);
  readonly resultsLinks = computed(() => this.sessionLinks()?.resultsLinks ?? []);

  private readonly submissionStatusClasses: Record<FeedbackSessionSubmissionStatus, string> = {
    [FeedbackSessionSubmissionStatus.OPEN]: 'bg-success',
    [FeedbackSessionSubmissionStatus.GRACE_PERIOD]: 'bg-success',
    [FeedbackSessionSubmissionStatus.CLOSED]: 'bg-dark',
    [FeedbackSessionSubmissionStatus.NOT_VISIBLE]: 'bg-secondary',
  };

  private readonly submissionStatusOrder: Record<FeedbackSessionSubmissionStatus, number> = {
    [FeedbackSessionSubmissionStatus.OPEN]: 0,
    [FeedbackSessionSubmissionStatus.GRACE_PERIOD]: 0,
    [FeedbackSessionSubmissionStatus.NOT_VISIBLE]: 1,
    [FeedbackSessionSubmissionStatus.CLOSED]: 2,
  };

  ngOnInit(): void {
    this.userService.getSessionLinks(this.userId).subscribe({
      next: (sessionLinks: SessionLinks) => {
        this.sessionLinks.set(this.sortSessionLinks(sessionLinks));
        this.isLoading.set(false);
      },
      error: (resp: ErrorMessageOutput) => {
        this.isLoading.set(false);
        this.statusMessageService.showErrorToast(resp.error.message);
        this.activeModal.dismiss(resp.error.message);
      },
    });
  }

  async copyLink(url: string): Promise<void> {
    try {
      await navigator.clipboard.writeText(url);
      this.statusMessageService.showSuccessToast('Link copied.');
    } catch {
      this.statusMessageService.showErrorToast('Unable to copy link.');
    }
  }

  formatTimeRange(link: SessionSubmissionLink | SessionResultLink): string {
    const dateFormatWithZoneInfo = 'ddd, DD MMM YYYY, hh:mm A Z';
    const startTime = this.timezoneService.formatToString(
      link.submissionStartTimestamp,
      link.timeZone,
      dateFormatWithZoneInfo,
    );
    const endTime = this.timezoneService.formatToString(
      link.submissionEndTimestamp,
      link.timeZone,
      dateFormatWithZoneInfo,
    );

    return `${startTime} - ${endTime}`;
  }

  getSubmissionStatusClasses(status: FeedbackSessionSubmissionStatus): string {
    return this.submissionStatusClasses[status];
  }

  private sortSessionLinks(sessionLinks: SessionLinks): SessionLinks {
    return {
      ...sessionLinks,
      submissionLinks: [...sessionLinks.submissionLinks].sort(
        (firstLink: SessionSubmissionLink, secondLink: SessionSubmissionLink) =>
          this.compareSubmissionLinks(firstLink, secondLink),
      ),
      resultsLinks: [...sessionLinks.resultsLinks].sort((firstLink: SessionResultLink, secondLink: SessionResultLink) =>
        this.compareResultLinks(firstLink, secondLink),
      ),
    };
  }

  private compareSubmissionLinks(firstLink: SessionSubmissionLink, secondLink: SessionSubmissionLink): number {
    const statusDifference =
      this.submissionStatusOrder[firstLink.submissionStatus] - this.submissionStatusOrder[secondLink.submissionStatus];
    if (statusDifference !== 0) {
      return statusDifference;
    }

    if (firstLink.submissionStatus === FeedbackSessionSubmissionStatus.CLOSED) {
      return this.compareByEndTimeDescending(firstLink, secondLink);
    }

    if (this.isCurrentlySubmittable(firstLink.submissionStatus)) {
      return this.compareByEndTimeAscending(firstLink, secondLink);
    }

    return this.compareByStartTimeAscending(firstLink, secondLink);
  }

  private compareResultLinks(firstLink: SessionResultLink, secondLink: SessionResultLink): number {
    return this.compareByEndTimeDescending(firstLink, secondLink);
  }

  private isCurrentlySubmittable(status: FeedbackSessionSubmissionStatus): boolean {
    return status === FeedbackSessionSubmissionStatus.OPEN || status === FeedbackSessionSubmissionStatus.GRACE_PERIOD;
  }

  private compareByStartTimeAscending(
    firstLink: SessionSubmissionLink | SessionResultLink,
    secondLink: SessionSubmissionLink | SessionResultLink,
  ): number {
    return this.compareAscending(firstLink.submissionStartTimestamp, secondLink.submissionStartTimestamp);
  }

  private compareByEndTimeAscending(
    firstLink: SessionSubmissionLink | SessionResultLink,
    secondLink: SessionSubmissionLink | SessionResultLink,
  ): number {
    return this.compareAscending(firstLink.submissionEndTimestamp, secondLink.submissionEndTimestamp);
  }

  private compareByEndTimeDescending(
    firstLink: SessionSubmissionLink | SessionResultLink,
    secondLink: SessionSubmissionLink | SessionResultLink,
  ): number {
    return this.compareDescending(firstLink.submissionEndTimestamp, secondLink.submissionEndTimestamp);
  }

  private compareAscending(firstValue: number, secondValue: number): number {
    return firstValue - secondValue;
  }

  private compareDescending(firstValue: number, secondValue: number): number {
    return secondValue - firstValue;
  }
}
