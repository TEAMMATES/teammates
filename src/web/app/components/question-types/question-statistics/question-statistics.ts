import { Directive, Input, OnInit } from '@angular/core';
import {
  FeedbackParticipantType,
  FeedbackQuestionDetails,
  FeedbackResponseDetails,
} from '../../../../types/api-output';

/**
 * Holds some information of the feedback response.
 *
 * This is an adaptation of one of the API output format. A new interface
 * is required as the said API output format does not support precise typing
 * for the subclass of FeedbackResponseDetails.
 */
export interface Response<R extends FeedbackResponseDetails> {
  giver: string;
  giverEmail?: string;
  giverTeam: string;
  giverSection: string;
  recipient: string;
  recipientEmail?: string;
  recipientTeam: string;
  recipientSection: string;
  responseDetails: R;
}

/**
 * The abstract question statistics.
 */
@Directive()
// tslint:disable-next-line:directive-class-suffix
export class QuestionStatistics<Q extends FeedbackQuestionDetails, R extends FeedbackResponseDetails>
    implements OnInit {

  @Input() responses: Response<R>[] = [];
  @Input() question: Q;
  @Input() recipientType: FeedbackParticipantType = FeedbackParticipantType.NONE;
  @Input() isStudent: boolean = false;

  protected constructor(question: Q) {
    this.question = question;
  }

  ngOnInit(): void {
  }

}
