import { Component, Input } from '@angular/core';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { FeedbackContributionQuestionDetails } from '../../../../../feedback-question';
import {
  CONTRIBUTION_POINT_NOT_SUBMITTED,
  CONTRIBUTION_POINT_NOT_SURE,
  FeedbackContributionResponseDetails } from '../../../../../feedback-response';
import { BasicRecipientSubmissionFormComponent } from '../basic-recipient-submission-form';

/**
 * The contribution question submission form for a recipient.
 */
@Component({
  selector: 'tm-contribution-recipient-submission-form',
  templateUrl: './contribution-recipient-submission-form.component.html',
  styleUrls: ['./contribution-recipient-submission-form.component.scss'],
})
export class ContributionRecipientSubmissionFormComponent
    extends BasicRecipientSubmissionFormComponent
        <FeedbackContributionQuestionDetails, FeedbackContributionResponseDetails> {

  @Input()
  shouldShowHelpLink: boolean = true;

  CONTRIBUTION_POINT_NOT_SUBMITTED: number = CONTRIBUTION_POINT_NOT_SUBMITTED;
  CONTRIBUTION_POINT_NOT_SURE: number = CONTRIBUTION_POINT_NOT_SURE;

  constructor(private modalService: NgbModal) {
    super({
      isNotSureAllowed: false,
    }, {
      answer: CONTRIBUTION_POINT_NOT_SUBMITTED,
    });
  }

  get contributionQuestionPoints(): number[] {
    const points: number[] = [];
    for (let i: number = 200; i >= 0; i -= 10) {
      points.push(i);
    }
    return points;
  }

  /**
   * Opens a modal.
   */
  openModal(modal: any): void {
    this.modalService.open(modal);
  }
}
