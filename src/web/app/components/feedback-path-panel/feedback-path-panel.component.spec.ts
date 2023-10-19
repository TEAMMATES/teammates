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

    component.customNumberOfEntitiesToGiveFeedbackTo.subscribe((value) => {
      component.model.customNumberOfEntitiesToGiveFeedbackTo = value;
    });
    component.numberOfEntitiesToGiveFeedbackToSetting.subscribe((value) => {
      component.model.numberOfEntitiesToGiveFeedbackToSetting = value;
    });
    component.customFeedbackPath.subscribe((value) => {
      component.model.isUsingOtherFeedbackPath = value;
    });

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
    component.triggerCustomNumberOfEntities(customNumber);
    const result = component.model.customNumberOfEntitiesToGiveFeedbackTo;
    expect(result).toEqual(customNumber);
  });

  it('should trigger number of entities setting', () => {
    const setting = NumberOfEntitiesToGiveFeedbackToSetting.CUSTOM;
    component.triggerNumberOfEntitiesSetting(setting);
    const result = component.model.numberOfEntitiesToGiveFeedbackToSetting;
     expect(result).toEqual(setting);
  });

  it('should trigger custom feedback path', () => {
    component.triggerCustomFeedbackPath();
    const result = component.model.isUsingOtherFeedbackPath;
    expect(result).toBe(true);
  });

});
