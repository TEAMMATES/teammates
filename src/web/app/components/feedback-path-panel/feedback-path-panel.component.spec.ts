import { EventEmitter } from '@angular/core';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { NgbDropdownModule } from '@ng-bootstrap/ng-bootstrap';

import { FeedbackPathPanelComponent } from './feedback-path-panel.component';
import { FeedbackParticipantType, NumberOfEntitiesToGiveFeedbackToSetting } from '../../../types/api-output';
import { TeammatesCommonModule } from '../teammates-common/teammates-common.module';

describe('FeedbackPathPanelComponent', () => {
  let component: FeedbackPathPanelComponent;
  let fixture: ComponentFixture<FeedbackPathPanelComponent>;
  let emitSpy: jest.SpyInstance;

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
    ]);
    component.triggerModelChangeBatch = new EventEmitter<any>();
    emitSpy = jest.spyOn(component.triggerModelChangeBatch, 'emit');
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should toggle sub menu status correctly', () => {
    const menu = FeedbackParticipantType.STUDENTS;
    component.toggleSubMenu(menu);
    const firstToggle = component.isSubMenuOpen(menu);
    expect(firstToggle).toBe(true);
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
    const customNumberOfEntitiesToGiveFeedbackToEmitSpy =
      jest.spyOn(component.customNumberOfEntitiesToGiveFeedbackTo, 'emit');
    component.triggerCustomNumberOfEntities(customNumber);
    expect(customNumberOfEntitiesToGiveFeedbackToEmitSpy).toHaveBeenCalledWith(customNumber);
  });

  it('should trigger number of entities setting', () => {
    const setting = NumberOfEntitiesToGiveFeedbackToSetting.CUSTOM;
    const numberOfEntitiesToGiveFeedbackToSettingEmitSpy =
      jest.spyOn(component.numberOfEntitiesToGiveFeedbackToSetting, 'emit');
    component.triggerNumberOfEntitiesSetting(setting);
    expect(numberOfEntitiesToGiveFeedbackToSettingEmitSpy).toHaveBeenCalledWith(setting);
  });

  it('should trigger custom feedback path', () => {
    const customFeedbackPathEmitSpy = jest.spyOn(component.customFeedbackPath, 'emit');
    component.triggerCustomFeedbackPath();
    expect(customFeedbackPathEmitSpy).toHaveBeenCalledWith(true);
  });

  describe('changeGiverRecipientType', () => {
    it('should set default recipientType if recipientType is not allowed for giverType', () => {
      component.allowedFeedbackPaths.set(FeedbackParticipantType.TEAMS,
        [FeedbackParticipantType.OWN_TEAM_MEMBERS, FeedbackParticipantType.STUDENTS]);
      component.changeGiverRecipientType(FeedbackParticipantType.TEAMS, FeedbackParticipantType.INSTRUCTORS);
      expect(component.model.recipientType).toEqual(FeedbackParticipantType.OWN_TEAM_MEMBERS);
    });

    it('checks if custom path remains & emits false if input unchanged', () => {
      component.model.isUsingOtherFeedbackPath = true;
      component.model.giverType = FeedbackParticipantType.TEAMS;
      component.model.recipientType = FeedbackParticipantType.STUDENTS;
      component.changeGiverRecipientType(FeedbackParticipantType.TEAMS, FeedbackParticipantType.STUDENTS);
      expect(emitSpy).toHaveBeenCalledWith({
        isUsingOtherFeedbackPath: false,
      });
    });

    it('resets settings & emits params if giverType/recipientType changed', () => {
      component.model.giverType = FeedbackParticipantType.TEAMS;
      component.model.recipientType = FeedbackParticipantType.STUDENTS;
      component.changeGiverRecipientType(FeedbackParticipantType.TEAMS, FeedbackParticipantType.OWN_TEAM_MEMBERS);
      expect(emitSpy).toHaveBeenCalledWith({
        giverType: FeedbackParticipantType.TEAMS,
        recipientType: FeedbackParticipantType.OWN_TEAM_MEMBERS,
        commonVisibilitySettingName: 'Please select a visibility option',
        isUsingOtherFeedbackPath: false,
        isUsingOtherVisibilitySetting: false,
        showResponsesTo: [],
        showGiverNameTo: [],
        showRecipientNameTo: [],
      });
    });
    it('checks if emitSpy is not called when isUsingOtherFeedbackPath is false', () => {
      component.model.isUsingOtherFeedbackPath = false;
      component.model.giverType = FeedbackParticipantType.TEAMS;
      component.model.recipientType = FeedbackParticipantType.STUDENTS;
      component.changeGiverRecipientType(FeedbackParticipantType.TEAMS, FeedbackParticipantType.STUDENTS);
      expect(emitSpy).not.toHaveBeenCalled();
    });
  });
});
