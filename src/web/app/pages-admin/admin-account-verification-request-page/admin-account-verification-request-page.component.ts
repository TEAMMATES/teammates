import { ChangeDetectionStrategy, Component, OnInit, inject, input, signal } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { AccountService } from '../../../services/account.service';
import { DateFormatService } from '../../../services/date-format.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { TimezoneService } from '../../../services/timezone.service';
import { AccountVerificationRequest, AccountVerificationRequestStatus } from '../../../types/api-output';
import { ErrorMessageOutput } from '../../error-message-output';
import { LoadingSpinnerDirective } from '../../components/loading-spinner/loading-spinner.directive';
import {
  AccountVerificationRequestDraft,
  toAccountVerificationRequestUpdateRequest,
} from './account-verification-request-draft';
import { RequestDetailsCardComponent } from './request-details-card/request-details-card.component';

const mockRequestHistory: AccountVerificationRequest[] = [
  {
    accountVerificationRequestId: 'history-approved-request',
    institute: 'Example Graduate School',
    status: AccountVerificationRequestStatus.APPROVED,
    createdAt: Date.now() - 1000 * 60 * 60 * 24 * 48,
    email: 'instructor@teammates.tmt',
    name: 'instructor',
    country: 'SG',
  },
  {
    accountVerificationRequestId: 'history-rejected-request',
    institute: 'Example Teaching Institute',
    status: AccountVerificationRequestStatus.REJECTED,
    createdAt: Date.now() - 1000 * 60 * 60 * 24 * 180,
    email: 'instructor@teammates.tmt',
    name: 'instructor',
    country: 'SG',
  },
];

/**
 * Review page for a single account verification request.
 */
@Component({
  selector: 'tm-admin-account-verification-request-page',
  templateUrl: './admin-account-verification-request-page.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [LoadingSpinnerDirective, RequestDetailsCardComponent],
})
export class AdminAccountVerificationRequestPageComponent implements OnInit {
  private readonly accountService = inject(AccountService);
  private readonly dateFormatService = inject(DateFormatService);
  private readonly statusMessageService = inject(StatusMessageService);
  private readonly timezoneService = inject(TimezoneService);

  readonly accountVerificationRequestId = input.required<string>();

  readonly isLoading = signal(true);
  readonly isInvalidLink = signal(false);
  readonly accountVerificationRequest = signal<AccountVerificationRequest | null>(null);
  readonly historicalRequests = signal<AccountVerificationRequest[]>(mockRequestHistory);

  readonly isEditing = signal(false);
  readonly isApprovingOrRejecting = signal(false);

  readonly timezone = this.timezoneService.guessTimezone() || 'UTC';
  readonly submitRequestDraft = (draft: AccountVerificationRequestDraft): Promise<void> =>
    this.saveRequestDetails(draft);

  ngOnInit(): void {
    this.accountService.getAccountVerificationRequest(this.accountVerificationRequestId()).subscribe({
      next: (accountVerificationRequest: AccountVerificationRequest) => {
        this.accountVerificationRequest.set(accountVerificationRequest);
        this.isLoading.set(false);
      },
      error: () => {
        this.isInvalidLink.set(true);
        this.isLoading.set(false);
      },
    });
  }

  approveRequest(): void {
    const accountVerificationRequest = this.accountVerificationRequest();
    if (!accountVerificationRequest || !this.canTakeAction(accountVerificationRequest.status) || this.isEditing()) {
      return;
    }

    this.runStatusTransition(
      this.accountService.approveAccountVerificationRequest(accountVerificationRequest.accountVerificationRequestId),
      (updatedRequest: AccountVerificationRequest) =>
        `Account verification request was successfully approved. Email has been sent to ${updatedRequest.email}.`,
    );
  }

  rejectRequest(): void {
    const accountVerificationRequest = this.accountVerificationRequest();
    if (!accountVerificationRequest || !this.canTakeAction(accountVerificationRequest.status) || this.isEditing()) {
      return;
    }

    this.runStatusTransition(
      this.accountService.rejectAccountVerificationRequest(accountVerificationRequest.accountVerificationRequestId),
      () => 'Account verification request was successfully rejected.',
    );
  }

  canTakeAction(status: AccountVerificationRequestStatus): boolean {
    return status === AccountVerificationRequestStatus.PENDING && !this.isApprovingOrRejecting();
  }

  startEditing(): void {
    if (!this.accountVerificationRequest()) {
      return;
    }
    this.isEditing.set(true);
  }

  cancelEditing(): void {
    this.isEditing.set(false);
  }

  async saveRequestDetails(draft: AccountVerificationRequestDraft): Promise<void> {
    const accountVerificationRequest = this.accountVerificationRequest();
    if (!accountVerificationRequest) {
      return;
    }

    try {
      const updatedRequest = await firstValueFrom(
        this.accountService.editAccountVerificationRequest(
          accountVerificationRequest.accountVerificationRequestId,
          toAccountVerificationRequestUpdateRequest(draft, accountVerificationRequest.status),
        ),
      );
      this.accountVerificationRequest.set(updatedRequest);
      this.isEditing.set(false);
      this.statusMessageService.showSuccessToast('Account verification request was successfully updated.');
    } catch (error) {
      const errorResponse = error as ErrorMessageOutput;
      this.statusMessageService.showErrorToast(errorResponse.error.message);
    }
  }

  getStatusBadgeClass(status: AccountVerificationRequestStatus): string {
    if (status === AccountVerificationRequestStatus.APPROVED) {
      return 'badge bg-success-subtle text-success-emphasis';
    }
    if (status === AccountVerificationRequestStatus.REJECTED) {
      return 'badge bg-danger-subtle text-danger-emphasis';
    }
    return 'badge bg-primary-subtle text-primary-emphasis';
  }

  formatTimestamp(timestamp: number): string {
    return this.dateFormatService.formatDateDetailed(timestamp, this.timezone);
  }

  private runStatusTransition(
    requestAction: ReturnType<AccountService['approveAccountVerificationRequest']>,
    getSuccessMessage: (updatedRequest: AccountVerificationRequest) => string,
  ): void {
    this.isApprovingOrRejecting.set(true);
    requestAction.subscribe({
      next: (updatedRequest: AccountVerificationRequest) => {
        this.accountVerificationRequest.set(updatedRequest);
        this.statusMessageService.showSuccessToast(getSuccessMessage(updatedRequest));
        this.isApprovingOrRejecting.set(false);
      },
      error: (resp: ErrorMessageOutput) => {
        this.statusMessageService.showErrorToast(resp.error.message);
        this.isApprovingOrRejecting.set(false);
      },
    });
  }
}
