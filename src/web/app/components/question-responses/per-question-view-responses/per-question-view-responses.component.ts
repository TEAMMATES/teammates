import { Component, Input, OnChanges, OnInit, TemplateRef } from '@angular/core';
import { FeedbackQuestionsService } from '../../../../services/feedback-questions.service';
import { FeedbackResponsesService } from '../../../../services/feedback-responses.service';
import { SimpleModalService } from '../../../../services/simple-modal.service';
import { TableComparatorService } from '../../../../services/table-comparator.service';
import {
  FeedbackSession, FeedbackSessionPublishStatus, FeedbackSessionSubmissionStatus,
  ResponseOutput, ResponseVisibleSetting, SessionVisibleSetting,
} from '../../../../types/api-output';
import { SortBy, SortOrder } from '../../../../types/sort-properties';
import {
  InstructorSessionResultSectionType,
} from '../../../pages-instructor/instructor-session-result-page/instructor-session-result-section-type.enum';
import { CommentTableModel } from '../../comment-box/comment-table/comment-table.component';
import { SimpleModalType } from '../../simple-modal/simple-modal';
import { InstructorResponsesViewBase } from '../instructor-responses-view-base';

/**
 * Component to display list of responses for one question.
 */
@Component({
  selector: 'tm-per-question-view-responses',
  templateUrl: './per-question-view-responses.component.html',
  styleUrls: ['./per-question-view-responses.component.scss'],
})
export class PerQuestionViewResponsesComponent extends InstructorResponsesViewBase implements OnInit, OnChanges {

  SortBy: typeof SortBy = SortBy;
  SortOrder: typeof SortOrder = SortOrder;

  @Input() responses: ResponseOutput[] = [];
  @Input() section: string = '';
  @Input() sectionType: InstructorSessionResultSectionType = InstructorSessionResultSectionType.EITHER;
  @Input() groupByTeam: boolean = true;
  @Input() indicateMissingResponses: boolean = true;
  @Input() showGiver: boolean = true;
  @Input() showRecipient: boolean = true;
  @Input() session: FeedbackSession = {
    courseId: '',
    timeZone: '',
    feedbackSessionName: '',
    instructions: '',
    submissionStartTimestamp: 0,
    submissionEndTimestamp: 0,
    gracePeriod: 0,
    sessionVisibleSetting: SessionVisibleSetting.AT_OPEN,
    responseVisibleSetting: ResponseVisibleSetting.AT_VISIBLE,
    submissionStatus: FeedbackSessionSubmissionStatus.OPEN,
    publishStatus: FeedbackSessionPublishStatus.NOT_PUBLISHED,
    isClosingEmailEnabled: true,
    isPublishedEmailEnabled: true,
    createdAtTimestamp: 0,
  };
  @Input() isDisplayOnly: boolean = false;
  @Input() statistics: string = '';

  responsesToShow: ResponseOutput[] = [];
  sortBy: SortBy = SortBy.NONE;
  sortOrder: SortOrder = SortOrder.ASC;

  constructor(private tableComparatorService: TableComparatorService,
              private questionsService: FeedbackQuestionsService,
              private feedbackResponsesService: FeedbackResponsesService,
              private simpleModalService: SimpleModalService) {
    super();
  }

  ngOnInit(): void {
    this.filterResponses();
  }

  ngOnChanges(): void {
    this.filterResponses();
  }

  private filterResponses(): void {
    const responsesToShow: ResponseOutput[] = [];
    for (const response of this.responses) {
      if (!this.indicateMissingResponses && response.isMissingResponse) {
        // filter out missing responses
        continue;
      }

      const shouldDisplayBasedOnSection: boolean = this.feedbackResponsesService
        .isFeedbackResponsesDisplayedOnSection(response, this.section, this.sectionType);

      if (!shouldDisplayBasedOnSection) {
        continue;
      }

      responsesToShow.push(response);
    }

    const hasRealResponse: boolean =
        responsesToShow.some((response: ResponseOutput) => !response.isMissingResponse);
    if (hasRealResponse) {
      this.responsesToShow = responsesToShow;
      this.sortResponses(this.sortBy);
    } else {
      // If there is no real response, it is not necessary to show any of the missing responses
      this.responsesToShow = [];
    }
  }

  sortResponses(by: SortBy): void {
    if (by !== SortBy.NONE && this.sortBy === by) {
      this.sortOrder = this.sortOrder === SortOrder.ASC ? SortOrder.DESC : SortOrder.ASC;
    } else {
      this.sortBy = by;
      this.sortOrder = SortOrder.ASC;
    }
    this.responsesToShow.sort(this.sortResponsesBy(by, this.sortOrder));
  }

  sortResponsesBy(by: SortBy, order: SortOrder):
    ((a: ResponseOutput, b: ResponseOutput) => number) {
    if (by === SortBy.NONE) {
      // Default order: giver team > giver name > recipient team > recipient name
      return ((a: ResponseOutput, b: ResponseOutput): number => {
        return this.tableComparatorService.compare(SortBy.GIVER_TEAM, order, a.giverTeam, b.giverTeam)
            || this.tableComparatorService.compare(SortBy.GIVER_NAME, order, a.giver, b.giver)
            || this.tableComparatorService.compare(SortBy.RECIPIENT_TEAM, order, a.recipientTeam, b.recipientTeam)
            || this.tableComparatorService.compare(SortBy.RECIPIENT_NAME, order, a.recipient, b.recipient);
      });
    }
    return ((a: ResponseOutput, b: ResponseOutput): number => {
      let strA: string;
      let strB: string;
      switch (by) {
        case SortBy.GIVER_TEAM:
          strA = a.giverTeam;
          strB = b.giverTeam;
          break;
        case SortBy.GIVER_NAME:
          strA = a.giver;
          strB = b.giver;
          break;
        case SortBy.RECIPIENT_TEAM:
          strA = a.recipientTeam;
          strB = b.recipientTeam;
          break;
        case SortBy.RECIPIENT_NAME:
          strA = a.recipient;
          strB = b.recipient;
          break;
        default:
          strA = '';
          strB = '';
      }
      return this.tableComparatorService.compare(by, order, strA, strB);
    });
  }

  /**
   * Opens the comments table modal.
   */
  showCommentTableModal(response: ResponseOutput, modal: TemplateRef<any>): void {
    const model: CommentTableModel = this.instructorCommentTableModel[response.responseId];
    model.isAddingNewComment = true;

    this.simpleModalService.openInformationModal('Comments', SimpleModalType.NEUTRAL, modal, { response, model });
  }

  /**
   * Check whether the question can have participant comments.
   */
  get canResponseHasComment(): boolean {
    return this.questionsService.isAllowedToHaveParticipantComment(this.question.questionType);
  }
}
