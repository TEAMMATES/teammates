import { Component, Input, OnInit } from '@angular/core';
import {
  FeedbackParticipantType,
  FeedbackQuestion,
  FeedbackQuestionType, NumberOfEntitiesToGiveFeedbackToSetting,
  ResponseOutput,
} from '../../../../types/api-output';
import { CommentRowMode } from '../../comment-box/comment-row/comment-row.mode';

/**
 * Feedback response in student results page view.
 */
@Component({
  selector: 'tm-student-view-responses',
  templateUrl: './student-view-responses.component.html',
  styleUrls: ['./student-view-responses.component.scss'],
})
export class StudentViewResponsesComponent implements OnInit {
  // enum
  CommentRowMode: typeof CommentRowMode = CommentRowMode;

  @Input() feedbackQuestion: FeedbackQuestion = {
    feedbackQuestionId: '',
    questionNumber: 1,
    questionBrief: '',
    questionDescription: '',
    questionDetails: {
      questionType: FeedbackQuestionType.MCQ,
      questionText: '',
    },
    questionType: FeedbackQuestionType.MCQ,
    giverType: FeedbackParticipantType.STUDENTS,
    recipientType: FeedbackParticipantType.STUDENTS,
    numberOfEntitiesToGiveFeedbackToSetting: NumberOfEntitiesToGiveFeedbackToSetting.UNLIMITED,
    customNumberOfEntitiesToGiveFeedbackTo: 0,
    showResponsesTo: [],
    showGiverNameTo: [],
    showRecipientNameTo: [],
  };
  @Input() responses: ResponseOutput[] = [];
  @Input() statistics: string = '';
  @Input() isSelfResponses: boolean = false;
  @Input() timezone: string = 'UTC';

  recipient: string = '';

  ngOnInit(): void {
    this.recipient = this.responses.length ? this.responses[0].recipient : '';
  }

}
