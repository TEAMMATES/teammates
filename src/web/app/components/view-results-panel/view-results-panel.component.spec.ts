import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { NgbTooltipModule } from '@ng-bootstrap/ng-bootstrap';

import { ViewResultsPanelComponent } from './view-results-panel.component';
import {
  InstructorSessionResultSectionType,
} from '../../pages-instructor/instructor-session-result-page/instructor-session-result-section-type.enum';
import {
  InstructorSessionResultViewType,
} from '../../pages-instructor/instructor-session-result-page/instructor-session-result-view-type.enum';
import {
  SectionTypeDescriptionModule,
} from '../../pages-instructor/instructor-session-result-page/section-type-description.module';
import { TeammatesCommonModule } from '../teammates-common/teammates-common.module';

describe('ViewResultsPanelComponent', () => {
  let component: ViewResultsPanelComponent;
  let fixture: ComponentFixture<ViewResultsPanelComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [
        ViewResultsPanelComponent,
      ],
      imports: [
        FormsModule,
        NgbTooltipModule,
        TeammatesCommonModule,
        SectionTypeDescriptionModule,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ViewResultsPanelComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should emit viewTypeChange event when handleViewTypeChange is called', () => {
    const newViewType = InstructorSessionResultViewType.GRQ;
    const spy = jest.spyOn(component.viewTypeChange, 'emit');
    component.handleViewTypeChange(newViewType);
    expect(spy).toHaveBeenCalledWith(newViewType);
  });

  it('should emit sectionChange event when handleSectionChange is called', () => {
    const newSection = 'newSection';
    const spy = jest.spyOn(component.sectionChange, 'emit');
    component.handleSectionChange(newSection);
    expect(spy).toHaveBeenCalledWith(newSection);
  });

  it('should emit sectionTypeChange event when handleSectionTypeChange is called', () => {
    const newSectionType = InstructorSessionResultSectionType.EITHER;
    const spy = jest.spyOn(component.sectionTypeChange, 'emit');
    component.handleSectionTypeChange(newSectionType);
    expect(spy).toHaveBeenCalledWith(newSectionType);
  });

  it('should emit groupByTeamChange event when handleGroupByTeamChange is called', () => {
    const newGroupByTeam = false;
    const spy = jest.spyOn(component.groupByTeamChange, 'emit');
    component.handleGroupByTeamChange(newGroupByTeam);
    expect(spy).toHaveBeenCalledWith(newGroupByTeam);
  });

  it('should emit showStatisticsChange event when handleShowStatisticsChange is called', () => {
    const newShowStatistics = false;
    const spy = jest.spyOn(component.showStatisticsChange, 'emit');
    component.handleShowStatisticsChange(newShowStatistics);
    expect(spy).toHaveBeenCalledWith(newShowStatistics);
  });

  it('should emit indicateMissingResponsesChange event when handleIndicateMissingResponsesChange is called', () => {
    const newIndicateMissingResponsesChange = false;
    const spy = jest.spyOn(component.indicateMissingResponsesChange, 'emit');
    component.handleIndicateMissingResponsesChange(newIndicateMissingResponsesChange);
    expect(spy).toHaveBeenCalledWith(newIndicateMissingResponsesChange);
  });
});
