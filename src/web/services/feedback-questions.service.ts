import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { default as templateQuestions } from '../data/template-questions.json';
import {
  FeedbackMcqQuestionDetails,
  FeedbackParticipantType,
  FeedbackQuestion,
  FeedbackQuestionDetails,
  FeedbackQuestionType,
  FeedbackVisibilityType,
  NumberOfEntitiesToGiveFeedbackToSetting,
} from '../types/api-output';
import { FeedbackQuestionCreateRequest, FeedbackQuestionUpdateRequest } from '../types/api-request';
import {
  DEFAULT_CONTRIBUTION_QUESTION_DETAILS,
  DEFAULT_MCQ_QUESTION_DETAILS,
  DEFAULT_NUMSCALE_QUESTION_DETAILS,
  DEFAULT_TEXT_QUESTION_DETAILS,
} from '../types/default-question-structs';
import { VisibilityControl } from '../types/visibility-control';
import { HttpRequestService } from './http-request.service';
import { VisibilityStateMachine } from './visibility-state-machine';

/**
 * A template question.
 */
export interface TemplateQuestion {
  description: string;
  question: FeedbackQuestion;
}

/**
 * Handles feedback question logic provision.
 */
@Injectable({
  providedIn: 'root',
})
export class FeedbackQuestionsService {

  constructor(private httpRequestService: HttpRequestService) { }

  /**
   * Gets allowed feedback paths based on question type as some feedback paths does not make
   * sense under certain question.
   */
  getAllowedFeedbackPaths(type: FeedbackQuestionType): Map<FeedbackParticipantType, FeedbackParticipantType[]> {
    const paths: Map<FeedbackParticipantType, FeedbackParticipantType[]> = new Map();
    switch (type) {
      case FeedbackQuestionType.CONTRIB:
        paths.set(FeedbackParticipantType.STUDENTS, [FeedbackParticipantType.OWN_TEAM_MEMBERS_INCLUDING_SELF]);
        break;
      case FeedbackQuestionType.TEXT:
      case FeedbackQuestionType.MCQ:
      case FeedbackQuestionType.NUMSCALE:
        paths.set(FeedbackParticipantType.SELF,
          [FeedbackParticipantType.SELF, FeedbackParticipantType.STUDENTS, FeedbackParticipantType.INSTRUCTORS,
            FeedbackParticipantType.TEAMS, FeedbackParticipantType.OWN_TEAM, FeedbackParticipantType.NONE]);

        paths.set(FeedbackParticipantType.STUDENTS,
          [FeedbackParticipantType.SELF, FeedbackParticipantType.STUDENTS, FeedbackParticipantType.INSTRUCTORS,
            FeedbackParticipantType.TEAMS, FeedbackParticipantType.OWN_TEAM, FeedbackParticipantType.OWN_TEAM_MEMBERS,
            FeedbackParticipantType.OWN_TEAM_MEMBERS_INCLUDING_SELF, FeedbackParticipantType.NONE]);

        paths.set(FeedbackParticipantType.INSTRUCTORS,
          [FeedbackParticipantType.SELF, FeedbackParticipantType.STUDENTS, FeedbackParticipantType.INSTRUCTORS,
            FeedbackParticipantType.TEAMS, FeedbackParticipantType.OWN_TEAM, FeedbackParticipantType.NONE]);

        paths.set(FeedbackParticipantType.TEAMS,
          [FeedbackParticipantType.SELF, FeedbackParticipantType.STUDENTS, FeedbackParticipantType.INSTRUCTORS,
            FeedbackParticipantType.TEAMS, FeedbackParticipantType.OWN_TEAM_MEMBERS_INCLUDING_SELF,
            FeedbackParticipantType.NONE]);
        break;
      default:
    }
    return paths;
  }

  /**
   * Gets common feedback paths based on question type.
   */
  getCommonFeedbackPaths(type: FeedbackQuestionType): Map<FeedbackParticipantType, FeedbackParticipantType[]> {
    const paths: Map<FeedbackParticipantType, FeedbackParticipantType[]> = new Map();
    switch (type) {
      case FeedbackQuestionType.CONTRIB:
        paths.set(FeedbackParticipantType.STUDENTS, [FeedbackParticipantType.OWN_TEAM_MEMBERS_INCLUDING_SELF]);
        break;
      case FeedbackQuestionType.TEXT:
      case FeedbackQuestionType.MCQ:
      case FeedbackQuestionType.NUMSCALE:
        paths.set(FeedbackParticipantType.SELF,
            [FeedbackParticipantType.NONE, FeedbackParticipantType.SELF, FeedbackParticipantType.INSTRUCTORS]);
        paths.set(FeedbackParticipantType.STUDENTS,
          [FeedbackParticipantType.NONE, FeedbackParticipantType.SELF, FeedbackParticipantType.INSTRUCTORS,
            FeedbackParticipantType.OWN_TEAM_MEMBERS, FeedbackParticipantType.OWN_TEAM_MEMBERS_INCLUDING_SELF]);
        paths.set(FeedbackParticipantType.INSTRUCTORS,
            [FeedbackParticipantType.NONE, FeedbackParticipantType.SELF, FeedbackParticipantType.INSTRUCTORS]);
        break;
      default:
    }
    return paths;
  }

  /**
   * Gets a state machine of visibility settings under {@code giverType} and {@code recipientType}.
   */
  getNewVisibilityStateMachine(giverType: FeedbackParticipantType,
                               recipientType: FeedbackParticipantType): VisibilityStateMachine {
    return new VisibilityStateMachine(giverType, recipientType);
  }

  /**
   * Gets common feedback visibility settings under a feedback question type.
   *
   * @param visibilityStateMachine the state machine with current giverType and recipientType.
   * @param type the feedback question type.
   */
  getCommonFeedbackVisibilitySettings(visibilityStateMachine: VisibilityStateMachine,
                                      type: FeedbackQuestionType): CommonVisibilitySetting[] {
    let settings: CommonVisibilitySetting[] = [];
    switch (type) {
      case FeedbackQuestionType.CONTRIB:
        settings.push({
          name: "Shown anonymously to recipient and giver's team members, visible to instructors",
          visibilitySettings: {
            SHOW_RESPONSE: [FeedbackVisibilityType.INSTRUCTORS, FeedbackVisibilityType.RECIPIENT,
              FeedbackVisibilityType.GIVER_TEAM_MEMBERS],
            SHOW_GIVER_NAME: [FeedbackVisibilityType.INSTRUCTORS],
            SHOW_RECIPIENT_NAME: [FeedbackVisibilityType.INSTRUCTORS, FeedbackVisibilityType.RECIPIENT],
          },
        }, {
          name: 'Visible to instructors only',
          visibilitySettings: {
            SHOW_RESPONSE: [FeedbackVisibilityType.INSTRUCTORS],
            SHOW_GIVER_NAME: [FeedbackVisibilityType.INSTRUCTORS],
            SHOW_RECIPIENT_NAME: [FeedbackVisibilityType.INSTRUCTORS],
          },
        });
        break;
      case FeedbackQuestionType.TEXT:
      case FeedbackQuestionType.MCQ:
      case FeedbackQuestionType.NUMSCALE:
        settings.push({
          name: 'Shown anonymously to recipient and instructors',
          visibilitySettings: {
            SHOW_RESPONSE: [FeedbackVisibilityType.INSTRUCTORS, FeedbackVisibilityType.RECIPIENT],
            SHOW_GIVER_NAME: [],
            SHOW_RECIPIENT_NAME: [FeedbackVisibilityType.INSTRUCTORS, FeedbackVisibilityType.RECIPIENT],
          },
        }, {
          name: 'Shown anonymously to recipient, visible to instructors',
          visibilitySettings: {
            SHOW_RESPONSE: [FeedbackVisibilityType.INSTRUCTORS, FeedbackVisibilityType.RECIPIENT],
            SHOW_GIVER_NAME: [FeedbackVisibilityType.INSTRUCTORS],
            SHOW_RECIPIENT_NAME: [FeedbackVisibilityType.INSTRUCTORS, FeedbackVisibilityType.RECIPIENT],
          },
        }, {
          name: "Shown anonymously to recipient and giver/recipient's team members, visible to instructors",
          visibilitySettings: {
            SHOW_RESPONSE: [FeedbackVisibilityType.INSTRUCTORS, FeedbackVisibilityType.RECIPIENT,
              FeedbackVisibilityType.GIVER_TEAM_MEMBERS, FeedbackVisibilityType.RECIPIENT_TEAM_MEMBERS,
            ],
            SHOW_GIVER_NAME: [FeedbackVisibilityType.INSTRUCTORS],
            SHOW_RECIPIENT_NAME: [FeedbackVisibilityType.INSTRUCTORS, FeedbackVisibilityType.RECIPIENT],
          },
        }, {
          name: "Shown anonymously to recipient and giver's team members, visible to instructors",
          visibilitySettings: {
            SHOW_RESPONSE: [FeedbackVisibilityType.INSTRUCTORS, FeedbackVisibilityType.RECIPIENT,
              FeedbackVisibilityType.GIVER_TEAM_MEMBERS,
            ],
            SHOW_GIVER_NAME: [FeedbackVisibilityType.INSTRUCTORS],
            SHOW_RECIPIENT_NAME: [FeedbackVisibilityType.INSTRUCTORS, FeedbackVisibilityType.RECIPIENT],
          },
        }, {
          name: 'Visible to instructors only',
          visibilitySettings: {
            SHOW_RESPONSE: [FeedbackVisibilityType.INSTRUCTORS],
            SHOW_GIVER_NAME: [FeedbackVisibilityType.INSTRUCTORS],
            SHOW_RECIPIENT_NAME: [FeedbackVisibilityType.INSTRUCTORS],
          },
        }, {
          name: 'Visible to recipient and instructors',
          visibilitySettings: {
            SHOW_RESPONSE: [FeedbackVisibilityType.INSTRUCTORS, FeedbackVisibilityType.RECIPIENT],
            SHOW_GIVER_NAME: [FeedbackVisibilityType.INSTRUCTORS, FeedbackVisibilityType.RECIPIENT],
            SHOW_RECIPIENT_NAME: [FeedbackVisibilityType.INSTRUCTORS, FeedbackVisibilityType.RECIPIENT],
          },
        });
        break;
      default:
    }

    // filter common settings based on visibility state
    // (i.e. some common settings does not make sense under certain state)
    settings = settings.filter((setting: CommonVisibilitySetting) => {
      for (const visibilityType of setting.visibilitySettings.SHOW_RESPONSE) {
        if (!visibilityStateMachine.isVisibilityTypeApplicable(visibilityType)) {
          return false;
        }
      }
      for (const visibilityType of setting.visibilitySettings.SHOW_GIVER_NAME) {
        if (!visibilityStateMachine.isVisibilityTypeApplicable(visibilityType)) {
          return false;
        }
      }
      for (const visibilityType of setting.visibilitySettings.SHOW_RECIPIENT_NAME) {
        if (!visibilityStateMachine.isVisibilityTypeApplicable(visibilityType)) {
          return false;
        }
      }
      return true;
    });

    return settings;
  }

  /**
   * Returns whether setting custom feedback visibility is allowed.
   */
  isCustomFeedbackVisibilitySettingAllowed(type: FeedbackQuestionType): boolean {
    switch (type) {
      case FeedbackQuestionType.TEXT:
        return true;
      case FeedbackQuestionType.CONTRIB:
        return false;
      case FeedbackQuestionType.MCQ:
        return true;
      case FeedbackQuestionType.NUMSCALE:
        return true;
      default:
        throw new Error(`Unsupported question type: ${type}`);
    }
  }

  /**
   * Gets the model (contains default values) for new question.
   */
  getNewQuestionModel(type: FeedbackQuestionType): NewQuestionModel {
    switch (type) {
      case FeedbackQuestionType.TEXT:
        return {
          questionBrief: '',
          questionDescription: '',

          questionType: FeedbackQuestionType.TEXT,
          questionDetails: DEFAULT_TEXT_QUESTION_DETAILS(),

          giverType: FeedbackParticipantType.STUDENTS,
          recipientType: FeedbackParticipantType.OWN_TEAM_MEMBERS,

          numberOfEntitiesToGiveFeedbackToSetting: NumberOfEntitiesToGiveFeedbackToSetting.UNLIMITED,

          showResponsesTo: [FeedbackVisibilityType.INSTRUCTORS, FeedbackVisibilityType.RECIPIENT],
          showGiverNameTo: [FeedbackVisibilityType.INSTRUCTORS],
          showRecipientNameTo: [FeedbackVisibilityType.INSTRUCTORS, FeedbackVisibilityType.RECIPIENT],
        };
      case FeedbackQuestionType.CONTRIB:
        return {
          questionBrief: '',
          questionDescription: '',

          questionType: FeedbackQuestionType.CONTRIB,
          questionDetails: DEFAULT_CONTRIBUTION_QUESTION_DETAILS(),

          giverType: FeedbackParticipantType.STUDENTS,
          recipientType: FeedbackParticipantType.OWN_TEAM_MEMBERS_INCLUDING_SELF,

          numberOfEntitiesToGiveFeedbackToSetting: NumberOfEntitiesToGiveFeedbackToSetting.UNLIMITED,

          showResponsesTo: [FeedbackVisibilityType.INSTRUCTORS, FeedbackVisibilityType.RECIPIENT,
            FeedbackVisibilityType.GIVER_TEAM_MEMBERS],
          showGiverNameTo: [FeedbackVisibilityType.INSTRUCTORS],
          showRecipientNameTo: [FeedbackVisibilityType.INSTRUCTORS, FeedbackVisibilityType.RECIPIENT],
        };

      case FeedbackQuestionType.NUMSCALE:

        return {
          questionBrief: '',
          questionDescription: '',

          questionType: FeedbackQuestionType.NUMSCALE,
          questionDetails: DEFAULT_NUMSCALE_QUESTION_DETAILS(),
          giverType: FeedbackParticipantType.STUDENTS,
          recipientType: FeedbackParticipantType.OWN_TEAM_MEMBERS,

          numberOfEntitiesToGiveFeedbackToSetting: NumberOfEntitiesToGiveFeedbackToSetting.UNLIMITED,

          showResponsesTo: [FeedbackVisibilityType.INSTRUCTORS, FeedbackVisibilityType.RECIPIENT],
          showGiverNameTo: [FeedbackVisibilityType.INSTRUCTORS],
          showRecipientNameTo: [FeedbackVisibilityType.INSTRUCTORS, FeedbackVisibilityType.RECIPIENT],
        };

      case FeedbackQuestionType.MCQ:

        const mcqQuestionDetails: FeedbackMcqQuestionDetails = DEFAULT_MCQ_QUESTION_DETAILS();
        mcqQuestionDetails.numOfMcqChoices = 2;
        mcqQuestionDetails.mcqChoices = [' ', ' '];

        return {
          questionBrief: '',
          questionDescription: '',

          questionType: FeedbackQuestionType.MCQ,
          questionDetails: mcqQuestionDetails,
          giverType: FeedbackParticipantType.STUDENTS,
          recipientType: FeedbackParticipantType.OWN_TEAM_MEMBERS,

          numberOfEntitiesToGiveFeedbackToSetting: NumberOfEntitiesToGiveFeedbackToSetting.UNLIMITED,

          showResponsesTo: [FeedbackVisibilityType.INSTRUCTORS, FeedbackVisibilityType.RECIPIENT,
            FeedbackVisibilityType.GIVER_TEAM_MEMBERS],
          showGiverNameTo: [FeedbackVisibilityType.INSTRUCTORS],
          showRecipientNameTo: [FeedbackVisibilityType.INSTRUCTORS, FeedbackVisibilityType.RECIPIENT],
        };
      default:
        throw new Error(`Unsupported question type ${type}`);
    }
  }

  /**
   * Gets template questions.
   */
  getTemplateQuestions(): TemplateQuestion[] {
    return templateQuestions;
  }

  /**
   * Creates a feedback question by calling API.
   */
  createFeedbackQuestion(courseId: string, feedbackSessionName: string,
                         request: FeedbackQuestionCreateRequest): Observable<FeedbackQuestion> {
    const paramMap: { [key: string]: string } = {
      courseid: courseId,
      fsname: feedbackSessionName,
    };

    return this.httpRequestService.post('/question', paramMap, request);
  }

  /**
   * Saves a feedback question by calling API.
   */
  saveFeedbackQuestion(feedbackQuestionId: string, request: FeedbackQuestionUpdateRequest):
      Observable<FeedbackQuestion> {
    const paramMap: { [key: string]: string } = { questionid: feedbackQuestionId };

    return this.httpRequestService.put('/question', paramMap, request);
  }

}

/**
 * Type represents the common visibility setting.
 */
export interface CommonVisibilitySetting {
  name: string;
  visibilitySettings: {[TKey in VisibilityControl]: FeedbackVisibilityType[]};
}

/**
 * The model for new question.
 */
export interface NewQuestionModel {
  questionBrief: string;
  questionDescription: string;

  questionType: FeedbackQuestionType;
  questionDetails: FeedbackQuestionDetails;

  giverType: FeedbackParticipantType;
  recipientType: FeedbackParticipantType;

  numberOfEntitiesToGiveFeedbackToSetting: NumberOfEntitiesToGiveFeedbackToSetting;
  customNumberOfEntitiesToGiveFeedbackTo?: number;

  showResponsesTo: FeedbackVisibilityType[];
  showGiverNameTo: FeedbackVisibilityType[];
  showRecipientNameTo: FeedbackVisibilityType[];
}
