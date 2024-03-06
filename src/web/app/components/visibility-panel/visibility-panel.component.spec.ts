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

  describe('triggerCustomVisibilitySetting()', () => {
    it('should emit true from customVisibilitySetting', () => {
      const emitSpy = jest.spyOn(component.customVisibilitySetting, 'emit');

      component.triggerCustomVisibilitySetting();

      expect(emitSpy).toHaveBeenCalledWith(true);
    });
  });

  describe('getCheckboxAriaLabel(FeedbackVisibilityType, VisibilityControl)', () => {
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
    ])('should return the string \'$visibilityTypeAriaLabel can see $visibilityControlAriaLabel\'',
    ({visibilityType, visibilityControl, visibilityTypeAriaLabel, visibilityControlAriaLabel}) => {
      expect(component.getCheckboxAriaLabel(visibilityType, visibilityControl))
      .toBe(`${visibilityTypeAriaLabel} can see ${visibilityControlAriaLabel}`);
    });
  });

  describe('applyCommonVisibilitySettings(CommonVisibilitySetting)', () => {
    it('should trigger model change with isUsingOtherVisibilitySetting as false and CommonVisibilitySetting', () => {
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
  });
});
