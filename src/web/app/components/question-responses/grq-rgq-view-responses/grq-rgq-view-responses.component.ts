import { KeyValue } from '@angular/common';
import { Component, Input, OnChanges, OnInit } from '@angular/core';
import { FeedbackResponsesService } from '../../../../services/feedback-responses.service';
import {
  FeedbackParticipantType,
  FeedbackSession,
  FeedbackSessionPublishStatus,
  FeedbackSessionSubmissionStatus, QuestionOutput, ResponseOutput,
  ResponseVisibleSetting,
  SessionVisibleSetting,
} from '../../../../types/api-output';
import {
  InstructorSessionResultSectionType,
} from '../../../pages-instructor/instructor-session-result-page/instructor-session-result-section-type.enum';
import { collapseAnim } from '../../teammates-common/collapse-anim';
import { InstructorResponsesViewBase } from '../instructor-responses-view-base';

/**
 * Component to display list of responses in GRQ/RGQ view.
 */
@Component({
  selector: 'tm-grq-rgq-view-responses',
  templateUrl: './grq-rgq-view-responses.component.html',
  styleUrls: ['./grq-rgq-view-responses.component.scss'],
  animations: [collapseAnim],
})
export class GrqRgqViewResponsesComponent extends InstructorResponsesViewBase implements OnInit, OnChanges {

  @Input() responses: QuestionOutput[] = [];
  @Input() sectionOfView: string = '';
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
  userToRelatedEmail: Record<string, string> = {};

  teamExpanded: Record<string, boolean> = {};
  userExpanded: Record<string, boolean> = {};
  userIsInstructor: Record<string, boolean> = {};

  responsesToShow: Record<string, Record<string, QuestionOutput[]>> = {};
  userHasRealResponses: Record<string, boolean> = {};

  constructor(private feedbackResponsesService: FeedbackResponsesService) {
    super();
  }

  ngOnInit(): void {
    this.filterResponses();
  }

  ngOnChanges(): void {
    this.filterResponses();
  }

  trackByName(_: number, keyVal: KeyValue<string, boolean>): string {
    return keyVal.key;
  }

  private filterResponses(): void {
    this.responsesToShow = {};
    this.userHasRealResponses = {};
    this.teamsToUsers = {};
    this.usersToTeams = {};
    this.userToEmail = {};
    this.userToRelatedEmail = {};
    this.teamExpanded = {};
    this.userExpanded = {};
    for (const question of this.responses) {
      for (const response of question.allResponses) {
        if (!this.indicateMissingResponses && response.isMissingResponse) {
          // filter out missing responses
          continue;
        }

        if (this.sectionOfView) {
          if (this.isGrq && response.giverSection !== this.sectionOfView
              || !this.isGrq && response.recipientSection !== this.sectionOfView) {
            continue;
          }
        }
        const shouldDisplayBasedOnSection: boolean = this.feedbackResponsesService
            .isFeedbackResponsesDisplayedOnSection(response, this.section, this.sectionType);
        if (!shouldDisplayBasedOnSection) {
          continue;
        }

        if (response.giverEmail) {
          this.userToEmail[response.giver] = response.giverEmail;
        }
        if (response.recipientEmail) {
          this.userToEmail[response.recipient] = response.recipientEmail;
        }

        if (this.isGrq) {
          this.teamsToUsers[response.giverTeam] = this.teamsToUsers[response.giverTeam] || [];
          this.usersToTeams[response.giver] = this.usersToTeams[response.giver] || '';
          if (this.teamsToUsers[response.giverTeam].indexOf(response.giver) === -1) {
            this.teamsToUsers[response.giverTeam].push(response.giver);
            this.usersToTeams[response.giver] = response.giverTeam;
            this.teamExpanded[response.giverTeam] = this.isExpandAll;
          }
          if (response.relatedGiverEmail) {
            this.userToRelatedEmail[response.giver] = response.relatedGiverEmail;
          }
          this.userExpanded[response.giver] = this.isExpandAll;
          this.userIsInstructor[response.giver] =
              question.feedbackQuestion.giverType === FeedbackParticipantType.INSTRUCTORS;
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
        }
      }
    }

    for (const user of Object.keys(this.userExpanded)) {
      this.userHasRealResponses[user] = false;

      for (const question of this.responses) {
        const questionCopy: QuestionOutput = JSON.parse(JSON.stringify(question));
        questionCopy.allResponses = questionCopy.allResponses.filter((response: ResponseOutput) => {
          if (!this.indicateMissingResponses && response.isMissingResponse) {
            // filter out missing responses
            return false;
          }
          if (this.isGrq && user !== response.giver) {
            return false;
          }
          if (!this.isGrq && user !== response.recipient) {
            return false;
          }

          const shouldDisplayBasedOnSection: boolean = this.feedbackResponsesService
            .isFeedbackResponsesDisplayedOnSection(response, this.section, this.sectionType);

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

            if (!this.userHasRealResponses[user]) {
              this.userHasRealResponses[user] =
                  questionCopy2.allResponses.some((response: ResponseOutput) => !response.isMissingResponse);
            }
          }
        }
      }
    }
  }
}
