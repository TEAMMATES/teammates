import { ChangeDetectionStrategy, Component, OnInit, computed, inject, input, signal } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { AccountService } from '../../../services/account.service';
import { DateFormatService } from '../../../services/date-format.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { TimezoneService } from '../../../services/timezone.service';
import { AccountVerificationRequestUpdateRequest } from '../../../types/api-request';
import { AccountVerificationRequest, AccountVerificationRequestStatus } from '../../../types/api-output';
import { ErrorMessageOutput } from '../../error-message-output';
import { LoadingSpinnerDirective } from '../../components/loading-spinner/loading-spinner.directive';
import { RequestDetailsCardComponent } from './request-details-card/request-details-card.component';

interface MockAccountRequestHistoryEntry {
  id: string;
  institute: string;
  status: AccountVerificationRequestStatus;
  submittedAt: number;
}

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
  readonly mockHistoricalRequests = signal<MockAccountRequestHistoryEntry[]>([]);
  readonly requestHistory = computed(() => {
    const request = this.accountVerificationRequest();
    if (!request) {
      return [];
    }

    return [
      {
        id: request.accountVerificationRequestId,
        institute: request.institute,
        status: request.status,
        submittedAt: request.createdAt,
      },
      ...this.mockHistoricalRequests(),
    ];
  });
  readonly isEditing = signal(false);
  readonly isApproving = signal(false);
  readonly isRejecting = signal(false);

  readonly requestStatus = AccountVerificationRequestStatus;
  readonly formatTimestampFn = (timestamp: number): string => this.formatTimestamp(timestamp);

  ngOnInit(): void {
    this.accountService.getAccountVerificationRequest(this.accountVerificationRequestId()).subscribe({
      next: (accountVerificationRequest: AccountVerificationRequest) => {
        this.accountVerificationRequest.set(accountVerificationRequest);
        this.mockHistoricalRequests.set(this.buildMockRequestHistory(accountVerificationRequest));
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

    this.isApproving.set(true);
    this.accountService
      .approveAccountVerificationRequest(accountVerificationRequest.accountVerificationRequestId)
      .subscribe({
        next: (updatedRequest: AccountVerificationRequest) => {
          this.accountVerificationRequest.set(updatedRequest);
          this.statusMessageService.showSuccessToast(
            `Account verification request was successfully approved. Email has been sent to ${updatedRequest.email}.`,
          );
          this.isApproving.set(false);
        },
        error: (resp: ErrorMessageOutput) => {
          this.statusMessageService.showErrorToast(resp.error.message);
          this.isApproving.set(false);
        },
      });
  }

  rejectRequest(): void {
    const accountVerificationRequest = this.accountVerificationRequest();
    if (!accountVerificationRequest || !this.canTakeAction(accountVerificationRequest.status) || this.isEditing()) {
      return;
    }

    this.isRejecting.set(true);
    this.accountService
      .rejectAccountVerificationRequest(accountVerificationRequest.accountVerificationRequestId)
      .subscribe({
        next: (updatedRequest: AccountVerificationRequest) => {
          this.accountVerificationRequest.set(updatedRequest);
          this.statusMessageService.showSuccessToast('Account verification request was successfully rejected.');
          this.isRejecting.set(false);
        },
        error: (resp: ErrorMessageOutput) => {
          this.statusMessageService.showErrorToast(resp.error.message);
          this.isRejecting.set(false);
        },
      });
  }

  canTakeAction(status: AccountVerificationRequestStatus): boolean {
    return status === AccountVerificationRequestStatus.PENDING && !this.isApproving() && !this.isRejecting();
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

  async saveRequestDetails(updateRequest: AccountVerificationRequestUpdateRequest): Promise<void> {
    const accountVerificationRequest = this.accountVerificationRequest();
    if (!accountVerificationRequest) {
      return;
    }

    try {
      const updatedRequest = await firstValueFrom(
        this.accountService.editAccountVerificationRequest(
          accountVerificationRequest.accountVerificationRequestId,
          updateRequest,
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
    const timezone: string = this.timezoneService.guessTimezone() || 'UTC';
    return this.dateFormatService.formatDateDetailed(timestamp, timezone);
  }

  private buildMockRequestHistory(
    accountVerificationRequest: AccountVerificationRequest,
  ): MockAccountRequestHistoryEntry[] {
    return [
      {
        id: 'history-approved-request',
        institute: 'Example Graduate School',
        status: AccountVerificationRequestStatus.APPROVED,
        submittedAt: accountVerificationRequest.createdAt - 1000 * 60 * 60 * 24 * 48,
      },
      {
        id: 'history-rejected-request',
        institute: 'Example Teaching Institute',
        status: AccountVerificationRequestStatus.REJECTED,
        submittedAt: accountVerificationRequest.createdAt - 1000 * 60 * 60 * 24 * 180,
      },
    ];
  }
}
