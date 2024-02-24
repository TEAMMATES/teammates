import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { NgxPageScrollCoreModule } from 'ngx-page-scroll-core';
import { InstructorHelpQuestionsSectionComponent } from './instructor-help-questions-section.component';
import { PanelChevronModule } from '../../../components/panel-chevron/panel-chevron.module';
import { QuestionEditFormModule } from '../../../components/question-edit-form/question-edit-form.module';
import {
  QuestionSubmissionFormModule,
} from '../../../components/question-submission-form/question-submission-form.module';
import {
  QuestionStatisticsModule,
} from '../../../components/question-types/question-statistics/question-statistics.module';
import { TeammatesRouterModule } from '../../../components/teammates-router/teammates-router.module';
import {
  InstructorSessionResultViewModule,
} from '../../../pages-instructor/instructor-session-result-page/instructor-session-result-view.module';
import { ExampleBoxModule } from '../example-box/example-box.module';
import { InstructorHelpPanelComponent } from '../instructor-help-panel/instructor-help-panel.component';

describe('InstructorHelpQuestionsSectionComponent', () => {
  let component: InstructorHelpQuestionsSectionComponent;
  let fixture: ComponentFixture<InstructorHelpQuestionsSectionComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [
        InstructorHelpQuestionsSectionComponent,
        InstructorHelpPanelComponent,
      ],
      imports: [
        InstructorSessionResultViewModule, NgbModule, RouterTestingModule, HttpClientTestingModule,
        NgxPageScrollCoreModule, NoopAnimationsModule, ExampleBoxModule, TeammatesRouterModule,
        QuestionEditFormModule, QuestionStatisticsModule, QuestionSubmissionFormModule, PanelChevronModule,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(InstructorHelpQuestionsSectionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
