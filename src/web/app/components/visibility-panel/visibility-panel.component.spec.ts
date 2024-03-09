import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { NgbDropdownModule, NgbTooltipModule } from '@ng-bootstrap/ng-bootstrap';

import { VisibilityPanelComponent } from './visibility-panel.component';
import { TeammatesCommonModule } from '../teammates-common/teammates-common.module';
import { VisibilityMessagesModule } from '../visibility-messages/visibility-messages.module';
import { CommonVisibilitySetting } from '../../../services/feedback-questions.service';
import { FeedbackVisibilityType } from '../../../types/api-output';
import { VisibilityControl } from '../../../types/visibility-control';

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
    const emitSpy = jest.spyOn(component.customVisibilitySetting, 'emit');

    component.triggerCustomVisibilitySetting();

    expect(emitSpy).toHaveBeenCalledWith(true);
  });

  it.each([
    {visibilityType: FeedbackVisibilityType.RECIPIENT, visibilityControl: VisibilityControl.SHOW_RESPONSE,
      visibilityTypeAriaLabel: 'Recipient(s)', visibilityControlAriaLabel: 'Answer'},
    {visibilityType: FeedbackVisibilityType.RECIPIENT, visibilityControl: VisibilityControl.SHOW_GIVER_NAME,
      visibilityTypeAriaLabel: 'Recipient(s)', visibilityControlAriaLabel: 'Giver\'s Name'},
    {visibilityType: FeedbackVisibilityType.RECIPIENT, visibilityControl: VisibilityControl.SHOW_RECIPIENT_NAME,
      visibilityTypeAriaLabel: 'Recipient(s)', visibilityControlAriaLabel: 'Recipient\'s Name'},

    {visibilityType: FeedbackVisibilityType.GIVER_TEAM_MEMBERS, visibilityControl: VisibilityControl.SHOW_RESPONSE,
      visibilityTypeAriaLabel: 'Giver\'s Team Members', visibilityControlAriaLabel: 'Answer'},
    {visibilityType: FeedbackVisibilityType.GIVER_TEAM_MEMBERS, visibilityControl: VisibilityControl.SHOW_GIVER_NAME,
      visibilityTypeAriaLabel: 'Giver\'s Team Members', visibilityControlAriaLabel: 'Giver\'s Name'},
    {visibilityType: FeedbackVisibilityType.GIVER_TEAM_MEMBERS, visibilityControl: VisibilityControl.SHOW_RECIPIENT_NAME,
      visibilityTypeAriaLabel: 'Giver\'s Team Members', visibilityControlAriaLabel: 'Recipient\'s Name'},

    {visibilityType: FeedbackVisibilityType.RECIPIENT_TEAM_MEMBERS, visibilityControl: VisibilityControl.SHOW_RESPONSE,
      visibilityTypeAriaLabel: 'Recipient\'s Team Members', visibilityControlAriaLabel: 'Answer'},
    {visibilityType: FeedbackVisibilityType.RECIPIENT_TEAM_MEMBERS, visibilityControl: VisibilityControl.SHOW_GIVER_NAME,
      visibilityTypeAriaLabel: 'Recipient\'s Team Members', visibilityControlAriaLabel: 'Giver\'s Name'},
    {visibilityType: FeedbackVisibilityType.RECIPIENT_TEAM_MEMBERS, visibilityControl: VisibilityControl.SHOW_RECIPIENT_NAME,
      visibilityTypeAriaLabel: 'Recipient\'s Team Members', visibilityControlAriaLabel: 'Recipient\'s Name'},

    {visibilityType: FeedbackVisibilityType.STUDENTS, visibilityControl: VisibilityControl.SHOW_RESPONSE,
      visibilityTypeAriaLabel: 'Other Students', visibilityControlAriaLabel: 'Answer'},
    {visibilityType: FeedbackVisibilityType.STUDENTS, visibilityControl: VisibilityControl.SHOW_GIVER_NAME,
      visibilityTypeAriaLabel: 'Other Students', visibilityControlAriaLabel: 'Giver\'s Name'},
    {visibilityType: FeedbackVisibilityType.STUDENTS, visibilityControl: VisibilityControl.SHOW_RECIPIENT_NAME,
      visibilityTypeAriaLabel: 'Other Students', visibilityControlAriaLabel: 'Recipient\'s Name'},

    {visibilityType: FeedbackVisibilityType.INSTRUCTORS, visibilityControl: VisibilityControl.SHOW_RESPONSE,
      visibilityTypeAriaLabel: 'Instructors', visibilityControlAriaLabel: 'Answer'},
    {visibilityType: FeedbackVisibilityType.INSTRUCTORS, visibilityControl: VisibilityControl.SHOW_GIVER_NAME,
      visibilityTypeAriaLabel: 'Instructors', visibilityControlAriaLabel: 'Giver\'s Name'},
    {visibilityType: FeedbackVisibilityType.INSTRUCTORS, visibilityControl: VisibilityControl.SHOW_RECIPIENT_NAME,
      visibilityTypeAriaLabel: 'Instructors', visibilityControlAriaLabel: 'Recipient\'s Name'},
  ])('getCheckboxAriaLabel: should return the string \'$visibilityTypeAriaLabel can see $visibilityControlAriaLabel\'',
  ({visibilityType, visibilityControl, visibilityTypeAriaLabel, visibilityControlAriaLabel}) => {
    expect(component.getCheckboxAriaLabel(visibilityType, visibilityControl))
    .toBe(`${visibilityTypeAriaLabel} can see ${visibilityControlAriaLabel}`);
  });

  it('applyCommonVisibilitySettings: should trigger model change with isUsingOtherVisibilitySetting as false ' +
  'and CommonVisibilitySetting', () => {
    const testSettings: CommonVisibilitySetting = {
      name: 'testSettings name',

      visibilitySettings: {
        SHOW_RESPONSE: [FeedbackVisibilityType.RECIPIENT],
        SHOW_GIVER_NAME: [FeedbackVisibilityType.RECIPIENT],
        SHOW_RECIPIENT_NAME: [FeedbackVisibilityType.RECIPIENT],
      },
    };

    const emitSpy = jest.spyOn(component.triggerModelChangeBatch, 'emit');

    component.applyCommonVisibilitySettings(testSettings);

    expect(emitSpy).toHaveBeenCalledWith({
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
    const emitSpy = jest.spyOn(component.visibilityStateMachineChange, 'emit');

    component.modifyVisibilityControl(true, visibilityType, visibilityControl);

    expect(allowToSeeSpy).toHaveBeenCalledWith(visibilityType, visibilityControl);
    expect(disallowToSeeSpy).not.toHaveBeenCalledWith();
    expect(emitSpy).toHaveBeenCalledWith(component.visibilityStateMachine);
  });

  it('modifyVisibilityControl: should only call disallowToSee and emit the updated visibilityStateMachine', () => {
    const visibilityType = FeedbackVisibilityType.RECIPIENT;
    const visibilityControl = VisibilityControl.SHOW_RESPONSE;

    const allowToSeeSpy = jest.spyOn(component.visibilityStateMachine, 'allowToSee');
    const disallowToSeeSpy = jest.spyOn(component.visibilityStateMachine, 'disallowToSee');
    const emitSpy = jest.spyOn(component.visibilityStateMachineChange, 'emit');

    component.modifyVisibilityControl(false, visibilityType, visibilityControl);

    expect(allowToSeeSpy).not.toHaveBeenCalledWith();
    expect(disallowToSeeSpy).toHaveBeenCalledWith(visibilityType, visibilityControl);
    expect(emitSpy).toHaveBeenCalledWith(component.visibilityStateMachine);
  });

  it.each([
    {visibilityControl: VisibilityControl.SHOW_RESPONSE, visibilityControlString: 'SHOW_RESPONSE'},
    {visibilityControl: VisibilityControl.SHOW_GIVER_NAME, visibilityControlString: 'SHOW_GIVER_NAME'},
    {visibilityControl: VisibilityControl.SHOW_RECIPIENT_NAME, visibilityControlString: 'SHOW_RECIPIENT_NAME'},
  ])('modifyVisibilityControl: should trigger model change with unchanged arrays when visibility is initialized to true ' +
  'and isAllowed, visibilityType, visibilityControl are true, RECIPIENT, $visibilityControlString',({visibilityControl}) => {
    const emitSpy = jest.spyOn(component.triggerModelChangeBatch, 'emit');

    component.modifyVisibilityControl(true, FeedbackVisibilityType.RECIPIENT, VisibilityControl.SHOW_RESPONSE);
    component.modifyVisibilityControl(true, FeedbackVisibilityType.RECIPIENT, VisibilityControl.SHOW_GIVER_NAME);

    expect(emitSpy).toHaveBeenCalledWith({
      showResponsesTo: [FeedbackVisibilityType.RECIPIENT],
      showGiverNameTo: [FeedbackVisibilityType.RECIPIENT],
      showRecipientNameTo: [FeedbackVisibilityType.RECIPIENT],
    });

    component.modifyVisibilityControl(true, FeedbackVisibilityType.RECIPIENT, visibilityControl);

    expect(emitSpy).toHaveBeenCalledWith({
      showResponsesTo: [FeedbackVisibilityType.RECIPIENT],
      showGiverNameTo: [FeedbackVisibilityType.RECIPIENT],
      showRecipientNameTo: [FeedbackVisibilityType.RECIPIENT],
    });
  });

  it.each([
    {visibilityType: FeedbackVisibilityType.GIVER_TEAM_MEMBERS, visibilityControl: VisibilityControl.SHOW_RESPONSE,
      visibilityTypeString: 'GIVER_TEAM_MEMBERS', visibilityControlString: 'SHOW_RESPONSE'},
    {visibilityType: FeedbackVisibilityType.GIVER_TEAM_MEMBERS, visibilityControl: VisibilityControl.SHOW_GIVER_NAME,
      visibilityTypeString: 'GIVER_TEAM_MEMBERS', visibilityControlString: 'SHOW_GIVER_NAME'},
    {visibilityType: FeedbackVisibilityType.GIVER_TEAM_MEMBERS, visibilityControl: VisibilityControl.SHOW_RECIPIENT_NAME,
      visibilityTypeString: 'GIVER_TEAM_MEMBERS', visibilityControlString: 'SHOW_RECIPIENT_NAME'},

    {visibilityType: FeedbackVisibilityType.RECIPIENT_TEAM_MEMBERS, visibilityControl: VisibilityControl.SHOW_RESPONSE,
      visibilityTypeString: 'RECIPIENT_TEAM_MEMBERS', visibilityControlString: 'SHOW_RESPONSE'},
    {visibilityType: FeedbackVisibilityType.RECIPIENT_TEAM_MEMBERS, visibilityControl: VisibilityControl.SHOW_GIVER_NAME,
      visibilityTypeString: 'RECIPIENT_TEAM_MEMBERS', visibilityControlString: 'SHOW_GIVER_NAME'},
    {visibilityType: FeedbackVisibilityType.RECIPIENT_TEAM_MEMBERS, visibilityControl: VisibilityControl.SHOW_RECIPIENT_NAME,
      visibilityTypeString: 'RECIPIENT_TEAM_MEMBERS', visibilityControlString: 'SHOW_RECIPIENT_NAME'},

    {visibilityType: FeedbackVisibilityType.STUDENTS, visibilityControl: VisibilityControl.SHOW_RESPONSE,
      visibilityTypeString: 'STUDENTS', visibilityControlString: 'SHOW_RESPONSE'},
    {visibilityType: FeedbackVisibilityType.STUDENTS, visibilityControl: VisibilityControl.SHOW_GIVER_NAME,
      visibilityTypeString: 'STUDENTS', visibilityControlString: 'SHOW_GIVER_NAME'},
    {visibilityType: FeedbackVisibilityType.STUDENTS, visibilityControl: VisibilityControl.SHOW_RECIPIENT_NAME,
      visibilityTypeString: 'STUDENTS', visibilityControlString: 'SHOW_RECIPIENT_NAME'},

    {visibilityType: FeedbackVisibilityType.INSTRUCTORS, visibilityControl: VisibilityControl.SHOW_RESPONSE,
      visibilityTypeString: 'INSTRUCTORS', visibilityControlString: 'SHOW_RESPONSE'},
    {visibilityType: FeedbackVisibilityType.INSTRUCTORS, visibilityControl: VisibilityControl.SHOW_GIVER_NAME,
      visibilityTypeString: 'INSTRUCTORS', visibilityControlString: 'SHOW_GIVER_NAME'},
    {visibilityType: FeedbackVisibilityType.INSTRUCTORS, visibilityControl: VisibilityControl.SHOW_RECIPIENT_NAME,
      visibilityTypeString: 'INSTRUCTORS', visibilityControlString: 'SHOW_RECIPIENT_NAME'},
  ])('modifyVisibilityControl: should trigger model change with unchanged arrays when visibility is initialized to true ' +
  'and isAllowed, visibilityType, visibilityControl are true, $visibilityTypeString, $visibilityControlString',({visibilityType, visibilityControl}) => {
    const emitSpy = jest.spyOn(component.triggerModelChangeBatch, 'emit');

    component.modifyVisibilityControl(true, visibilityType, VisibilityControl.SHOW_GIVER_NAME);
    component.modifyVisibilityControl(true, visibilityType, VisibilityControl.SHOW_RECIPIENT_NAME);

    expect(emitSpy).toHaveBeenCalledWith({
      showResponsesTo: [visibilityType],
      showGiverNameTo: [visibilityType],
      showRecipientNameTo: [visibilityType],
    });

    component.modifyVisibilityControl(true, visibilityType, visibilityControl);

    expect(emitSpy).toHaveBeenCalledWith({
      showResponsesTo: [visibilityType],
      showGiverNameTo: [visibilityType],
      showRecipientNameTo: [visibilityType],
    });
  });

  it('modifyVisibilityControl: should trigger model change correctly when isAllowed, visibilityType, visibilityControl ' +
  'are true, RECIPIENT, SHOW_RESPONSE', () => {
    const emitSpy = jest.spyOn(component.triggerModelChangeBatch, 'emit');

    component.modifyVisibilityControl(true, FeedbackVisibilityType.RECIPIENT, VisibilityControl.SHOW_RESPONSE);

    expect(emitSpy).toHaveBeenCalledWith({
      showResponsesTo: [FeedbackVisibilityType.RECIPIENT],
      showGiverNameTo: [],
      showRecipientNameTo: [FeedbackVisibilityType.RECIPIENT],
    });
  });

  it.each([
    {visibilityType: FeedbackVisibilityType.GIVER_TEAM_MEMBERS, visibilityTypeString: 'GIVER_TEAM_MEMBERS'},
    {visibilityType: FeedbackVisibilityType.RECIPIENT_TEAM_MEMBERS, visibilityTypeString: 'RECIPIENT_TEAM_MEMBERS'},
    {visibilityType: FeedbackVisibilityType.STUDENTS, visibilityTypeString: 'STUDENTS'},
    {visibilityType: FeedbackVisibilityType.INSTRUCTORS, visibilityTypeString: 'INSTRUCTORS'},
  ])('modifyVisibilityControl: should trigger model change correctly when isAllowed, visibilityType, visibilityControl ' +
  'are true, $visibilityTypeString, SHOW_RESPONSE',
  ({visibilityType}) => {
    const emitSpy = jest.spyOn(component.triggerModelChangeBatch, 'emit');

    component.modifyVisibilityControl(true, visibilityType, VisibilityControl.SHOW_RESPONSE);

    expect(emitSpy).toHaveBeenCalledWith({
      showResponsesTo: [visibilityType],
      showGiverNameTo: [],
      showRecipientNameTo: [],
    });
  });

  it.each([
    {visibilityType: FeedbackVisibilityType.RECIPIENT, visibilityTypeString: 'RECIPIENT'},
    {visibilityType: FeedbackVisibilityType.GIVER_TEAM_MEMBERS, visibilityTypeString: 'GIVER_TEAM_MEMBERS'},
    {visibilityType: FeedbackVisibilityType.RECIPIENT_TEAM_MEMBERS, visibilityTypeString: 'RECIPIENT_TEAM_MEMBERS'},
    {visibilityType: FeedbackVisibilityType.STUDENTS, visibilityTypeString: 'STUDENTS'},
    {visibilityType: FeedbackVisibilityType.INSTRUCTORS, visibilityTypeString: 'INSTRUCTORS'},
  ])('modifyVisibilityControl: should trigger model change correctly when isAllowed, visibilityType, visibilityControl ' +
  'are true, $visibilityTypeString, SHOW_GIVER_NAME',
  ({visibilityType}) => {
    const emitSpy = jest.spyOn(component.triggerModelChangeBatch, 'emit');

    component.modifyVisibilityControl(true, visibilityType, VisibilityControl.SHOW_GIVER_NAME);

    expect(emitSpy).toHaveBeenCalledWith({
      showResponsesTo: [visibilityType],
      showGiverNameTo: [visibilityType],
      showRecipientNameTo: [],
    });
  });

  // recipients' show recipient name cannot be edited
  it('modifyVisibilityControl: should trigger model change correctly when isAllowed, visibilityType, visibilityControl ' +
  'are true, RECIPIENT, SHOW_RECIPIENT_NAME', () => {
    const emitSpy = jest.spyOn(component.triggerModelChangeBatch, 'emit');

    component.modifyVisibilityControl(true, FeedbackVisibilityType.RECIPIENT, VisibilityControl.SHOW_RECIPIENT_NAME);

    expect(emitSpy).toHaveBeenCalledWith({
      showResponsesTo: [],
      showGiverNameTo: [],
      showRecipientNameTo: [],
    });
  });

  it.each([
    {visibilityType: FeedbackVisibilityType.GIVER_TEAM_MEMBERS, visibilityTypeString: 'GIVER_TEAM_MEMBERS'},
    {visibilityType: FeedbackVisibilityType.RECIPIENT_TEAM_MEMBERS, visibilityTypeString: 'RECIPIENT_TEAM_MEMBERS'},
    {visibilityType: FeedbackVisibilityType.STUDENTS, visibilityTypeString: 'STUDENTS'},
    {visibilityType: FeedbackVisibilityType.INSTRUCTORS, visibilityTypeString: 'INSTRUCTORS'},
  ])('modifyVisibilityControl: should trigger model change correctly when isAllowed, visibilityType, visibilityControl ' +
  'are true, $visibilityTypeString, SHOW_RECIPIENT_NAME',
  ({visibilityType}) => {
    const emitSpy = jest.spyOn(component.triggerModelChangeBatch, 'emit');

    component.modifyVisibilityControl(true, visibilityType, VisibilityControl.SHOW_RECIPIENT_NAME);

    expect(emitSpy).toHaveBeenCalledWith({
      showResponsesTo: [visibilityType],
      showGiverNameTo: [],
      showRecipientNameTo: [visibilityType],
    });
  });

  it.each([
    {visibilityType: FeedbackVisibilityType.RECIPIENT, visibilityControl: VisibilityControl.SHOW_RESPONSE,
      visibilityTypeString: 'RECIPIENT', visibilityControlString: 'SHOW_RESPONSE'},
    {visibilityType: FeedbackVisibilityType.RECIPIENT, visibilityControl: VisibilityControl.SHOW_GIVER_NAME,
      visibilityTypeString: 'RECIPIENT', visibilityControlString: 'SHOW_GIVER_NAME'},
    {visibilityType: FeedbackVisibilityType.RECIPIENT, visibilityControl: VisibilityControl.SHOW_RECIPIENT_NAME,
      visibilityTypeString: 'RECIPIENT', visibilityControlString: 'SHOW_RECIPIENT_NAME'},

    {visibilityType: FeedbackVisibilityType.GIVER_TEAM_MEMBERS, visibilityControl: VisibilityControl.SHOW_RESPONSE,
      visibilityTypeString: 'GIVER_TEAM_MEMBERS', visibilityControlString: 'SHOW_RESPONSE'},
    {visibilityType: FeedbackVisibilityType.GIVER_TEAM_MEMBERS, visibilityControl: VisibilityControl.SHOW_GIVER_NAME,
      visibilityTypeString: 'GIVER_TEAM_MEMBERS', visibilityControlString: 'SHOW_GIVER_NAME'},
    {visibilityType: FeedbackVisibilityType.GIVER_TEAM_MEMBERS, visibilityControl: VisibilityControl.SHOW_RECIPIENT_NAME,
      visibilityTypeString: 'GIVER_TEAM_MEMBERS', visibilityControlString: 'SHOW_RECIPIENT_NAME'},

    {visibilityType: FeedbackVisibilityType.RECIPIENT_TEAM_MEMBERS, visibilityControl: VisibilityControl.SHOW_RESPONSE,
      visibilityTypeString: 'RECIPIENT_TEAM_MEMBERS', visibilityControlString: 'SHOW_RESPONSE'},
    {visibilityType: FeedbackVisibilityType.RECIPIENT_TEAM_MEMBERS, visibilityControl: VisibilityControl.SHOW_GIVER_NAME,
      visibilityTypeString: 'RECIPIENT_TEAM_MEMBERS', visibilityControlString: 'SHOW_GIVER_NAME'},
    {visibilityType: FeedbackVisibilityType.RECIPIENT_TEAM_MEMBERS, visibilityControl: VisibilityControl.SHOW_RECIPIENT_NAME,
      visibilityTypeString: 'RECIPIENT_TEAM_MEMBERS', visibilityControlString: 'SHOW_RECIPIENT_NAME'},

    {visibilityType: FeedbackVisibilityType.STUDENTS, visibilityControl: VisibilityControl.SHOW_RESPONSE,
      visibilityTypeString: 'STUDENTS', visibilityControlString: 'SHOW_RESPONSE'},
    {visibilityType: FeedbackVisibilityType.STUDENTS, visibilityControl: VisibilityControl.SHOW_GIVER_NAME,
      visibilityTypeString: 'STUDENTS', visibilityControlString: 'SHOW_GIVER_NAME'},
    {visibilityType: FeedbackVisibilityType.STUDENTS, visibilityControl: VisibilityControl.SHOW_RECIPIENT_NAME,
      visibilityTypeString: 'STUDENTS', visibilityControlString: 'SHOW_RECIPIENT_NAME'},

    {visibilityType: FeedbackVisibilityType.INSTRUCTORS, visibilityControl: VisibilityControl.SHOW_RESPONSE,
      visibilityTypeString: 'INSTRUCTORS', visibilityControlString: 'SHOW_RESPONSE'},
    {visibilityType: FeedbackVisibilityType.INSTRUCTORS, visibilityControl: VisibilityControl.SHOW_GIVER_NAME,
      visibilityTypeString: 'INSTRUCTORS', visibilityControlString: 'SHOW_GIVER_NAME'},
    {visibilityType: FeedbackVisibilityType.INSTRUCTORS, visibilityControl: VisibilityControl.SHOW_RECIPIENT_NAME,
      visibilityTypeString: 'INSTRUCTORS', visibilityControlString: 'SHOW_RECIPIENT_NAME'},
  ])('modifyVisibilityControl: should trigger model change with unchanged arrays when visibility is initialized to false ' +
  'and isAllowed, visibilityType, visibilityControl are false, $visibilityTypeString, $visibilityControlString',({visibilityType, visibilityControl}) => {
    const emitSpy = jest.spyOn(component.triggerModelChangeBatch, 'emit');

    component.modifyVisibilityControl(false, visibilityType, visibilityControl);

    expect(emitSpy).toHaveBeenCalledWith({
      showResponsesTo: [],
      showGiverNameTo: [],
      showRecipientNameTo: [],
    });
  });

  it('modifyVisibilityControl: should trigger model change correctly when isAllowed, visibilityType, visibilityControl ' +
  'are false, RECIPIENT, SHOW_RESPONSE', () => {
    const emitSpy = jest.spyOn(component.triggerModelChangeBatch, 'emit');

    component.modifyVisibilityControl(true, FeedbackVisibilityType.RECIPIENT, VisibilityControl.SHOW_RESPONSE);
    component.modifyVisibilityControl(true, FeedbackVisibilityType.RECIPIENT, VisibilityControl.SHOW_GIVER_NAME);

    expect(emitSpy).toHaveBeenCalledWith({
      showResponsesTo: [FeedbackVisibilityType.RECIPIENT],
      showGiverNameTo: [FeedbackVisibilityType.RECIPIENT],
      showRecipientNameTo: [FeedbackVisibilityType.RECIPIENT],
    });

    component.modifyVisibilityControl(false, FeedbackVisibilityType.RECIPIENT, VisibilityControl.SHOW_RESPONSE);

    expect(emitSpy).toHaveBeenCalledWith({
      showResponsesTo: [],
      showGiverNameTo: [],
      showRecipientNameTo: [],
    });
  });

  it.each([
    {visibilityType: FeedbackVisibilityType.GIVER_TEAM_MEMBERS, visibilityTypeString: 'GIVER_TEAM_MEMBERS'},
    {visibilityType: FeedbackVisibilityType.RECIPIENT_TEAM_MEMBERS, visibilityTypeString: 'RECIPIENT_TEAM_MEMBERS'},
    {visibilityType: FeedbackVisibilityType.STUDENTS, visibilityTypeString: 'STUDENTS'},
    {visibilityType: FeedbackVisibilityType.INSTRUCTORS, visibilityTypeString: 'INSTRUCTORS'},
  ])('modifyVisibilityControl: should trigger model change correctly when isAllowed, visibilityType, visibilityControl ' +
  'are false, $visibilityTypeString, SHOW_RESPONSE',
  ({visibilityType}) => {
    const emitSpy = jest.spyOn(component.triggerModelChangeBatch, 'emit');

    component.modifyVisibilityControl(true, visibilityType, VisibilityControl.SHOW_GIVER_NAME);
    component.modifyVisibilityControl(true, visibilityType, VisibilityControl.SHOW_RECIPIENT_NAME);

    expect(emitSpy).toHaveBeenCalledWith({
      showResponsesTo: [visibilityType],
      showGiverNameTo: [visibilityType],
      showRecipientNameTo: [visibilityType],
    });

    component.modifyVisibilityControl(false, visibilityType, VisibilityControl.SHOW_RESPONSE);

    expect(emitSpy).toHaveBeenCalledWith({
      showResponsesTo: [],
      showGiverNameTo: [],
      showRecipientNameTo: [],
    });
  });

  it('modifyVisibilityControl: should trigger model change correctly when isAllowed, visibilityType, visibilityControl ' +
  'are false, RECIPIENT, SHOW_GIVER_NAME', () => {
    const emitSpy = jest.spyOn(component.triggerModelChangeBatch, 'emit');

    component.modifyVisibilityControl(true, FeedbackVisibilityType.RECIPIENT, VisibilityControl.SHOW_RESPONSE);
    component.modifyVisibilityControl(true, FeedbackVisibilityType.RECIPIENT, VisibilityControl.SHOW_GIVER_NAME);

    expect(emitSpy).toHaveBeenCalledWith({
      showResponsesTo: [FeedbackVisibilityType.RECIPIENT],
      showGiverNameTo: [FeedbackVisibilityType.RECIPIENT],
      showRecipientNameTo: [FeedbackVisibilityType.RECIPIENT],
    });

    component.modifyVisibilityControl(false, FeedbackVisibilityType.RECIPIENT, VisibilityControl.SHOW_GIVER_NAME);

    expect(emitSpy).toHaveBeenCalledWith({
      showResponsesTo: [FeedbackVisibilityType.RECIPIENT],
      showGiverNameTo: [],
      showRecipientNameTo: [FeedbackVisibilityType.RECIPIENT],
    });
  });

  it.each([
    {visibilityType: FeedbackVisibilityType.GIVER_TEAM_MEMBERS, visibilityTypeString: 'GIVER_TEAM_MEMBERS'},
    {visibilityType: FeedbackVisibilityType.RECIPIENT_TEAM_MEMBERS, visibilityTypeString: 'RECIPIENT_TEAM_MEMBERS'},
    {visibilityType: FeedbackVisibilityType.STUDENTS, visibilityTypeString: 'STUDENTS'},
    {visibilityType: FeedbackVisibilityType.INSTRUCTORS, visibilityTypeString: 'INSTRUCTORS'},
  ])('modifyVisibilityControl: should trigger model change correctly when isAllowed, visibilityType, visibilityControl ' +
  'are false, $visibilityTypeString, SHOW_GIVER_NAME',
  ({visibilityType}) => {
    const emitSpy = jest.spyOn(component.triggerModelChangeBatch, 'emit');

    component.modifyVisibilityControl(true, visibilityType, VisibilityControl.SHOW_GIVER_NAME);
    component.modifyVisibilityControl(true, visibilityType, VisibilityControl.SHOW_RECIPIENT_NAME);

    expect(emitSpy).toHaveBeenCalledWith({
      showResponsesTo: [visibilityType],
      showGiverNameTo: [visibilityType],
      showRecipientNameTo: [visibilityType],
    });

    component.modifyVisibilityControl(false, visibilityType, VisibilityControl.SHOW_GIVER_NAME);

    expect(emitSpy).toHaveBeenCalledWith({
      showResponsesTo: [visibilityType],
      showGiverNameTo: [],
      showRecipientNameTo: [visibilityType],
    });
  });

  it('modifyVisibilityControl: should trigger model change correctly when isAllowed, visibilityType, visibilityControl ' +
  'are false, RECIPIENT, SHOW_RECIPIENT_NAME', () => {
    const emitSpy = jest.spyOn(component.triggerModelChangeBatch, 'emit');

    component.modifyVisibilityControl(true, FeedbackVisibilityType.RECIPIENT, VisibilityControl.SHOW_RESPONSE);
    component.modifyVisibilityControl(true, FeedbackVisibilityType.RECIPIENT, VisibilityControl.SHOW_GIVER_NAME);

    expect(emitSpy).toHaveBeenCalledWith({
      showResponsesTo: [FeedbackVisibilityType.RECIPIENT],
      showGiverNameTo: [FeedbackVisibilityType.RECIPIENT],
      showRecipientNameTo: [FeedbackVisibilityType.RECIPIENT],
    });

    component.modifyVisibilityControl(false, FeedbackVisibilityType.RECIPIENT, VisibilityControl.SHOW_RECIPIENT_NAME);

    expect(emitSpy).toHaveBeenCalledWith({
      showResponsesTo: [FeedbackVisibilityType.RECIPIENT],
      showGiverNameTo: [FeedbackVisibilityType.RECIPIENT],
      showRecipientNameTo: [FeedbackVisibilityType.RECIPIENT],
    });
  });

  it.each([
    {visibilityType: FeedbackVisibilityType.GIVER_TEAM_MEMBERS, visibilityTypeString: 'GIVER_TEAM_MEMBERS'},
    {visibilityType: FeedbackVisibilityType.RECIPIENT_TEAM_MEMBERS, visibilityTypeString: 'RECIPIENT_TEAM_MEMBERS'},
    {visibilityType: FeedbackVisibilityType.STUDENTS, visibilityTypeString: 'STUDENTS'},
    {visibilityType: FeedbackVisibilityType.INSTRUCTORS, visibilityTypeString: 'INSTRUCTORS'},
  ])('modifyVisibilityControl: should trigger model change correctly when isAllowed, visibilityType, visibilityControl ' +
  'are false, $visibilityTypeString, SHOW_RECIPIENT_NAME',
  ({visibilityType}) => {
    const emitSpy = jest.spyOn(component.triggerModelChangeBatch, 'emit');

    component.modifyVisibilityControl(true, visibilityType, VisibilityControl.SHOW_GIVER_NAME);
    component.modifyVisibilityControl(true, visibilityType, VisibilityControl.SHOW_RECIPIENT_NAME);

    expect(emitSpy).toHaveBeenCalledWith({
      showResponsesTo: [visibilityType],
      showGiverNameTo: [visibilityType],
      showRecipientNameTo: [visibilityType],
    });

    component.modifyVisibilityControl(false, visibilityType, VisibilityControl.SHOW_RECIPIENT_NAME);

    expect(emitSpy).toHaveBeenCalledWith({
      showResponsesTo: [visibilityType],
      showGiverNameTo: [visibilityType],
      showRecipientNameTo: [],
    });
  });
});
