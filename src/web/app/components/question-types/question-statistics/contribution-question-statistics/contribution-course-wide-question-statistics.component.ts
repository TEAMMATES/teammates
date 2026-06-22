import { Component, Input, OnChanges } from '@angular/core';
import { CourseWideRow, FeedbackContributionCourseWideStatistics } from '../../../../../types/api-output';
import { SortBy } from '../../../../../types/sort-properties';
import { QuestionsSectionQuestions } from '../../../../pages-help/instructor-help-page/instructor-help-questions-section/questions-section-questions';
import { Sections } from '../../../../pages-help/instructor-help-page/sections';
import { ContributionRatingsListComponent } from './contribution-ratings-list.component';
import { ContributionComponent } from './contribution.component';
import {
  ColumnData,
  SortableTableCellData,
  SortableTableComponent,
} from '../../../sortable-table/sortable-table.component';
import { TeammatesRouterDirective } from '../../../teammates-router/teammates-router.directive';

@Component({
  selector: 'tm-contribution-course-wide-question-statistics',
  templateUrl: './contribution-course-wide-question-statistics.component.html',
  imports: [TeammatesRouterDirective, SortableTableComponent],
})
export class ContributionCourseWideQuestionStatisticsComponent implements OnChanges {
  @Input({ required: true }) statistics!: FeedbackContributionCourseWideStatistics;

  QuestionsSectionQuestions!: typeof QuestionsSectionQuestions;
  Sections!: typeof Sections;

  columnsData: ColumnData[] = [];
  rowsData: SortableTableCellData[][] = [];

  constructor() {
    this.QuestionsSectionQuestions = QuestionsSectionQuestions;
    this.Sections = Sections;
  }

  ngOnChanges(): void {
    this.getTableData(this.statistics);
  }

  private getTableData(statistics?: FeedbackContributionCourseWideStatistics): void {
    if (!statistics) {
      this.rowsData = [];
      return;
    }

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

    this.rowsData = statistics.rows.map((row: CourseWideRow) => {
      const recipientLabel = row.recipientEmail ? `${row.recipientName} (${row.recipientEmail})` : row.recipientName;
      return [
        { value: row.teamName },
        { value: recipientLabel },
        {
          value: row.claimed,
          customComponent: {
            component: ContributionComponent,
            componentData: () => ({ value: row.claimed }),
          },
        },
        {
          value: row.perceived,
          customComponent: {
            component: ContributionComponent,
            componentData: () => ({ value: row.perceived }),
          },
        },
        {
          value: row.diff,
          customComponent: {
            component: ContributionComponent,
            componentData: () => ({ value: row.diff, diffOnly: true }),
          },
        },
        {
          customComponent: {
            component: ContributionRatingsListComponent,
            componentData: () => ({ ratingsList: row.ratingsReceived }),
          },
        },
      ];
    });
  }
}
