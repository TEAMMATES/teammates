import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { NgxPageScrollCoreModule } from 'ngx-page-scroll-core';
import { HttpRequestService } from '../../../../services/http-request.service';
import { QuestionEditFormModule } from '../../../components/question-edit-form/question-edit-form.module';
import { QuestionSubmissionFormModule,
} from '../../../components/question-submission-form/question-submission-form.module';
import {
    QuestionStatisticsModule,
} from '../../../components/question-types/question-statistics/question-statistics.module';
import { InstructorSessionResultViewModule } from '../../../pages-instructor/instructor-session-result-page/instructor-session-result-view.module';
import { ExampleBoxModule } from '../example-box/example-box.module';
import { InstructorHelpQuestionsSectionComponent } from './instructor-help-questions-section.component';

describe('InstructorHelpQuestionsSectionComponent', () => {
  let component: InstructorHelpQuestionsSectionComponent;
  let fixture: ComponentFixture<InstructorHelpQuestionsSectionComponent>;
  let spyHttpRequestService: any;

  beforeEach(async(() => {
    spyHttpRequestService = {
      get: jest.fn(),
      post: jest.fn(),
      put: jest.fn(),
      delete: jest.fn(),
    };
    TestBed.configureTestingModule({
      declarations: [InstructorHelpQuestionsSectionComponent],
      imports: [
        InstructorSessionResultViewModule, NgbModule, RouterTestingModule, NgxPageScrollCoreModule, ExampleBoxModule,
        QuestionEditFormModule, QuestionStatisticsModule, QuestionSubmissionFormModule,
      ],
      providers: [
        { provide: HttpRequestService, useValue: spyHttpRequestService },
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
