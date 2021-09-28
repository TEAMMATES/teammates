import { Component, Input, OnInit } from '@angular/core';
import {
  FeedbackParticipantType,
  FeedbackQuestionType,
  FeedbackTextQuestionDetails,
  FeedbackVisibilityType,
  NumberOfEntitiesToGiveFeedbackToSetting,
} from '../../../types/api-output';
import { CommonVisibilitySetting } from '../../../services/feedback-questions.service';
import { QuestionEditFormModel } from '../question-edit-form/question-edit-form-model';
import { VisibilityControl } from '../../../types/visibility-control';
//import { VisibilityStateMachine } from '../../../services/visibility-state-machine';

@Component({
  selector: 'tm-visibility-panel',
  templateUrl: './visibility-panel.component.html',
  styleUrls: ['./visibility-panel.component.scss']
})
export class VisibilityPanelComponent implements OnInit {

  //enum
  FeedbackParticipantType: typeof FeedbackParticipantType = FeedbackParticipantType;
  FeedbackQuestionType: typeof FeedbackQuestionType = FeedbackQuestionType;
  NumberOfEntitiesToGiveFeedbackToSetting: typeof NumberOfEntitiesToGiveFeedbackToSetting =
        NumberOfEntitiesToGiveFeedbackToSetting;
  VisibilityControl: typeof VisibilityControl = VisibilityControl;
  FeedbackVisibilityType: typeof FeedbackVisibilityType = FeedbackVisibilityType;
  
  @Input()
  isCustomFeedbackVisibilitySettingAllowed: boolean = false;
  
  @Input()
  model: QuestionEditFormModel = {
                                     feedbackQuestionId: '',

                                     questionNumber: 0,
                                     questionBrief: '',
                                     questionDescription: '',

                                     isQuestionHasResponses: false,

                                     questionType: FeedbackQuestionType.TEXT,
                                     questionDetails: {
                                       questionType: FeedbackQuestionType.TEXT,
                                       questionText: '',
                                     } as FeedbackTextQuestionDetails,

                                     giverType: FeedbackParticipantType.STUDENTS,
                                     recipientType: FeedbackParticipantType.STUDENTS,

                                     numberOfEntitiesToGiveFeedbackToSetting: NumberOfEntitiesToGiveFeedbackToSetting.UNLIMITED,
                                     customNumberOfEntitiesToGiveFeedbackTo: 1,

                                     showResponsesTo: [],
                                     showGiverNameTo: [],
                                     showRecipientNameTo: [],

                                     commonVisibilitySettingName: '',

                                     isUsingOtherFeedbackPath: false,
                                     isUsingOtherVisibilitySetting: false,
                                     isDeleting: false,
                                     isDuplicating: false,
                                     isEditable: false,
                                     isSaving: false,
                                     isCollapsed: false,
                                     isVisibilityChanged: false,
                                     isFeedbackPathChanged: false,
                                     isQuestionDetailsChanged: false,
                                   };
  
  @Input()
  VisibilityControls: VisibilityControl = VisibilityControl.SHOW_RESPONSE
  
  @Input()
  FeedbackVisibilityTypes: FeedbackVisibilityType = FeedbackVisibilityType.STUDENTS
  
  @Input()
  commonFeedbackVisibilitySettings: CommonVisibilitySetting[] = [];
  
  //@Input()
  //visibilityStateMachine: VisibilityStateMachine;
    
  constructor() { }

  ngOnInit(): void {
  }
  

}
