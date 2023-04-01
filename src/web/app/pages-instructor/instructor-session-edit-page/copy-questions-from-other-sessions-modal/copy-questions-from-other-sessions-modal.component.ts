import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { FeedbackQuestionsService } from '../../../../services/feedback-questions.service';
import { StatusMessageService } from '../../../../services/status-message.service';
import { TableComparatorService } from '../../../../services/table-comparator.service';
import { FeedbackQuestion, FeedbackQuestions } from '../../../../types/api-output';
import { Intent } from '../../../../types/api-request';
import { SortBy, SortOrder } from '../../../../types/sort-properties';
import { ErrorMessageOutput } from '../../../error-message-output';
import { FeedbackSessionTabModel, QuestionToCopyCandidate } from './copy-questions-from-other-sessions-modal-model';

/**
 * Modal to select questions to copy from other sessions.
 */
@Component({
  selector: 'tm-copy-questions-from-other-sessions-modal',
  templateUrl: './copy-questions-from-other-sessions-modal.component.html',
  styleUrls: ['./copy-questions-from-other-sessions-modal.component.scss'],
})
export class CopyQuestionsFromOtherSessionsModalComponent {

  // enum
  SortBy: typeof SortBy = SortBy;
  SortOrder: typeof SortOrder = SortOrder;

  // data
  feedbackSessionTabModels: FeedbackSessionTabModel[] = [];
  feedbackSessionTabModelsSortBy: SortBy = SortBy.COURSE_ID;

  constructor(public activeModal: NgbActiveModal,
              public statusMessageService: StatusMessageService,
              public feedbackQuestionsService: FeedbackQuestionsService,
              private tableComparatorService: TableComparatorService) { }

  /**
   * Toggles specific card and loads questions if needed.
   */
  toggleCard(feedbackSessionTabModel: FeedbackSessionTabModel): void {
    feedbackSessionTabModel.isTabExpanded = !feedbackSessionTabModel.isTabExpanded;
    if (!feedbackSessionTabModel.hasQuestionsLoaded) {
      this.loadQuestions(feedbackSessionTabModel);
    }
  }

  /**
   * Loads the questions in the feedback session.
   */
  loadQuestions(model: FeedbackSessionTabModel): void {
    model.hasQuestionsLoaded = false;
    model.hasLoadingFailed = false;
    model.questionsTableRowModels = [];
    this.feedbackQuestionsService.getFeedbackQuestions({
      courseId: model.courseId,
      feedbackSessionName: model.feedbackSessionName,
      intent: Intent.FULL_DETAIL,
    })
    .subscribe({
      next: (response: FeedbackQuestions) => {
        response.questions.forEach((q: FeedbackQuestion) => {
          const questionToCopy: QuestionToCopyCandidate = {
            question: q,
            isSelected: false,
          };
          model.questionsTableRowModels.push(questionToCopy);
        });
        model.hasQuestionsLoaded = true;
      },
      error: (resp: ErrorMessageOutput) => {
        model.hasLoadingFailed = true;
        this.statusMessageService.showErrorToast(resp.error.message);
      },
    });
  }

  /**
   * Gets the selected questions to copy.
   */
  getSelectedQuestions(): FeedbackQuestion[] {
    const selectedQuestions: FeedbackQuestion[] = [];
    this.feedbackSessionTabModels.forEach((model: FeedbackSessionTabModel) => {
      if (model.questionsTableRowModels.length > 0) {
        selectedQuestions.push(
          ...model.questionsTableRowModels
            .filter((c: QuestionToCopyCandidate) => c.isSelected)
            .map((c: QuestionToCopyCandidate) => c.question),
        );
      }
    });
    return selectedQuestions;
  }

  /**
   * Checks the option selected to sort feedback sessions.
   */
  isSelectedForSorting(by: SortBy): boolean {
    return this.feedbackSessionTabModelsSortBy === by;
  }

  /**
   * Check whether the feedback session questions are sorted by the given type and in the given order.
   */
  isSortQuestionsBy(model: FeedbackSessionTabModel, by: SortBy, order: SortOrder): boolean {
    return model.questionsTableRowModelsSortBy === by && model.questionsTableRowModelsSortOrder === order;
  }

  getAriaSort(model: FeedbackSessionTabModel, by: SortBy): String {
    if (model.questionsTableRowModelsSortBy !== by) {
      return 'none';
    }
    return model.questionsTableRowModelsSortOrder === SortOrder.ASC ? 'ascending' : 'descending';
  }

  /**
   * Sorts the list of feedback sessions.
   */
  sortFeedbackSessionTabs(by: SortBy): void {
    this.feedbackSessionTabModelsSortBy = by;
    this.feedbackSessionTabModels.sort(this.sortFeedbackSessionsBy(by));
  }

  /**
   * Sorts the list of questions for a feedback session.
   */
  sortQuestionsToCopyForFeedbackSession(model: FeedbackSessionTabModel, by: SortBy): void {
    model.questionsTableRowModelsSortBy = by;
    // reverse the sort order
    model.questionsTableRowModelsSortOrder =
      model.questionsTableRowModelsSortOrder === SortOrder.DESC ? SortOrder.ASC : SortOrder.DESC;
    model.questionsTableRowModels.sort(this.sortQuestionsBy(by, model.questionsTableRowModelsSortOrder));
  }

  /**
   * Generates a sorting function for sessions.
   */
  protected sortFeedbackSessionsBy(by: SortBy):
      ((a: FeedbackSessionTabModel, b: FeedbackSessionTabModel) => number) {
    return ((a: FeedbackSessionTabModel, b: FeedbackSessionTabModel): number => {
      let strA: string;
      let strB: string;
      let order: SortOrder;
      switch (by) {
        case SortBy.COURSE_ID:
          strA = a.courseId.toString();
          strB = b.courseId.toString();
          order = SortOrder.ASC;
          break;
        case SortBy.SESSION_NAME:
          strA = a.feedbackSessionName;
          strB = b.feedbackSessionName;
          order = SortOrder.ASC;
          break;
        case SortBy.SESSION_CREATION_DATE:
          strA = a.createdAtTimestamp.toString();
          strB = b.createdAtTimestamp.toString();
          order = SortOrder.DESC;
          break;
        default:
          strA = '';
          strB = '';
          order = SortOrder.ASC;
      }
      return this.tableComparatorService.compare(by, order, strA, strB);
    });
  }
  /**
   * Generates a sorting function for questions.
   */
  protected sortQuestionsBy(by: SortBy, order: SortOrder):
      ((a: QuestionToCopyCandidate, b: QuestionToCopyCandidate) => number) {
    return ((a: QuestionToCopyCandidate, b: QuestionToCopyCandidate): number => {
      let strA: string;
      let strB: string;
      switch (by) {
        case SortBy.QUESTION_TYPE:
          strA = a.question.questionType;
          strB = b.question.questionType;
          break;
        case SortBy.QUESTION_TEXT:
          strA = a.question.questionBrief;
          strB = b.question.questionBrief;
          break;
        default:
          strA = '';
          strB = '';
      }
      return this.tableComparatorService.compare(by, order, strA, strB);
    });
  }

  /**
   * Checks whether there are any selected questions.
   */
  get hasAnyQuestionsToCopySelected(): boolean {
    return this.feedbackSessionTabModels.reduce((a: boolean, b: FeedbackSessionTabModel) => {
      return a || !!b.questionsTableRowModels.find((c: QuestionToCopyCandidate) => c.isSelected);
    }, false);
  }

}
