import { NgTemplateOutlet, KeyValuePipe } from '@angular/common';
import { Component, Input, OnChanges, OnInit, inject } from '@angular/core';
import { FeedbackResponsesService } from '../../../../services/feedback-responses.service';
import {
  FeedbackSession,
  FeedbackSessionPublishStatus,
  FeedbackSessionSubmissionStatus,
  QuestionGiverType,
  QuestionOutput,
  ResponseOutput,
  ResponseVisibleSetting,
  SessionVisibleSetting,
} from '../../../../types/api-output';
import { InstructorSessionResultSectionType } from '../../../pages-instructor/instructor-session-result-page/instructor-session-result-section-type.enum';
import { DEFAULT_SECTION_ID } from '../../../pages-instructor/instructor-session-result-page/instructor-session-tab.model';
import { ResponseModerationButtonComponent } from '../../../pages-instructor/instructor-session-result-page/response-moderation-button/response-moderation-button.component';
import { PanelChevronComponent } from '../../panel-chevron/panel-chevron.component';
import { GroupedResponsesComponent } from '../grouped-responses/grouped-responses.component';
import { InstructorResponsesViewBase } from '../instructor-responses-view-base';
import { NgbCollapse } from '@ng-bootstrap/ng-bootstrap/collapse';

/**
 * Component to display list of responses in GRQ/RGQ view.
 */
@Component({
  selector: 'tm-grq-rgq-view-responses',
  templateUrl: './grq-rgq-view-responses.component.html',
  styleUrls: ['./grq-rgq-view-responses.component.scss'],
  imports: [
    NgbCollapse,
    PanelChevronComponent,
    NgTemplateOutlet,
    ResponseModerationButtonComponent,
    GroupedResponsesComponent,
    KeyValuePipe,
  ],
})
export class GrqRgqViewResponsesComponent extends InstructorResponsesViewBase implements OnInit, OnChanges {
  private feedbackResponsesService = inject(FeedbackResponsesService);

  @Input() responses: QuestionOutput[] = [];
  @Input() sectionOfView = '';
  @Input() section = '';
  @Input() sectionType: InstructorSessionResultSectionType = InstructorSessionResultSectionType.EITHER;
  @Input() groupByTeam = true;
  @Input() showStatistics = true;
  @Input() indicateMissingResponses = true;
  @Input() session: FeedbackSession = {
    feedbackSessionId: '',
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
    isClosingSoonEmailEnabled: true,
    isPublishedEmailEnabled: true,
    createdAtTimestamp: 0,
  };

  @Input() isGrq = true;

  teamsToUsers: Record<string, string[]> = {};
  usersToTeams: Record<string, string> = {};
  userToEmail: Record<string, string> = {};
  userToUserIdForModeration: Record<string, string> = {};

  teamExpanded: Record<string, boolean> = {};
  userExpanded: Record<string, boolean> = {};
  userIsInstructor: Record<string, boolean> = {};

  responsesToShow: Record<string, Record<string, QuestionOutput[]>> = {};
  userHasRealResponses: Record<string, boolean> = {};

  ngOnInit(): void {
    this.filterResponses();
  }

  ngOnChanges(): void {
    this.filterResponses();
  }

  private filterResponses(): void {
    this.responsesToShow = {};
    this.userHasRealResponses = {};
    this.teamsToUsers = {};
    this.usersToTeams = {};
    this.userToEmail = {};
    this.userToUserIdForModeration = {};
    this.teamExpanded = {};
    this.userExpanded = {};
    for (const question of this.responses) {
      for (const response of question.allResponses) {
        if (!this.indicateMissingResponses && response.isMissingResponse) {
          // filter out missing responses
          continue;
        }

        if (this.sectionOfView) {
          if (
            (this.isGrq && !this.isResponseSection(response.giverSectionId, this.sectionOfView)) ||
            (!this.isGrq && !this.isResponseSection(response.recipientSectionId, this.sectionOfView))
          ) {
            continue;
          }
        }
        const shouldDisplayBasedOnSection: boolean =
          this.feedbackResponsesService.isFeedbackResponsesDisplayedOnSection(response, this.section, this.sectionType);
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
          if (!this.teamsToUsers[response.giverTeam].includes(response.giver)) {
            this.teamsToUsers[response.giverTeam].push(response.giver);
            this.usersToTeams[response.giver] = response.giverTeam;
            this.teamExpanded[response.giverTeam] = this.isExpandAll;
          }
          if (response.userIdForModeration) {
            this.userToUserIdForModeration[response.giver] = response.userIdForModeration;
          }
          this.userExpanded[response.giver] = this.isExpandAll;
          this.userIsInstructor[response.giver] = question.feedbackQuestion.giverType === QuestionGiverType.INSTRUCTORS;
        } else {
          this.usersToTeams[response.recipient] = this.usersToTeams[response.recipient] || '';
          this.userExpanded[response.recipient] = this.isExpandAll;
          if (response.recipientTeam) {
            this.teamsToUsers[response.recipientTeam] = this.teamsToUsers[response.recipientTeam] || [];
            if (!this.teamsToUsers[response.recipientTeam].includes(response.recipient)) {
              this.teamsToUsers[response.recipientTeam].push(response.recipient);
              this.usersToTeams[response.recipient] = response.recipientTeam;
              this.teamExpanded[response.recipientTeam] = this.isExpandAll;
            }
          } else {
            // Recipient is team
            this.teamsToUsers[response.recipient] = this.teamsToUsers[response.recipient] || [];
            if (!this.teamsToUsers[response.recipient].includes(response.recipient)) {
              this.teamsToUsers[response.recipient].push(response.recipient);
              this.teamExpanded[response.recipient] = this.isExpandAll;
            }
          }
        }
      }
    }

    for (const user of Object.keys(this.userExpanded)) {
      this.userHasRealResponses[user] = false;

      for (const question of this.responses) {
        const questionCopy: QuestionOutput = structuredClone(question);
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

          const shouldDisplayBasedOnSection: boolean =
            this.feedbackResponsesService.isFeedbackResponsesDisplayedOnSection(
              response,
              this.section,
              this.sectionType,
            );

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
            const questionCopy2: QuestionOutput = structuredClone(questionCopy);
            questionCopy2.allResponses = questionCopy2.allResponses.filter((response: ResponseOutput) => {
              return this.isGrq ? response.recipient === other : response.giver === other;
            });
            this.responsesToShow[user] = this.responsesToShow[user] || {};
            this.responsesToShow[user][other] = this.responsesToShow[user][other] || [];
            this.responsesToShow[user][other].push(questionCopy2);

            if (!this.userHasRealResponses[user]) {
              this.userHasRealResponses[user] = questionCopy2.allResponses.some(
                (response: ResponseOutput) => !response.isMissingResponse,
              );
            }
          }
        }
      }
    }
  }

  private isResponseSection(responseSectionId: string | null | undefined, sectionId: string): boolean {
    return sectionId === DEFAULT_SECTION_ID ? !responseSectionId : responseSectionId === sectionId;
  }
}
