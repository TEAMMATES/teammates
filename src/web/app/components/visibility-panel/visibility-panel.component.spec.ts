import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { NgbDropdownModule, NgbTooltipModule } from '@ng-bootstrap/ng-bootstrap';

import { VisibilityPanelComponent } from './visibility-panel.component';
import { CommonVisibilitySetting } from '../../../services/feedback-questions.service';
import { FeedbackVisibilityType } from '../../../types/api-output';
import { VisibilityControl } from '../../../types/visibility-control';
import { TeammatesCommonModule } from '../teammates-common/teammates-common.module';
import { VisibilityMessagesModule } from '../visibility-messages/visibility-messages.module';

describe('VisibilityPanelComponent', () => {
  let component: VisibilityPanelComponent;
  let fixture: ComponentFixture<VisibilityPanelComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [
        VisibilityPanelComponent,
      ],
      imports: [
        FormsModule,
        NgbDropdownModule,
        NgbTooltipModule,
        TeammatesCommonModule,
        VisibilityMessagesModule,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(VisibilityPanelComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('triggerCustomVisibilitySetting: should emit true from customVisibilitySetting', () => {
    const customVisibilitySettingSpy = jest.spyOn(component.customVisibilitySetting, 'emit');

    component.triggerCustomVisibilitySetting();

    expect(customVisibilitySettingSpy).toHaveBeenCalledWith(true);
  });

  it('getCheckboxAriaLabel: should return the string \'Recipient(s) can see Answer\' when'
  + ' visibilityType is RECIPIENT and visibilityControl is SHOW_RESPONSE', () => {
    const visibilityType = FeedbackVisibilityType.RECIPIENT;
    const visibilityTypeAriaLabels = 'Recipient(s)';
    const visibilityControl = VisibilityControl.SHOW_RESPONSE;
    const visibilityControlAriaLabels = 'Answer';

    const ariaLabel = component.getCheckboxAriaLabel(visibilityType, visibilityControl);

    expect(ariaLabel).toBe(`${visibilityTypeAriaLabels} can see ${visibilityControlAriaLabels}`);
  });

  it('applyCommonVisibilitySettings: should trigger model change with'
  + ' isUsingOtherVisibilitySetting as false and CommonVisibilitySetting', () => {
    const testSettings: CommonVisibilitySetting = {
      name: 'testSettings name',

      visibilitySettings: {
        SHOW_RESPONSE: [FeedbackVisibilityType.RECIPIENT],
        SHOW_GIVER_NAME: [FeedbackVisibilityType.RECIPIENT],
        SHOW_RECIPIENT_NAME: [FeedbackVisibilityType.RECIPIENT],
      },
    };

    const triggerModelChangeBatchSpy = jest.spyOn(component.triggerModelChangeBatch, 'emit');

    component.applyCommonVisibilitySettings(testSettings);

    expect(triggerModelChangeBatchSpy).toHaveBeenCalledWith({
      showResponsesTo: testSettings.visibilitySettings.SHOW_RESPONSE,
      showGiverNameTo: testSettings.visibilitySettings.SHOW_GIVER_NAME,
      showRecipientNameTo: testSettings.visibilitySettings.SHOW_RECIPIENT_NAME,
      commonVisibilitySettingName: testSettings.name,
      isUsingOtherVisibilitySetting: false,
    });
  });

  it('modifyVisibilityControl: should only call allowToSee and emit the updated visibilityStateMachine', () => {
    const visibilityType = FeedbackVisibilityType.RECIPIENT;
    const visibilityControl = VisibilityControl.SHOW_RESPONSE;

    const allowToSeeSpy = jest.spyOn(component.visibilityStateMachine, 'allowToSee');
    const disallowToSeeSpy = jest.spyOn(component.visibilityStateMachine, 'disallowToSee');
    const visibilityStateMachineChangeSpy = jest.spyOn(component.visibilityStateMachineChange, 'emit');

    component.modifyVisibilityControl(true, visibilityType, visibilityControl);

    expect(allowToSeeSpy).toHaveBeenCalledWith(visibilityType, visibilityControl);
    expect(disallowToSeeSpy).not.toHaveBeenCalledWith();
    expect(visibilityStateMachineChangeSpy).toHaveBeenCalledWith(component.visibilityStateMachine);
  });

  it('modifyVisibilityControl: should only call disallowToSee and emit the updated visibilityStateMachine', () => {
    const visibilityType = FeedbackVisibilityType.RECIPIENT;
    const visibilityControl = VisibilityControl.SHOW_RESPONSE;

    const allowToSeeSpy = jest.spyOn(component.visibilityStateMachine, 'allowToSee');
    const disallowToSeeSpy = jest.spyOn(component.visibilityStateMachine, 'disallowToSee');
    const visibilityStateMachineChangeSpy = jest.spyOn(component.visibilityStateMachineChange, 'emit');

    component.modifyVisibilityControl(false, visibilityType, visibilityControl);

    expect(allowToSeeSpy).not.toHaveBeenCalledWith();
    expect(disallowToSeeSpy).toHaveBeenCalledWith(visibilityType, visibilityControl);
    expect(visibilityStateMachineChangeSpy).toHaveBeenCalledWith(component.visibilityStateMachine);
  });

  // RECIPIENT and SHOW_RESPONSE will also make the recipient name visible
  it('modifyVisibilityControl: should trigger model change correctly when'
  + ' isAllowed is true, visibilityType is RECIPIENT and visibilityControl is SHOW_RESPONSE', () => {
    const triggerModelChangeBatchSpy = jest.spyOn(component.triggerModelChangeBatch, 'emit');

    component.modifyVisibilityControl(true, FeedbackVisibilityType.RECIPIENT, VisibilityControl.SHOW_RESPONSE);

    expect(triggerModelChangeBatchSpy).toHaveBeenCalledWith({
      showResponsesTo: [FeedbackVisibilityType.RECIPIENT],
      showGiverNameTo: [],
      showRecipientNameTo: [FeedbackVisibilityType.RECIPIENT],
    });
  });

  it('modifyVisibilityControl: should trigger model change correctly when'
  + ' isAllowed is true, visibilityType is NOT RECIPIENT and visibilityControl is SHOW_RESPONSE', () => {
    const visibilityType = FeedbackVisibilityType.GIVER_TEAM_MEMBERS;

    const triggerModelChangeBatchSpy = jest.spyOn(component.triggerModelChangeBatch, 'emit');

    component.modifyVisibilityControl(true, visibilityType, VisibilityControl.SHOW_RESPONSE);

    expect(triggerModelChangeBatchSpy).toHaveBeenCalledWith({
      showResponsesTo: [visibilityType],
      showGiverNameTo: [],
      showRecipientNameTo: [],
    });
  });

  it('modifyVisibilityControl: should trigger model change correctly when'
  + ' isAllowed is true, visibilityType is ANY and visibilityControl is SHOW_GIVER_NAME', () => {
    const visibilityType = FeedbackVisibilityType.RECIPIENT;

    const triggerModelChangeBatchSpy = jest.spyOn(component.triggerModelChangeBatch, 'emit');

    component.modifyVisibilityControl(true, visibilityType, VisibilityControl.SHOW_GIVER_NAME);

    expect(triggerModelChangeBatchSpy).toHaveBeenCalledWith({
      showResponsesTo: [visibilityType],
      showGiverNameTo: [visibilityType],
      showRecipientNameTo: [],
    });
  });

  // recipients' show recipient name cannot be edited
  it('modifyVisibilityControl: should trigger model change correctly when'
  + ' isAllowed is true, visibilityType is RECIPIENT and visibilityControl is SHOW_RECIPIENT_NAME', () => {
    const triggerModelChangeBatchSpy = jest.spyOn(component.triggerModelChangeBatch, 'emit');

    component.modifyVisibilityControl(true, FeedbackVisibilityType.RECIPIENT, VisibilityControl.SHOW_RECIPIENT_NAME);

    expect(triggerModelChangeBatchSpy).toHaveBeenCalledWith({
      showResponsesTo: [],
      showGiverNameTo: [],
      showRecipientNameTo: [],
    });
  });

  it('modifyVisibilityControl: should trigger model change correctly when'
  + ' isAllowed is true, visibilityType is NOT RECIPIENT and visibilityControl is SHOW_RECIPIENT_NAME', () => {
    const visibilityType = FeedbackVisibilityType.GIVER_TEAM_MEMBERS;

    const triggerModelChangeBatchSpy = jest.spyOn(component.triggerModelChangeBatch, 'emit');

    component.modifyVisibilityControl(true, visibilityType, VisibilityControl.SHOW_RECIPIENT_NAME);

    expect(triggerModelChangeBatchSpy).toHaveBeenCalledWith({
      showResponsesTo: [visibilityType],
      showGiverNameTo: [],
      showRecipientNameTo: [visibilityType],
    });
  });

  it('modifyVisibilityControl: should trigger model change correctly when'
  + ' isAllowed is false, visibilityType is ANY and visibilityControl is SHOW_RESPONSE', () => {
    const visibilityType = FeedbackVisibilityType.RECIPIENT;

    const triggerModelChangeBatchSpy = jest.spyOn(component.triggerModelChangeBatch, 'emit');

    component.modifyVisibilityControl(true, visibilityType, VisibilityControl.SHOW_RESPONSE);
    component.modifyVisibilityControl(true, visibilityType, VisibilityControl.SHOW_GIVER_NAME);
    component.modifyVisibilityControl(true, visibilityType, VisibilityControl.SHOW_RECIPIENT_NAME);

    expect(triggerModelChangeBatchSpy).toHaveBeenCalledWith({
      showResponsesTo: [visibilityType],
      showGiverNameTo: [visibilityType],
      showRecipientNameTo: [visibilityType],
    });

    component.modifyVisibilityControl(false, visibilityType, VisibilityControl.SHOW_RESPONSE);

    expect(triggerModelChangeBatchSpy).toHaveBeenCalledWith({
      showResponsesTo: [],
      showGiverNameTo: [],
      showRecipientNameTo: [],
    });
  });

  it('modifyVisibilityControl: should trigger model change correctly when'
  + ' isAllowed is false, visibilityType is ANY and visibilityControl is SHOW_GIVER_NAME', () => {
    const visibilityType = FeedbackVisibilityType.RECIPIENT;

    const triggerModelChangeBatchSpy = jest.spyOn(component.triggerModelChangeBatch, 'emit');

    component.modifyVisibilityControl(true, visibilityType, VisibilityControl.SHOW_RESPONSE);
    component.modifyVisibilityControl(true, visibilityType, VisibilityControl.SHOW_GIVER_NAME);
    component.modifyVisibilityControl(true, visibilityType, VisibilityControl.SHOW_RECIPIENT_NAME);

    expect(triggerModelChangeBatchSpy).toHaveBeenCalledWith({
      showResponsesTo: [visibilityType],
      showGiverNameTo: [visibilityType],
      showRecipientNameTo: [visibilityType],
    });

    component.modifyVisibilityControl(false, visibilityType, VisibilityControl.SHOW_GIVER_NAME);

    expect(triggerModelChangeBatchSpy).toHaveBeenCalledWith({
      showResponsesTo: [visibilityType],
      showGiverNameTo: [],
      showRecipientNameTo: [visibilityType],
    });
  });

  // recipients' show recipient is visible by setting SHOW_RESPONSE to true and cannot be edited afterwards
  it('modifyVisibilityControl: should trigger model change correctly when'
  + ' isAllowed is false, visibilityType is RECIPIENT and visibilityControl is SHOW_RECIPIENT_NAME', () => {
    const triggerModelChangeBatchSpy = jest.spyOn(component.triggerModelChangeBatch, 'emit');

    component.modifyVisibilityControl(true, FeedbackVisibilityType.RECIPIENT, VisibilityControl.SHOW_RESPONSE);
    component.modifyVisibilityControl(true, FeedbackVisibilityType.RECIPIENT, VisibilityControl.SHOW_GIVER_NAME);

    expect(triggerModelChangeBatchSpy).toHaveBeenCalledWith({
      showResponsesTo: [FeedbackVisibilityType.RECIPIENT],
      showGiverNameTo: [FeedbackVisibilityType.RECIPIENT],
      showRecipientNameTo: [FeedbackVisibilityType.RECIPIENT],
    });

    component.modifyVisibilityControl(false, FeedbackVisibilityType.RECIPIENT, VisibilityControl.SHOW_RECIPIENT_NAME);

    expect(triggerModelChangeBatchSpy).toHaveBeenCalledWith({
      showResponsesTo: [FeedbackVisibilityType.RECIPIENT],
      showGiverNameTo: [FeedbackVisibilityType.RECIPIENT],
      showRecipientNameTo: [FeedbackVisibilityType.RECIPIENT],
    });
  });

  it('modifyVisibilityControl: should trigger model change correctly when'
  + ' isAllowed is false, visibilityType is NOT RECIPIENT and visibilityControl is SHOW_RECIPIENT_NAME', () => {
    const visibilityType = FeedbackVisibilityType.GIVER_TEAM_MEMBERS;

    const triggerModelChangeBatchSpy = jest.spyOn(component.triggerModelChangeBatch, 'emit');

    component.modifyVisibilityControl(true, visibilityType, VisibilityControl.SHOW_GIVER_NAME);
    component.modifyVisibilityControl(true, visibilityType, VisibilityControl.SHOW_RECIPIENT_NAME);

    expect(triggerModelChangeBatchSpy).toHaveBeenCalledWith({
      showResponsesTo: [visibilityType],
      showGiverNameTo: [visibilityType],
      showRecipientNameTo: [visibilityType],
    });

    component.modifyVisibilityControl(false, visibilityType, VisibilityControl.SHOW_RECIPIENT_NAME);

    expect(triggerModelChangeBatchSpy).toHaveBeenCalledWith({
      showResponsesTo: [visibilityType],
      showGiverNameTo: [visibilityType],
      showRecipientNameTo: [],
    });
  });
});
