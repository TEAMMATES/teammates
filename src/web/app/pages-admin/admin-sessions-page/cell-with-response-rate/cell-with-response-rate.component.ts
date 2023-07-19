import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { FeedbackSessionsService } from '../../../../services/feedback-sessions.service';
import { StatusMessageService } from '../../../../services/status-message.service';
import { FeedbackSessionStats } from '../../../../types/api-output';
import { AjaxLoadingModule } from '../../../components/ajax-loading/ajax-loading.module';
import { ErrorMessageOutput } from '../../../error-message-output';

@Component({
    selector: 'tm-response-rate',
    templateUrl: './cell-with-response-rate.component.html',
    imports: [AjaxLoadingModule, CommonModule],
    standalone: true,
})
export class ResponseRateComponent {
    @Input() courseId: string = '';
    @Input() feedbackSessionName: string = '';
    empty: boolean = true;
    isLoading: boolean = false;
    responseRate: string = '';

    constructor(private statusMessageService: StatusMessageService,
        private feedbackSessionsService: FeedbackSessionsService) {}
    /**
     * Gets the response rate of the session
     */
    getResponseRate(): void {
        this.isLoading = true;
        this.feedbackSessionsService.loadSessionStatistics(
          this.courseId, this.feedbackSessionName)
        .subscribe({
          next: (resp: FeedbackSessionStats) => {
              this.responseRate = `${resp.submittedTotal} / ${resp.expectedTotal}`;
              this.isLoading = false;
              this.empty = false;
          },
          error: (resp: ErrorMessageOutput) => {
            this.statusMessageService.showErrorToast(resp.error.message);
          },
        });
    }
}
