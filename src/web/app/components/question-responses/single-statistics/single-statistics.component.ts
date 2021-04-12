import { Component, Input, OnChanges, OnInit } from '@angular/core';
import { FeedbackResponsesService } from '../../../../services/feedback-responses.service';
import {
  FeedbackParticipantType,
  FeedbackQuestionDetails,
  FeedbackQuestionType,
  ResponseOutput,
} from '../../../../types/api-output';
import {
  InstructorSessionResultSectionType,
} from '../../../pages-instructor/instructor-session-result-page/instructor-session-result-section-type.enum';

/**
 * The component that will map a generic response statistics to its specialized view component.
 */
@Component({
  selector: 'tm-single-statistics',
  templateUrl: './single-statistics.component.html',
  styleUrls: ['./single-statistics.component.scss'],
})
export class SingleStatisticsComponent implements OnInit, OnChanges {

  @Input() responses: ResponseOutput[] = [];
  @Input() question: FeedbackQuestionDetails = {
    questionType: FeedbackQuestionType.TEXT,
    questionText: '',
  };
  @Input() recipientType: FeedbackParticipantType = FeedbackParticipantType.NONE;
  @Input() isStudent: boolean = false;
  @Input() statistics: string = '';
  @Input() displayContributionStats: boolean = true;
  @Input() section: string = '';
  @Input() sectionType: InstructorSessionResultSectionType = InstructorSessionResultSectionType.EITHER;

  // enum
  FeedbackQuestionType: typeof FeedbackQuestionType = FeedbackQuestionType;
  responsesToUse: ResponseOutput[] = [];

  constructor(private feedbackResponsesService: FeedbackResponsesService) { }

  ngOnInit(): void {
    this.filterResponses();
  }

  ngOnChanges(): void {
    this.filterResponses();
  }

  private filterResponses(): void {
    this.responsesToUse = this.responses.filter((response: ResponseOutput) => {
      if (response.isMissingResponse && this.question.questionType !== FeedbackQuestionType.CONTRIB) {
        // Missing response is meaningless for statistics
        // For contribution question statistics, need to keep the missing response
        // to build the response summary
        return false;
      }

      if (this.isUsingResponsesToSelf() && response.recipient !== 'You') {
        return false;
      }

      return this.feedbackResponsesService
          .isFeedbackResponsesDisplayedOnSection(response, this.section, this.sectionType);
    });
  }

  private isUsingResponsesToSelf(): boolean {
    return this.isStudent && this.question.questionType === FeedbackQuestionType.NUMSCALE;
  }
}
