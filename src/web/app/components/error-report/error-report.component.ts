import { Component, OnInit } from '@angular/core';
import { HttpRequestService } from '../../../services/http-request.service';
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

  constructor(private httpRequestService: HttpRequestService) {}

  ngOnInit(): void {
  }

  /**
   * Sends the error report.
   */
  sendErrorReport(): void {
    const paramMap: { [key: string]: string } = {
      errorfeedbackemailsubject: this.subject,
      errorfeedbackrequestid: this.requestId,
    };
    this.sendButtonEnabled = false;
    this.httpRequestService.post('/errorreport', paramMap, this.content).subscribe(() => {
      this.errorReportSubmitted = true;
    }, (res: ErrorMessageOutput) => {
      this.sendButtonEnabled = true;
      console.error(res);
    });
  }

}
