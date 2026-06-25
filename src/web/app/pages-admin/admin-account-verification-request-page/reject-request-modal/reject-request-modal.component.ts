import { ChangeDetectionStrategy, Component, Input, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap/modal';
import { environment } from '../../../../environments/environment';
import { AccountVerificationRequestRejectionType } from '../../../../types/api-request';

@Component({
  selector: 'tm-reject-request-modal',
  templateUrl: './reject-request-modal.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [FormsModule],
})
export class RejectRequestModalComponent {
  readonly activeModal = inject(NgbActiveModal);

  @Input() instituteName = '';

  readonly selectedRejectionType = signal<AccountVerificationRequestRejectionType | null>(null);
  readonly additionalComments = signal('');
  readonly isValid = computed(() => this.selectedRejectionType() !== null);

  readonly RejectionType = AccountVerificationRequestRejectionType;

  readonly rejectionTypeLabels: Record<AccountVerificationRequestRejectionType, string> = {
    [AccountVerificationRequestRejectionType.ALREADY_VERIFIED]: 'Already Verified',
    [AccountVerificationRequestRejectionType.CANNOT_VERIFY_IDENTITY]: 'Cannot Verify Identity',
    [AccountVerificationRequestRejectionType.NOT_OFFICIAL_EMAIL]: 'Not Official Email',
    [AccountVerificationRequestRejectionType.NOT_INSTRUCTOR_ACCOUNT]: 'Not Instructor Account',
    [AccountVerificationRequestRejectionType.OTHERS]: 'Others / No specific reason',
  };

  private readonly rejectionReasons: Record<AccountVerificationRequestRejectionType, string> = {
    [AccountVerificationRequestRejectionType.ALREADY_VERIFIED]: 'Your account is already verified for this institute.',
    [AccountVerificationRequestRejectionType.CANNOT_VERIFY_IDENTITY]:
      'We were unable to verify that you belong to the institute.',
    [AccountVerificationRequestRejectionType.NOT_OFFICIAL_EMAIL]:
      'The email address provided does not appear to be an official email address issued by the institute.',
    [AccountVerificationRequestRejectionType.NOT_INSTRUCTOR_ACCOUNT]:
      'Your account does not appear to belong to an instructor.',
    [AccountVerificationRequestRejectionType.OTHERS]: '',
  };

  readonly rejectionTypes = Object.values(AccountVerificationRequestRejectionType);

  readonly emailPreview = computed(() => {
    const rejectionType = this.selectedRejectionType();
    const reason =
      rejectionType && rejectionType !== AccountVerificationRequestRejectionType.OTHERS
        ? this.rejectionReasons[rejectionType]
        : null;
    const comments = this.additionalComments().trim();
    return { reason, comments };
  });

  readonly supportEmail = environment.supportEmail;

  confirm(): void {
    const rejectionType = this.selectedRejectionType();
    if (!rejectionType) {
      return;
    }
    const additionalComments = this.additionalComments().trim() || undefined;
    this.activeModal.close({ rejectionType, additionalComments });
  }

  dismiss(): void {
    this.activeModal.dismiss();
  }
}
