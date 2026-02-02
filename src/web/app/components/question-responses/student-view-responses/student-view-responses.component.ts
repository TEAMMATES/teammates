import { NgClass, NgFor, NgIf } from '@angular/common';
import { Component, Input, OnInit } from '@angular/core';
import {
  FeedbackParticipantType,
  FeedbackQuestion,
  FeedbackQuestionType, NumberOfEntitiesToGiveFeedbackToSetting,
  ResponseOutput,
} from '../../../../types/api-output';
import { CommentRowComponent } from '../../comment-box/comment-row/comment-row.component';
import { CommentRowMode } from '../../comment-box/comment-row/comment-row.mode';
import { CommentTableComponent } from '../../comment-box/comment-table/comment-table.component';
import { CommentToCommentRowModelPipe } from '../../comment-box/comment-to-comment-row-model.pipe';
import { CommentsToCommentTableModelPipe } from '../../comment-box/comments-to-comment-table-model.pipe';
import { SingleResponseComponent } from '../single-response/single-response.component';

/**
 * Feedback response in student results page view.
 */
@Component({
  selector: 'tm-student-view-responses',
  templateUrl: './student-view-responses.component.html',
  styleUrls: ['./student-view-responses.component.scss'],
  imports: [
    NgClass,
    NgFor,
    SingleResponseComponent,
    NgIf,
    CommentRowComponent,
    CommentTableComponent,
    CommentToCommentRowModelPipe,
    CommentsToCommentTableModelPipe,
  ],
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
