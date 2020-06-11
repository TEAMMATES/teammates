import { Component, Input, OnChanges, OnInit } from '@angular/core';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import {
  ContributionStatistics,
  ContributionStatisticsEntry,
  FeedbackContributionQuestionDetails,
  FeedbackContributionResponseDetails,
} from '../../../../types/api-output';
import { DEFAULT_CONTRIBUTION_QUESTION_DETAILS } from '../../../../types/default-question-structs';
import { SortBy } from '../../../../types/sort-properties';
import { ColumnData, CustomTableCellData } from '../../sortable-table/sortable-table.component';
import { ContributionRatingsListComponent } from './contribution-ratings-list.component';
import { ContributionComponent } from './contribution.component';
import { QuestionStatistics } from './question-statistics';

/**
 * Statistics for contribution questions.
 */
@Component({
  selector: 'tm-contribution-question-statistics',
  templateUrl: './contribution-question-statistics.component.html',
  styleUrls: ['./contribution-question-statistics.component.scss'],
})
export class ContributionQuestionStatisticsComponent
    extends QuestionStatistics<FeedbackContributionQuestionDetails, FeedbackContributionResponseDetails>
    implements OnInit, OnChanges {

  @Input() statistics: string = '';
  @Input() displayContributionStats: boolean = true;

  emailToTeamName: Record<string, string> = {};
  emailToName: Record<string, string> = {};
  emailToDiff: Record<string, number> = {};
  questionOverallStatistics?: ContributionStatistics;
  questionStatisticsForStudent?: ContributionStatisticsEntry;

  columnsData: ColumnData[] = [];
  rowsData: any[][] | CustomTableCellData[][] = [];

  constructor(private modalService: NgbModal) {
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

  parseStatistics(): void {
    this.emailToTeamName = {};
    this.emailToName = {};

    this.questionOverallStatistics = {
      results: {},
    };
    this.questionStatisticsForStudent = {
      claimed: 0,
      perceived: 0,
      claimedOthers: [],
      perceivedOthers: [],
    };

    if (this.statistics) {
      const statisticsObject: ContributionStatistics = JSON.parse(this.statistics);
      if (this.isStudent) {
        const results: ContributionStatisticsEntry[] = Object.values(statisticsObject.results);
        if (results.length) {
          this.questionStatisticsForStudent = results[0];
        }
      } else {
        for (const response of this.responses) {
          // the recipient email will always exist for contribution question when viewing by instructors
          if (!response.recipientEmail) {
            continue;
          }

          if (!this.emailToTeamName[response.recipientEmail]) {
            this.emailToTeamName[response.recipientEmail] = response.recipientTeam;
          }
          if (!this.emailToName[response.recipientEmail]) {
            this.emailToName[response.recipientEmail] = response.recipient;
          }
        }

        this.questionOverallStatistics = statisticsObject;

        for (const email of Object.keys(this.emailToName)) {
          const statisticsForEmail: ContributionStatisticsEntry = this.questionOverallStatistics.results[email];
          const { claimed }: { claimed: number } = statisticsForEmail;
          const { perceived }: { perceived: number } = statisticsForEmail;
          if (claimed < 0 || perceived < 0) {
            this.emailToDiff[email] = -999;
          } else {
            this.emailToDiff[email] = perceived - claimed;
          }
        }
      }
    }
  }

  private getTableData(): void {
    if (!this.questionOverallStatistics) {
      return;
    }
    const statistics: ContributionStatistics = this.questionOverallStatistics;

    this.columnsData = [
      { header: 'Team', sortBy: SortBy.CONTRIBUTION_TEAM },
      { header: 'Recipient', sortBy: SortBy.CONTRIBUTION_RECIPIENT },
      { header: 'CC', sortBy: SortBy.CONTRIBUTION_VALUE, component: ContributionComponent },
      { header: 'PC', sortBy: SortBy.CONTRIBUTION_VALUE, component: ContributionComponent },
      { header: 'Diff', sortBy: SortBy.CONTRIBUTION_VALUE, component: ContributionComponent },
      { header: 'Ratings Received', component: ContributionRatingsListComponent },
    ];

    this.rowsData = Object.keys(this.emailToName).map((email: string) => {
      return [
        this.emailToTeamName[email],
        this.emailToName[email],
        { value: statistics.results[email].claimed, data: { value: statistics.results[email].claimed } },
        { value: statistics.results[email].perceived, data: { value: statistics.results[email].perceived } },
        { value: this.emailToDiff[email], data: { value: this.emailToDiff[email], diffOnly: true } },
        { data: { ratingsList: statistics.results[email].perceivedOthers } },
      ];
    });
  }

  /**
   * Opens a modal.
   */
  openModal(modal: any): void {
    this.modalService.open(modal);
  }

}
