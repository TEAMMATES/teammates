import { Component, Input, OnChanges, TemplateRef, inject } from '@angular/core';
import { NgbTooltip } from '@ng-bootstrap/ng-bootstrap/tooltip';
import { ContributionRatingsListComponent } from './contribution-ratings-list.component';
import { ContributionComponent } from './contribution.component';
import { SimpleModalService } from '../../../../../services/simple-modal.service';
import {
  ContributionStatistics,
  ContributionStatisticsEntry,
  FeedbackContributionQuestionDetails,
  FeedbackContributionResponseDetails,
} from '../../../../../types/api-output';
import { DEFAULT_CONTRIBUTION_QUESTION_DETAILS } from '../../../../../types/default-question-structs';
import { SortBy } from '../../../../../types/sort-properties';
import { QuestionsSectionQuestions } from '../../../../pages-help/instructor-help-page/instructor-help-questions-section/questions-section-questions';
import { Sections } from '../../../../pages-help/instructor-help-page/sections';
import { SimpleModalType } from '../../../simple-modal/simple-modal-type';
import {
  ColumnData,
  SortableTableCellData,
  SortableTableComponent,
} from '../../../sortable-table/sortable-table.component';
import { TeammatesRouterDirective } from '../../../teammates-router/teammates-router.directive';
import { ContributionQuestionStatistics, Response } from '../../../../../types/question-statistics.model';
import { calculateContributionQuestionStatistics } from '../../../../utils/question-statistics.util';

/**
 * Statistics for contribution questions.
 */
@Component({
  selector: 'tm-contribution-question-statistics',
  templateUrl: './contribution-question-statistics.component.html',
  imports: [NgbTooltip, ContributionComponent, TeammatesRouterDirective, SortableTableComponent],
})
export class ContributionQuestionStatisticsComponent implements OnChanges {
  private simpleModalService = inject(SimpleModalService);

  @Input()
  question: FeedbackContributionQuestionDetails = DEFAULT_CONTRIBUTION_QUESTION_DETAILS();
  @Input()
  responses: Response<FeedbackContributionResponseDetails>[] = [];
  @Input()
  isStudent = false;
  @Input() statistics = '';
  @Input() displayContributionStats = true;

  // enum
  QuestionsSectionQuestions!: typeof QuestionsSectionQuestions;
  Sections!: typeof Sections;

  columnsData: ColumnData[] = [];
  rowsData: SortableTableCellData[][] = [];
  questionOverallStatistics?: ContributionStatistics;
  questionStatisticsForStudent?: ContributionStatisticsEntry & { claimedOthersValues: number[] };

  constructor() {
    this.QuestionsSectionQuestions = QuestionsSectionQuestions;
    this.Sections = Sections;
  }

  ngOnChanges(): void {
    const stats = calculateContributionQuestionStatistics(this.responses, this.statistics, this.isStudent);
    this.getTableData(stats);
    this.questionOverallStatistics = stats.questionOverallStatistics;
    this.questionStatisticsForStudent = stats.questionStatisticsForStudent;
  }

  private getTableData(stats: ContributionQuestionStatistics): void {
    if (!stats.questionOverallStatistics) {
      return;
    }
    const statistics: ContributionStatistics = stats.questionOverallStatistics;

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
        headerToolTip:
          'Perceived Contribution: This is the average of what other team members think' + ' this student contributed',
      },
      {
        header: 'Diff',
        sortBy: SortBy.CONTRIBUTION_VALUE,
        headerToolTip: 'Perceived Contribution - Claimed Contribution',
      },
      { header: 'Ratings Received', headerToolTip: 'The list of points that this student received from others' },
    ];

    this.rowsData = Object.keys(stats.userIdToName).map((userId: string) => {
      const email = stats.userIdToEmail[userId];
      const recipientLabel = email ? `${stats.userIdToName[userId]} (${email})` : stats.userIdToName[userId];
      return [
        { value: stats.userIdToTeamName[userId] },
        { value: recipientLabel },
        {
          value: statistics.results[userId].claimed,
          customComponent: {
            component: ContributionComponent,
            componentData: () => {
              return {
                value: statistics.results[userId].claimed,
              };
            },
          },
        },
        {
          value: statistics.results[userId].perceived,
          customComponent: {
            component: ContributionComponent,
            componentData: () => {
              return {
                value: statistics.results[userId].perceived,
              };
            },
          },
        },
        {
          value: stats.userIdToDiff[userId],
          customComponent: {
            component: ContributionComponent,
            componentData: () => {
              return {
                value: stats.userIdToDiff[userId],
                diffOnly: true,
              };
            },
          },
        },
        {
          customComponent: {
            component: ContributionRatingsListComponent,
            componentData: () => {
              return {
                ratingsList: statistics.results[userId].perceivedOthers,
              };
            },
          },
        },
      ];
    });
  }

  openHelpModal(modal: TemplateRef<void>): void {
    this.simpleModalService.openInformationModal(
      'More info about contribution questions',
      SimpleModalType.NEUTRAL,
      modal,
    );
  }
}
