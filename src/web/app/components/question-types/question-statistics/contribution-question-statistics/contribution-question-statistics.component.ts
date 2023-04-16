import { Component, Input, OnChanges, OnInit, TemplateRef } from '@angular/core';
import { SimpleModalService } from '../../../../../services/simple-modal.service';
import { ContributionStatistics } from '../../../../../types/api-output';
import { DEFAULT_CONTRIBUTION_QUESTION_DETAILS } from '../../../../../types/default-question-structs';
import { SortBy } from '../../../../../types/sort-properties';
import {
  QuestionsSectionQuestions,
} from '../../../../pages-help/instructor-help-page/instructor-help-questions-section/questions-section-questions';
import { Sections } from '../../../../pages-help/instructor-help-page/sections';
import { SimpleModalType } from '../../../simple-modal/simple-modal-type';
import {
  ColumnData,
  SortableTableCellData,
} from '../../../sortable-table/sortable-table.component';
import {
  ContributionQuestionStatisticsCalculation,
} from '../question-statistics-calculation/contribution-question-statistics-calculation';
import { ContributionRatingsListComponent } from './contribution-ratings-list.component';
import { ContributionComponent } from './contribution.component';

/**
 * Statistics for contribution questions.
 */
@Component({
  selector: 'tm-contribution-question-statistics',
  templateUrl: './contribution-question-statistics.component.html',
  styleUrls: ['./contribution-question-statistics.component.scss'],
})
export class ContributionQuestionStatisticsComponent
  extends ContributionQuestionStatisticsCalculation
  implements OnInit, OnChanges {
  // enum
  QuestionsSectionQuestions: typeof QuestionsSectionQuestions = QuestionsSectionQuestions;
  Sections: typeof Sections = Sections;

  @Input() displayContributionStats: boolean = true;

  columnsData: ColumnData[] = [];
  rowsData: SortableTableCellData[][] = [];

  constructor(private simpleModalService: SimpleModalService) {
    super(DEFAULT_CONTRIBUTION_QUESTION_DETAILS());
  }

  ngOnInit(): void {
    this.parseStatistics();
    this.getTableData();
  }

  ngOnChanges(): void {
    this.parseStatistics();
    this.getTableData();
  }

  private getTableData(): void {
    if (!this.questionOverallStatistics) {
      return;
    }
    const statistics: ContributionStatistics = this.questionOverallStatistics;

    this.columnsData = [
      { header: 'Team', sortBy: SortBy.CONTRIBUTION_TEAM },
      { header: 'Recipient', sortBy: SortBy.CONTRIBUTION_RECIPIENT },
      {
        header: 'CC',
        sortBy: SortBy.CONTRIBUTION_VALUE,
        headerToolTip: "Claimed Contribution: This is the student's own estimation of his/her contributions",
      },
      {
        header: 'PC',
        sortBy: SortBy.CONTRIBUTION_VALUE,
        headerToolTip: 'Perceived Contribution: This is the average of what other team members think'
            + ' this student contributed',
      },
      {
        header: 'Diff',
        sortBy: SortBy.CONTRIBUTION_VALUE,
        headerToolTip: 'Perceived Contribution - Claimed Contribution',
      },
      { header: 'Ratings Received', headerToolTip: 'The list of points that this student received from others' },
    ];

    this.rowsData = Object.keys(this.emailToName).map((email: string) => {
      return [
        { value: this.emailToTeamName[email] },
        { value: `${this.emailToName[email]} (${email})` },
        {
          value: statistics.results[email].claimed,
          customComponent: {
            component: ContributionComponent,
            componentData: { value: statistics.results[email].claimed },
          },
        },
        {
          value: statistics.results[email].perceived,
          customComponent: {
            component: ContributionComponent,
            componentData: { value: statistics.results[email].perceived },
          },
        },
        {
          value: this.emailToDiff[email],
          customComponent: {
            component: ContributionComponent,
            componentData: { value: this.emailToDiff[email], diffOnly: true },
          },
        },
        {
          customComponent: {
            component: ContributionRatingsListComponent,
            componentData: { ratingsList: statistics.results[email].perceivedOthers },
          },
        },
      ];
    });
  }

  openHelpModal(modal: TemplateRef<any>): void {
    this.simpleModalService.openInformationModal(
      'More info about contribution questions', SimpleModalType.NEUTRAL, modal);
  }
}
