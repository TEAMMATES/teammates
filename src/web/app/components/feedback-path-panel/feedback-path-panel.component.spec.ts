import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { NgbDropdownModule } from '@ng-bootstrap/ng-bootstrap';

import { FeedbackParticipantType, NumberOfEntitiesToGiveFeedbackToSetting } from '../../../types/api-output';
import { TeammatesCommonModule } from '../teammates-common/teammates-common.module';
import { FeedbackPathPanelComponent } from './feedback-path-panel.component';

describe('FeedbackPathPanelComponent', () => {
  let component: FeedbackPathPanelComponent;
  let fixture: ComponentFixture<FeedbackPathPanelComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [
        FeedbackPathPanelComponent,
      ],
      imports: [
        FormsModule,
        NgbDropdownModule,
        TeammatesCommonModule,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(FeedbackPathPanelComponent);
    component = fixture.componentInstance;
    component.allowedFeedbackPaths = new Map([
      [FeedbackParticipantType.TEAMS, [FeedbackParticipantType.OWN_TEAM_MEMBERS, FeedbackParticipantType.STUDENTS]],
      [FeedbackParticipantType.INSTRUCTORS, [FeedbackParticipantType.OWN_TEAM_MEMBERS, FeedbackParticipantType.STUDENTS]]
    ]);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should toggle sub menu status correctly', () => {
    const menu = FeedbackParticipantType.STUDENTS;
    component.toggleSubMenu(menu);
    const firstToggle = component.isSubMenuOpen(menu);
    component.toggleSubMenu(menu);
    const secondToggle = component.isSubMenuOpen(menu);
    expect(firstToggle).toBe(!secondToggle);
  });

  it('should reset menu', () => {
    const menu = FeedbackParticipantType.STUDENTS;
    component.toggleSubMenu(menu);
    component.resetMenu();
    const isMenuOpen = component.isSubMenuOpen(menu);
    expect(isMenuOpen).toBe(false);
  });

  it('should trigger custom number of entities', () => {
    const customNumber = 5;
    const emitSpy = jest.spyOn(component.customNumberOfEntitiesToGiveFeedbackTo, 'emit');
    component.triggerCustomNumberOfEntities(customNumber);
    expect(emitSpy).toHaveBeenCalledWith(customNumber);
  });

  it('should trigger number of entities setting', () => {
    const setting = NumberOfEntitiesToGiveFeedbackToSetting.CUSTOM;
    const emitSpy = jest.spyOn(component.numberOfEntitiesToGiveFeedbackToSetting, 'emit');
    component.triggerNumberOfEntitiesSetting(setting);
    expect(emitSpy).toHaveBeenCalledWith(setting);
  });

  it('should trigger custom feedback path', () => {
    const emitSpy = jest.spyOn(component.customFeedbackPath, 'emit');
    component.triggerCustomFeedbackPath();
    expect(emitSpy).toHaveBeenCalledWith(true);
  });

  describe('changeGiverRecipientType', () => {
    it('should reset visibility settings if not using custom feedback path', () => {
      component.model.isUsingOtherFeedbackPath = false;
      component.changeGiverRecipientType(FeedbackParticipantType.INSTRUCTORS, FeedbackParticipantType.STUDENTS);
      expect(component.model.isUsingOtherFeedbackPath).toEqual(false);
      expect(component.model.isUsingOtherVisibilitySetting).toEqual(false);
      expect(component.model.showResponsesTo).toEqual([]);
      expect(component.model.showGiverNameTo).toEqual([]);
      expect(component.model.showRecipientNameTo).toEqual([]);
    });

    it('should not reset visibility settings if using custom feedback path', () => {
      component.model.isUsingOtherFeedbackPath = true;
      component.changeGiverRecipientType(FeedbackParticipantType.INSTRUCTORS, FeedbackParticipantType.STUDENTS);
      expect(component.model.commonVisibilitySettingName).not.toEqual('Please select a visibility option');
      expect(component.model.isUsingOtherFeedbackPath).toEqual(true);
    });
  });
});
