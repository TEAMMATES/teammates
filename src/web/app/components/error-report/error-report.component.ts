import { Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { environment } from '../../../environments/environment';
import { ErrorReportService } from '../../../services/error-report.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { ErrorReportRequest } from '../../../types/api-request';
import { ErrorMessageOutput } from '../../error-message-output';

/**
 * Error report component.
 */
@Component({
  selector: 'tm-error-report',
  templateUrl: './error-report.component.html',
  styleUrls: ['./error-report.component.scss'],
  imports: [FormsModule],
})
export class ErrorReportComponent implements OnInit {
  private errorReportService = inject(ErrorReportService);
  private ngbActiveModal = inject(NgbActiveModal);
  private statusMessageService = inject(StatusMessageService);

  errorMessage = '';
  subject = 'User-submitted Error Report';
  content = '';
  requestId = '';
  sendButtonEnabled = true;
  errorReportEnabled = true;
  errorReportSubmitted = false;
  csrfErrorMessages: string[] = ['Missing CSRF token.', 'Invalid CSRF token.'];
  readonly supportEmail: string = environment.supportEmail;

  ngOnInit(): void {
    if (this.csrfErrorMessages.includes(this.errorMessage)) {
      this.errorReportEnabled = false;
    }
  }

  /**
   * Sends the error report.
   */
  sendErrorReport(): void {
    const request: ErrorReportRequest = {
      requestId: this.requestId,
      subject: this.subject,
      content: this.content,
    };

    this.sendButtonEnabled = false;
    this.errorReportService.sendErrorReport({ request }).subscribe({
      next: () => {
        this.errorReportSubmitted = true;
        this.statusMessageService.showSuccessToast('Your error report has been successfully sent');
        this.ngbActiveModal.close();
      },
      error: (res: ErrorMessageOutput) => {
        this.sendButtonEnabled = true;
        this.statusMessageService.showErrorToast(res.error.message);
      },
    });
  }
}
