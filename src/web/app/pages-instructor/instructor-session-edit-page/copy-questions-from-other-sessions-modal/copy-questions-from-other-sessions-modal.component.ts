import { Component, OnInit } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { TableComparatorService } from '../../../../services/table-comparator.service';
import { FeedbackQuestion } from '../../../../types/api-output';
import { SortBy, SortOrder } from '../../../../types/sort-properties';
import { QuestionToCopyCandidate } from './copy-questions-from-other-sessions-modal-model';

/**
 * Modal to select questions to copy from other sessions.
 */
@Component({
  selector: 'tm-copy-questions-from-other-sessions-modal',
  templateUrl: './copy-questions-from-other-sessions-modal.component.html',
  styleUrls: ['./copy-questions-from-other-sessions-modal.component.scss'],
})
export class CopyQuestionsFromOtherSessionsModalComponent implements OnInit {

  // enum
  SortBy: typeof SortBy = SortBy;
  SortOrder: typeof SortOrder = SortOrder;

  // data
  questionToCopyCandidates: QuestionToCopyCandidate[] = [];
  candidatesSortBy: SortBy = SortBy.NONE;
  candidatesSortOrder: SortOrder = SortOrder.ASC;

  constructor(public activeModal: NgbActiveModal, private tableComparatorService: TableComparatorService) { }

  ngOnInit(): void {
  }

  /**
   * Gets the selected questions to copy.
   */
  getSelectedQuestions(): FeedbackQuestion[] {
    return this.questionToCopyCandidates
        .filter((c: QuestionToCopyCandidate) => c.isSelected)
        .map((c: QuestionToCopyCandidate) => c.question);
  }

  /**
   * Sorts the list of questions to copy.
   */
  sortQuestionToCopyCandidates(by: SortBy): void {
    this.candidatesSortBy = by;
    // reverse the sort order
    this.candidatesSortOrder =
        this.candidatesSortOrder === SortOrder.DESC ? SortOrder.ASC : SortOrder.DESC;
    this.questionToCopyCandidates.sort(this.sortCandidatesBy(by, this.candidatesSortOrder));
  }

  /**
   * Generates a sorting function.
   */
  protected sortCandidatesBy(by: SortBy, order: SortOrder):
      ((a: QuestionToCopyCandidate, b: QuestionToCopyCandidate) => number) {
    return ((a: QuestionToCopyCandidate, b: QuestionToCopyCandidate): number => {
      let strA: string;
      let strB: string;
      switch (by) {
        case SortBy.SESSION_NAME:
          strA = a.feedbackSessionName;
          strB = b.feedbackSessionName;
          break;
        case SortBy.COURSE_ID:
          strA = a.courseId;
          strB = b.courseId;
          break;
        case SortBy.QUESTION_TYPE:
          strA = String(a.question.questionType);
          strB = String(b.question.questionType);
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
   * Checks whether there are any selected question.
   */
  get hasAnyQuestionsToCopySelected(): boolean {
    return this.questionToCopyCandidates.find((c: QuestionToCopyCandidate) => c.isSelected) !== undefined;
  }

}
