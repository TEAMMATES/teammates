import { Component, Input, OnInit } from '@angular/core';
import {
  CommentOutput,
  FeedbackSession, FeedbackSessionPublishStatus, FeedbackSessionSubmissionStatus,
  QuestionOutput,
  ResponseVisibleSetting,
  SessionVisibleSetting,
} from '../../../../types/api-output';
import { CommentRowMode, CommentRowModel } from '../../comment-box/comment-row/comment-row.component';
import { ResponsesInstructorCommentsBase } from '../responses-instructor-comments-base';

/**
 * A list of responses grouped in GRQ/RGQ mode.
 */
@Component({
  selector: 'tm-grouped-responses',
  templateUrl: './grouped-responses.component.html',
  styleUrls: ['./grouped-responses.component.scss'],
})
export class GroupedResponsesComponent extends ResponsesInstructorCommentsBase implements OnInit {

  // enum
  CommentRowMode: typeof CommentRowMode = CommentRowMode;

  @Input() responses: QuestionOutput[] = [];

  @Input() isGrq: boolean = true;
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

  constructor() {
    super();
  }

  ngOnInit(): void {
  }

  get fromToTop(): string {
    if (this.isGrq) {
      const team: string = this.responses[0].allResponses[0].recipientTeam !== '' ?
          `(${this.responses[0].allResponses[0].recipientTeam})` : '';
      return `To: ${this.responses[0].allResponses[0].recipient} ${team}`;
    }
    return `From: ${this.responses[0].allResponses[0].giver} (${this.responses[0].allResponses[0].giverTeam})`;
  }

  get fromToBot(): string {
    if (this.isGrq) {
      return `From: ${this.responses[0].allResponses[0].giver} (${this.responses[0].allResponses[0].giverTeam})`;
    }  {
      const team: string = this.responses[0].allResponses[0].recipientTeam !== '' ?
          `(${this.responses[0].allResponses[0].recipientTeam})` : '';
      return `To: ${this.responses[0].allResponses[0].recipient} ${team}`;
    }
  }

  /**
   * Transforms participant comment to comment row model.
   */
  transformParticipantCommentToCommandRowModel(participantComment: CommentOutput): CommentRowModel {
    return {
      originalComment: participantComment,
      timezone: this.session.timeZone,
      commentGiverName: participantComment.commentGiverName,
      lastEditorName: participantComment.lastEditorName,
      commentEditFormModel: {
        commentText: participantComment.commentText,
        isUsingCustomVisibilities: false,
        showCommentTo: [],
        showGiverNameTo: [],
      },
      isEditing: false,
    };
  }

}
