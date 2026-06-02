import { Component, Input, OnChanges } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbTooltip } from '@ng-bootstrap/ng-bootstrap/tooltip';
import { QuestionEditDetailsFormComponent } from './question-edit-details-form.component';
import {
  FeedbackConstantSumDistributePointsType,
  FeedbackConstantSumRecipientsQuestionDetails,
} from '../../../../types/api-output';

/**
 * Question details edit form component for constsum recipients question.
 */
@Component({
  selector: 'tm-constsum-recipients-question-edit-details-form',
  templateUrl: './constsum-recipients-question-edit-details-form.component.html',
  styleUrls: ['./constsum-recipients-question-edit-details-form.component.scss'],
  imports: [FormsModule, NgbTooltip],
})
export class ConstsumRecipientsQuestionEditDetailsFormComponent
  extends QuestionEditDetailsFormComponent<FeedbackConstantSumRecipientsQuestionDetails>
  implements OnChanges
{
  // enum
  FeedbackConstantSumDistributePointsType!: typeof FeedbackConstantSumDistributePointsType;

  @Input() questionNumber = 0;
  pointsRadioGroupName = '';

  constructor() {
    super();
    this.FeedbackConstantSumDistributePointsType = FeedbackConstantSumDistributePointsType;
  }

  ngOnChanges(): void {
    this.pointsRadioGroupName = `constsum-recipients-${this.questionNumber}`;
  }

  /**
   * Changes force uneven distribution option.
   */
  onForceUnevenDistribution(event: boolean): void {
    this.triggerModelChangeBatch({
      forceUnevenDistribution: event,
      distributePointsFor: event
        ? FeedbackConstantSumDistributePointsType.DISTRIBUTE_ALL_UNEVENLY
        : FeedbackConstantSumDistributePointsType.NONE,
    });
  }
}
