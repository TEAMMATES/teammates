import { Component, Input, OnChanges, OnInit } from "@angular/core";
import {
  ConfirmationModalOptions,
  ConfirmationModalService,
} from "../../../../../services/confirmation-modal.service";
import { ContributionStatistics } from "../../../../../types/api-output";
import { DEFAULT_CONTRIBUTION_QUESTION_DETAILS } from "../../../../../types/default-question-structs";
import { SortBy } from "../../../../../types/sort-properties";
import { QuestionsSectionQuestions } from "../../../../pages-help/instructor-help-page/instructor-help-questions-section/questions-section-questions";
import { Sections } from "../../../../pages-help/instructor-help-page/sections";
import { ConfirmationModalType } from "../../../confirmation-modal/confirmation-modal-type";
import {
  ColumnData,
  SortableTableCellData,
} from "../../../sortable-table/sortable-table.component";
import { ContributionQuestionStatisticsCalculation } from "../question-statistics-calculation/contribution-question-statistics-calculation";
import { ContributionRatingsListComponent } from "./contribution-ratings-list.component";
import { ContributionComponent } from "./contribution.component";

/**
 * Statistics for contribution questions.
 */
@Component({
  selector: "tm-contribution-question-statistics",
  templateUrl: "./contribution-question-statistics.component.html",
  styleUrls: ["./contribution-question-statistics.component.scss"],
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

  constructor(private confirmationModalService: ConfirmationModalService) {
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
      { header: "Team", sortBy: SortBy.CONTRIBUTION_TEAM },
      { header: "Recipient", sortBy: SortBy.CONTRIBUTION_RECIPIENT },
      {
        header: "CC",
        sortBy: SortBy.CONTRIBUTION_VALUE,
        headerToolTip:
          "Claimed Contribution: This is the student's own estimation of his/her contributions",
      },
      {
        header: "PC",
        sortBy: SortBy.CONTRIBUTION_VALUE,
        headerToolTip:
          "Perceived Contribution: This is the average of what other team members think" +
          " this student contributed",
      },
      {
        header: "Diff",
        sortBy: SortBy.CONTRIBUTION_VALUE,
        headerToolTip: "Perceived Contribution - Claimed Contribution",
      },
      {
        header: "Ratings Received",
        headerToolTip:
          "The list of points that this student received from others",
      },
    ];

    this.rowsData = Object.keys(this.emailToName).map((email: string) => {
      return [
        { value: this.emailToTeamName[email] },
        { value: this.emailToName[email] },
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
            componentData: {
              ratingsList: statistics.results[email].perceivedOthers,
            },
          },
        },
      ];
    });
  }

  openHelpModal(): void {
    const modalOptions: ConfirmationModalOptions = {
      isNotificationOnly: true,
      confirmMessage: "OK",
    };
    const modalContent: string = `<h4>How do I interpret these results?</h4>
    <ul>
      <li>
        Compare the values given in 'My view' with those under 'Team's view'.
        This tells you whether your perception matches what the team thinks, particularly regarding your own contribution.
        You may ignore minor differences.
      </li>
      <li>
        From the estimates you submit, we try to deduce the answer to this question:
        In your opinion, if your teammates are doing the project by themselves without you,
        how do they compare against each other in terms of contribution?
        We want to deduce your unbiased opinion about your team members' contributions.
      </li>
    </ul>
    <h4>How are contribution question results used in grading?</h4>
    <ul>
      <li>
        TEAMMATES does not calculate grades. Instructors are free to choose how they use contribution question results.
      </li>
      <li>
        TEAMMATES recommends that contribution question results be used only as flags to identify teams with contribution imbalances.
        Once identified, the instructor is recommended to investigate further before taking action.
      </li>
    </ul>
    <h4>How are the scores for contribution questions calculated?</h4>
    <p>
      Here are the important things to note about
      <a href="/web/front/help/instructor#team-contribution-questions" rel="noopener noreferrer" target="_blank">how contribution scores are calculated</a>:
    </p>
    <ul>
      <li>
        The contribution value you attribute to yourself is not used to calculate the team's view of your contribution.
        That means you cannot boost your perceived contribution by claiming a high contribution for yourself
        or attributing a low contribution to team members.
      </li>
      <li>
        We adjust the estimates you submitted to remove artificial inflation/deflation.
        For example, giving everyone <code>[Equal share + 20%]</code> is as same as giving everyone
        <code>[Equal share]</code> because in both cases all members have done a similar share of work.
      </li>
      <li>
        The sum of values in the team's view has been scaled to equal the sum of values in your view.
        That way, you can make a direct comparison between your view and the team's view.
        As this scaling is specific to you, the values you see in team's view may not match the values your team members see in their results.
      </li>
    </ul>`;
    this.confirmationModalService.open(
      "More info about contribution questions",
      ConfirmationModalType.NEUTRAL,
      modalContent,
      modalOptions
    );
  }
}
