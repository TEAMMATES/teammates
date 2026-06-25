import { ChangeDetectionStrategy, Component, effect, inject, input, signal } from '@angular/core';
import { EMPTY, Observable, catchError, finalize, firstValueFrom, map, switchMap, tap } from 'rxjs';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap/modal';
import { AccountService } from '../../../services/account.service';
import { DateFormatService } from '../../../services/date-format.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { TimezoneService } from '../../../services/timezone.service';
import { AccountVerificationRequest, AccountVerificationRequestStatus } from '../../../types/api-output';
import { ErrorMessageOutput } from '../../error-message-output';
import { LoadingSpinnerDirective } from '../../components/loading-spinner/loading-spinner.directive';
import { RouterLink } from '@angular/router';
import { AccountVerificationRequestRejectionType } from '../../../types/api-request';
import {
  AccountVerificationRequestDraft,
  toAccountVerificationRequestUpdateRequest,
} from './account-verification-request-draft';
import { RequestDetailsCardComponent } from './request-details-card/request-details-card.component';
import { RejectRequestModalComponent } from './reject-request-modal/reject-request-modal.component';

/**
 * Review page for a single account verification request.
 */
@Component({
  selector: 'tm-admin-account-verification-request-page',
  templateUrl: './admin-account-verification-request-page.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [LoadingSpinnerDirective, RequestDetailsCardComponent, RouterLink],
})
export class AdminAccountVerificationRequestPageComponent {
  private readonly accountService = inject(AccountService);
  private readonly dateFormatService = inject(DateFormatService);
  private readonly modalService = inject(NgbModal);
  private readonly statusMessageService = inject(StatusMessageService);
  private readonly timezoneService = inject(TimezoneService);

  readonly accountVerificationRequestId = input.required<string>();

  readonly isLoading = signal(true);
  readonly isInvalidLink = signal(false);
  readonly accountVerificationRequest = signal<AccountVerificationRequest | null>(null);
  readonly historicalRequests = signal<AccountVerificationRequest[]>([]);

  readonly isEditing = signal(false);
  readonly isApprovingOrRejecting = signal(false);

  readonly timezone = this.timezoneService.guessTimezone() || 'UTC';
  readonly submitRequestDraft = (draft: AccountVerificationRequestDraft): Promise<void> =>
    this.saveRequestDetails(draft);

  constructor() {
    effect((onCleanup) => {
      const requestId = this.accountVerificationRequestId();
      this.isInvalidLink.set(false);

      const subscription = this.accountService
        .getAccountVerificationRequest(requestId)
        .pipe(
          switchMap((accountVerificationRequest: AccountVerificationRequest) => {
            this.accountVerificationRequest.set(accountVerificationRequest);

            return this.accountService
              .getAccountVerificationRequests({ accountId: accountVerificationRequest.accountId })
              .pipe(map((resp) => ({ historicalRequests: resp.accountVerificationRequests })));
          }),
          tap(({ historicalRequests }) => this.historicalRequests.set(historicalRequests)),
          catchError(() => {
            this.isInvalidLink.set(true);
            this.accountVerificationRequest.set(null);
            this.historicalRequests.set([]);
            return EMPTY;
          }),
          finalize(() => this.isLoading.set(false)),
        )
        .subscribe();

      onCleanup(() => subscription.unsubscribe());
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

    const modalRef = this.modalService.open(RejectRequestModalComponent);
    modalRef.componentInstance.instituteName = accountVerificationRequest.institute;
    modalRef.result.then(
      (result: { rejectionType: AccountVerificationRequestRejectionType; additionalComments?: string }) => {
        this.runStatusTransition(
          this.accountService.rejectAccountVerificationRequest(
            { id: accountVerificationRequest.accountVerificationRequestId },
            result,
          ),
          () => 'Account verification request was successfully rejected.',
        );
      },
      () => {},
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

  isCurrentRequest(accountVerificationRequest: AccountVerificationRequest): boolean {
    const currentRequest = this.accountVerificationRequest();
    return currentRequest
      ? accountVerificationRequest.accountVerificationRequestId === currentRequest.accountVerificationRequestId
      : false;
  }

  private runStatusTransition(
    requestAction: Observable<AccountVerificationRequest>,
    getSuccessMessage: (updatedRequest: AccountVerificationRequest) => string,
  ): void {
    this.isApprovingOrRejecting.set(true);

    requestAction
      .pipe(
        tap((updatedRequest: AccountVerificationRequest) => {
          this.accountVerificationRequest.set(updatedRequest);
          this.statusMessageService.showSuccessToast(getSuccessMessage(updatedRequest));
        }),
        catchError((resp: ErrorMessageOutput) => {
          this.statusMessageService.showErrorToast(resp.error.message);
          return EMPTY;
        }),
        finalize(() => this.isApprovingOrRejecting.set(false)),
      )
      .subscribe();
  }
}
