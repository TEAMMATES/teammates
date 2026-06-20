import { Component, Input, TemplateRef, inject } from '@angular/core';
import { NgbTooltip } from '@ng-bootstrap/ng-bootstrap/tooltip';
import { FeedbackContributionRecipientStatistics } from '../../../../../types/api-output';
import { SimpleModalService } from '../../../../../services/simple-modal.service';
import { SimpleModalType } from '../../../simple-modal/simple-modal-type';
import { ContributionComponent } from './contribution.component';
import { TeammatesRouterDirective } from '../../../teammates-router/teammates-router.directive';

@Component({
  selector: 'tm-contribution-recipient-question-statistics',
  templateUrl: './contribution-recipient-question-statistics.component.html',
  imports: [NgbTooltip, ContributionComponent, TeammatesRouterDirective],
})
export class ContributionRecipientQuestionStatisticsComponent {
  private simpleModalService = inject(SimpleModalService);

  @Input({ required: true }) statistics!: FeedbackContributionRecipientStatistics;
  @Input() displayContributionStats = true;

  openHelpModal(modal: TemplateRef<void>): void {
    this.simpleModalService.openInformationModal(
      'More info about contribution questions',
      SimpleModalType.NEUTRAL,
      modal,
    );
  }
}
