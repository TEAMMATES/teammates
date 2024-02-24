import { Injectable } from '@angular/core';
import { NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { saveAs } from 'file-saver';
import { concat } from 'rxjs';
import { finalize, takeWhile } from 'rxjs/operators';
import { FeedbackSessionsService } from './feedback-sessions.service';
import { ProgressBarService } from './progress-bar.service';
import { SimpleModalService } from './simple-modal.service';
import { StatusMessageService } from './status-message.service';
import { SimpleModalType } from '../app/components/simple-modal/simple-modal-type';
import { ErrorMessageOutput } from '../app/error-message-output';
import {
  InstructorSessionResultSectionType,
} from '../app/pages-instructor/instructor-session-result-page/instructor-session-result-section-type.enum';
import {
  FeedbackQuestion,
} from '../types/api-output';
import {
  Intent,
} from '../types/api-request';

/**
 * Handles sessions related actions.
 */
@Injectable({
  providedIn: 'root',
})
export class FeedbackSessionActionsService {

  constructor(private feedbackSessionsService: FeedbackSessionsService,
      private simpleModalService: SimpleModalService,
      private progressBarService: ProgressBarService,
      private statusMessageService: StatusMessageService) {
  }

  /**
   * Downloads session result.
   */
  downloadSessionResult(
    courseId: string,
    feedbackSessionName: string,
    intent: Intent,
    indicateMissingResponses: boolean,
    showStatistics: boolean,
    questions: FeedbackQuestion[],
    groupBySection?: string,
    sectionDetail?: InstructorSessionResultSectionType,
  ): void {
    const filename: string =
      `${courseId}_${feedbackSessionName}_result.csv`;
    let blob: any;
    let downloadAborted: boolean = false;
    const outputData: string[] = [];
    const modalContent: string = 'Downloading the results of your feedback session...';
    const loadingModal: NgbModalRef = this.simpleModalService.openLoadingModal(
        'Download Progress', SimpleModalType.LOAD, modalContent);
    loadingModal.result.then(() => {
      downloadAborted = true;
    });
    outputData.push(`Course,${courseId}\n`);
    outputData.push(`Session Name,${feedbackSessionName}\n`);

    concat(
      ...questions.map((question: FeedbackQuestion) =>
        this.feedbackSessionsService.downloadSessionResults(
            courseId,
            feedbackSessionName,
            intent,
            indicateMissingResponses,
            showStatistics,
            question.feedbackQuestionId,
            groupBySection,
            sectionDetail,
        ),
      ),
      this.feedbackSessionsService.downloadFeedbackSessionNonSubmitterList(
        courseId,
        feedbackSessionName,
      ),
    ).pipe(
      takeWhile(() => !downloadAborted),
      finalize(() => loadingModal.close()),
    )
      .subscribe({
        next: (resp: string) => {
          outputData.push(resp);
          const numberOfItemsDownloaded: number = outputData.length;
          // Include non-submitter list
          const totalNumberOfItems: number = questions.length + 1;
          const progressPercentage: number = Math.round(100 * numberOfItemsDownloaded / totalNumberOfItems);
          this.progressBarService.updateProgress(progressPercentage);
        },
        complete: () => {
          if (downloadAborted) {
            return;
          }
          blob = new Blob(outputData, { type: 'text/csv' });
          saveAs(blob, filename);
        },
        error: (resp: ErrorMessageOutput) => {
          this.statusMessageService.showErrorToast(resp.error.message);
        },
      });
  }
}
