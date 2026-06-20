import { NgClass } from '@angular/common';
import { Component, Input, OnInit } from '@angular/core';
import {
  FeedbackQuestionResultsStatistics,
  FeedbackQuestion,
  FeedbackQuestionType,
  NumberOfEntitiesToGiveFeedbackToSetting,
  QuestionGiverType,
  QuestionRecipientType,
  ResponseOutput,
} from '../../../../types/api-output';
import { CommentRowComponent } from '../../comment-box/comment-row/comment-row.component';
import { CommentRowMode } from '../../comment-box/comment-row/comment-row.mode';
import { CommentTableComponent } from '../../comment-box/comment-table/comment-table.component';
import { GiverCommentToCommentRowModelPipe } from '../../comment-box/giver-comment-to-comment-row-model.pipe';
import { CommentsToCommentTableModelPipe } from '../../comment-box/comments-to-comment-table-model.pipe';
import { SingleResponseComponent } from '../single-response/single-response.component';

/**
 * Feedback response in student results page view.
 */
@Component({
  selector: 'tm-student-view-responses',
  templateUrl: './student-view-responses.component.html',
  imports: [
    NgClass,
    SingleResponseComponent,
    CommentRowComponent,
    CommentTableComponent,
    GiverCommentToCommentRowModelPipe,
    CommentsToCommentTableModelPipe,
  ],
})
export class StudentViewResponsesComponent implements OnInit {
  // enum
  CommentRowMode!: typeof CommentRowMode;

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
    giverType: QuestionGiverType.STUDENTS,
    recipientType: QuestionRecipientType.STUDENTS,
    numberOfEntitiesToGiveFeedbackToSetting: NumberOfEntitiesToGiveFeedbackToSetting.UNLIMITED,
    customNumberOfEntitiesToGiveFeedbackTo: 0,
    showResponsesTo: [],
    showGiverNameTo: [],
    showRecipientNameTo: [],
  };
  @Input() responses: ResponseOutput[] = [];
  @Input() statistics?: FeedbackQuestionResultsStatistics;
  @Input() isSelfResponses = false;
  @Input() timezone = 'UTC';

  recipient = '';

  constructor() {
    this.CommentRowMode = CommentRowMode;
  }

  ngOnInit(): void {
    this.recipient = this.responses.length ? this.responses[0].recipient : '';
  }
}
