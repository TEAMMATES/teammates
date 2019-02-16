import { Component } from '@angular/core';
import { environment } from '../../../environments/environment';
import { HttpRequestService } from '../../../services/http-request.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { MessageOutput } from '../../../types/api-output';
import { ErrorMessageOutput } from '../../error-message-output';

/**
 * Student recover session links page.
 */
@Component({
  selector: 'tm-student-recover-session-links-page',
  templateUrl: './link-recovery-page.component.html',
  styleUrls: ['./link-recovery-page.component.scss'],
})
export class LinkRecoveryPageComponent {

  readonly supportEmail: string = environment.supportEmail;
  recoveryEmail: string = '';

  constructor(private httpRequestService: HttpRequestService,
              private statusMessageService: StatusMessageService) { }

  /**
   * Sends the feedback session links to the registered email address.
   */
  onSubmitLinkRecovery(): void {
    if (!this.recoveryEmail) {
      this.statusMessageService.showErrorMessage('Please enter an email address.');
      return;
    }

    const paramsMap: { [key: string]: string } = {
      recoveryemail: this.recoveryEmail,
    };

    this.httpRequestService.get('/recovery', paramsMap)
      .subscribe((resp: MessageOutput) => {
        this.statusMessageService.showSuccessMessage(resp.message);
        this.recoveryEmail = ''; // Reset input field
      }, (response: ErrorMessageOutput) => {
        this.statusMessageService.showErrorMessage(response.error.message);
      });
  }
}
