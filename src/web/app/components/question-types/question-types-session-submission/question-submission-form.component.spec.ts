import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { HttpClientTestingModule } from '@angular/common/http/testing';
import { QuestionSubmissionFormComponent } from './question-submission-form.component';
import { QuestionTypesSessionSubmissionModule } from './question-types-session-submission.module';

describe('QuestionSubmissionFormComponent', () => {
  let component: QuestionSubmissionFormComponent;
  let fixture: ComponentFixture<QuestionSubmissionFormComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        QuestionTypesSessionSubmissionModule,
        HttpClientTestingModule,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(QuestionSubmissionFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
