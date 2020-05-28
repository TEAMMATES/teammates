import { Component, Input, OnChanges, OnInit } from '@angular/core';
import {
  FeedbackSession,
  FeedbackSessionPublishStatus,
  FeedbackSessionSubmissionStatus, QuestionOutput, ResponseOutput,
  ResponseVisibleSetting,
  SessionVisibleSetting,
} from '../../../../types/api-output';
import {
  InstructorSessionResultSectionType,
} from '../../../pages-instructor/instructor-session-result-page/instructor-session-result-section-type.enum';
import { ResponsesInstructorCommentsBase } from '../responses-instructor-comments-base';

/**
 * Component to display list of responses in GRQ/RGQ view.
 */
@Component({
  selector: 'tm-grq-rgq-view-responses',
  templateUrl: './grq-rgq-view-responses.component.html',
  styleUrls: ['./grq-rgq-view-responses.component.scss'],
})
export class GrqRgqViewResponsesComponent extends ResponsesInstructorCommentsBase implements OnInit, OnChanges {

  @Input() responses: QuestionOutput[] = [];
  @Input() section: string = '';
  @Input() sectionType: InstructorSessionResultSectionType = InstructorSessionResultSectionType.EITHER;
  @Input() groupByTeam: boolean = true;
  @Input() showStatistics: boolean = true;
  @Input() indicateMissingResponses: boolean = true;
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

  @Input() isGrq: boolean = true;

  teamsToUsers: Record<string, string[]> = {};
  usersToTeams: Record<string, string> = {};
  userToEmail: Record<string, string> = {};

  teamExpanded: Record<string, boolean> = {};
  userExpanded: Record<string, boolean> = {};

  responsesToShow: Record<string, Record<string, QuestionOutput[]>> = {};

  constructor() {
    super();
  }

  ngOnInit(): void {
    this.filterResponses();
  }

  ngOnChanges(): void {
    this.filterResponses();
  }

  private filterResponses(): void {
    this.responsesToShow = {};
    this.teamsToUsers = {};
    this.usersToTeams = {};
    this.userToEmail = {};
    this.teamExpanded = {};
    this.userExpanded = {};
    for (const question of this.responses) {
      for (const response of question.allResponses) {
        if (this.isGrq) {
          this.teamsToUsers[response.giverTeam] = this.teamsToUsers[response.giverTeam] || [];
          this.usersToTeams[response.giver] = this.usersToTeams[response.giver] || '';
          if (this.teamsToUsers[response.giverTeam].indexOf(response.giver) === -1) {
            this.teamsToUsers[response.giverTeam].push(response.giver);
            this.usersToTeams[response.giver] = response.giverTeam;
            this.teamExpanded[response.giverTeam] = this.isExpandAll;
          }
          if (response.giverEmail) {
            this.userToEmail[response.giver] = response.giverEmail;
          }
          this.userExpanded[response.giver] = this.isExpandAll;
        } else {
          this.usersToTeams[response.recipient] = this.usersToTeams[response.recipient] || '';
          this.userExpanded[response.recipient] = this.isExpandAll;
          if (!response.recipientTeam) {
            // Recipient is team
            this.teamsToUsers[response.recipient] = this.teamsToUsers[response.recipient] || [];
            if (this.teamsToUsers[response.recipient].indexOf(response.recipient) === -1) {
              this.teamsToUsers[response.recipient].push(response.recipient);
              this.teamExpanded[response.recipient] = this.isExpandAll;
            }
          } else {
            this.teamsToUsers[response.recipientTeam] = this.teamsToUsers[response.recipientTeam] || [];
            if (this.teamsToUsers[response.recipientTeam].indexOf(response.recipient) === -1) {
              this.teamsToUsers[response.recipientTeam].push(response.recipient);
              this.usersToTeams[response.recipient] = response.recipientTeam;
              this.teamExpanded[response.recipientTeam] = this.isExpandAll;
            }
          }
          if (response.recipientEmail) {
            this.userToEmail[response.recipient] = response.recipientEmail;
          }
        }
      }
    }

    for (const user of Object.keys(this.userExpanded)) {
      for (const question of this.responses) {
        const questionCopy: QuestionOutput = JSON.parse(JSON.stringify(question));
        questionCopy.allResponses = questionCopy.allResponses.filter((response: ResponseOutput) => {
          if (this.isGrq && user !== response.giver) {
            return false;
          }
          if (!this.isGrq && user !== response.recipient) {
            return false;
          }

          let shouldDisplayBasedOnSection: boolean = true;
          if (this.section) {
            switch (this.sectionType) {
              case InstructorSessionResultSectionType.EITHER:
                shouldDisplayBasedOnSection =
                    response.giverSection === this.section || response.recipientSection === this.section;
                break;
              case InstructorSessionResultSectionType.GIVER:
                shouldDisplayBasedOnSection = response.giverSection === this.section;
                break;
              case InstructorSessionResultSectionType.EVALUEE:
                shouldDisplayBasedOnSection = response.recipientSection === this.section;
                break;
              case InstructorSessionResultSectionType.BOTH:
                shouldDisplayBasedOnSection =
                    response.giverSection === this.section && response.recipientSection === this.section;
                break;
              default:
            }
          }
          if (!shouldDisplayBasedOnSection) {
            return false;
          }

          return true;
        });
        if (questionCopy.allResponses.length) {
          const others: string[] = questionCopy.allResponses.map((response: ResponseOutput) => {
            return this.isGrq ? response.recipient : response.giver;
          });
          for (const other of others) {
            const questionCopy2: QuestionOutput = JSON.parse(JSON.stringify(questionCopy));
            questionCopy2.allResponses = questionCopy2.allResponses.filter((response: ResponseOutput) => {
              return this.isGrq ? response.recipient === other : response.giver === other;
            });
            this.responsesToShow[user] = this.responsesToShow[user] || {};
            this.responsesToShow[user][other] = this.responsesToShow[user][other] || [];
            this.responsesToShow[user][other].push(questionCopy2);
          }
        }
      }
    }
  }
}
