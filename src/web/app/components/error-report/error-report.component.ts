import { Component, OnInit } from '@angular/core';
import { ErrorReportService } from '../../../services/error-report.service';
import { ErrorReportRequest } from '../../../types/api-request';
import { ErrorMessageOutput } from '../../error-message-output';

/**
 * Error report component.
 */
@Component({
  selector: 'tm-error-report',
  templateUrl: './error-report.component.html',
  styleUrls: ['./error-report.component.scss'],
})
export class ErrorReportComponent implements OnInit {

  errorMessage: string = '';
  subject: string = 'User-submitted Error Report';
  content: string = '';
  requestId: string = '';
  sendButtonEnabled: boolean = true;
  errorReportSubmitted: boolean = false;

  constructor(private errorReportService: ErrorReportService) {}

  ngOnInit(): void {
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
    this.errorReportService.sendErrorReport({ request }).subscribe(() => {
      this.errorReportSubmitted = true;
    }, (res: ErrorMessageOutput) => {
      this.sendButtonEnabled = true;
      console.error(res);
    });
  }

}
