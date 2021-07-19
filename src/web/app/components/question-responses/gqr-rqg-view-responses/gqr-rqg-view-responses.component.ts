import { Component, Input, OnChanges, OnInit } from '@angular/core';
import { FeedbackResponsesService } from '../../../../services/feedback-responses.service';
import {
  FeedbackParticipantType, FeedbackQuestionType,
  FeedbackSession, FeedbackSessionPublishStatus, FeedbackSessionSubmissionStatus,
  QuestionOutput, ResponseOutput,
  ResponseVisibleSetting,
  SessionVisibleSetting,
} from '../../../../types/api-output';
import {
  InstructorSessionResultSectionType,
} from '../../../pages-instructor/instructor-session-result-page/instructor-session-result-section-type.enum';
import { collapseAnim } from '../../teammates-common/collapse-anim';
import { InstructorResponsesViewBase } from '../instructor-responses-view-base';

interface QuestionTab {
  questionOutput: QuestionOutput;

  isTabExpanded: boolean;
}

/**
 * Component to display list of responses in GQR/RQG view.
 */
@Component({
  selector: 'tm-gqr-rqg-view-responses',
  templateUrl: './gqr-rqg-view-responses.component.html',
  styleUrls: ['./gqr-rqg-view-responses.component.scss'],
  animations: [collapseAnim],
})
export class GqrRqgViewResponsesComponent extends InstructorResponsesViewBase implements OnInit, OnChanges {

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

  @Input() isGqr: boolean = true;

  teamsToUsers: Record<string, string[]> = {};
  userToEmail: Record<string, string> = {};
  userToRelatedEmail: Record<string, string> = {};

  teamExpanded: Record<string, boolean> = {};
  userExpanded: Record<string, boolean> = {};
  userIsInstructor: Record<string, boolean> = {};

  responsesToShow: Record<string, QuestionTab[]> = {};
  teamsToQuestions: Record<string, QuestionOutput[]> = {};

  constructor(private feedbackResponsesService: FeedbackResponsesService) {
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
    this.teamsToQuestions = {};
    this.teamsToUsers = {};
    this.teamExpanded = {};
    this.userToEmail = {};
    this.userToRelatedEmail = {};
    this.userExpanded = {};
    this.userIsInstructor = {};
    for (const question of this.responses) {
      for (const response of question.allResponses) {
        if (!this.indicateMissingResponses && response.isMissingResponse) {
          // filter out missing responses
          continue;
        }

        if (this.sectionOfView) {
          if (this.isGqr && response.giverSection !== this.sectionOfView
              || !this.isGqr && response.recipientSection !== this.sectionOfView) {
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

        if (this.isGqr) {
          this.teamsToUsers[response.giverTeam] = this.teamsToUsers[response.giverTeam] || [];
          if (this.teamsToUsers[response.giverTeam].indexOf(response.giver) === -1) {
            this.teamsToUsers[response.giverTeam].push(response.giver);
            this.teamExpanded[response.giverTeam] = this.isExpandAll;
          }
          if (response.relatedGiverEmail) {
            this.userToRelatedEmail[response.giver] = response.relatedGiverEmail;
          }

          this.userExpanded[response.giver] = this.isExpandAll;
          this.userIsInstructor[response.giver] =
              question.feedbackQuestion.giverType === FeedbackParticipantType.INSTRUCTORS;
        } else {
          if (!response.recipientTeam) {
            // Recipient is team
            this.teamsToUsers[response.recipient] = this.teamsToUsers[response.recipient] || [];
            if (this.teamsToUsers[response.recipient].indexOf(response.recipient) === -1) {
              this.teamsToUsers[response.recipient].push(response.recipient);
              this.teamExpanded[response.recipient] = this.isExpandAll;
            }
            this.userExpanded[response.recipient] = this.isExpandAll;
            continue;
          }
          this.teamsToUsers[response.recipientTeam] = this.teamsToUsers[response.recipientTeam] || [];
          if (this.teamsToUsers[response.recipientTeam].indexOf(response.recipient) === -1) {
            this.teamsToUsers[response.recipientTeam].push(response.recipient);
            this.teamExpanded[response.recipientTeam] = this.isExpandAll;
          }
          this.userExpanded[response.recipient] = this.isExpandAll;
        }
      }
    }

    for (const user of Object.keys(this.userExpanded)) {
      for (const question of this.responses) {
        const questionCopy: QuestionOutput = JSON.parse(JSON.stringify(question));
        questionCopy.allResponses = questionCopy.allResponses.filter((response: ResponseOutput) => {
          if (!this.indicateMissingResponses && response.isMissingResponse) {
            // filter out missing responses
            return false;
          }
          if (this.isGqr && user !== response.giver) {
            return false;
          }
          if (!this.isGqr && user !== response.recipient) {
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
          this.responsesToShow[user] = this.responsesToShow[user] || [];
          this.responsesToShow[user].push({
            questionOutput: questionCopy,
            isTabExpanded: this.isExpandAll,
          });
        }
      }
    }

    for (const team of Object.keys(this.teamExpanded)) {
      for (const question of this.responses) {
        if (question.feedbackQuestion.questionType === FeedbackQuestionType.CONTRIB
            || question.feedbackQuestion.questionType === FeedbackQuestionType.TEXT) {
          // Should not display anything for contribution and text questions
          continue;
        }
        const questionCopy: QuestionOutput = JSON.parse(JSON.stringify(question));
        questionCopy.allResponses = questionCopy.allResponses.filter((response: ResponseOutput) => {
          if (response.isMissingResponse) {
            // Missing response is meaningless for team statistics
            return false;
          }
          if (this.isGqr && team !== response.giverTeam) {
            return false;
          }
          if (!this.isGqr && team !== response.recipientTeam) {
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
          this.teamsToQuestions[team] = this.teamsToQuestions[team] || [];
          this.teamsToQuestions[team].push(questionCopy);
        }
      }
    }
  }
}
