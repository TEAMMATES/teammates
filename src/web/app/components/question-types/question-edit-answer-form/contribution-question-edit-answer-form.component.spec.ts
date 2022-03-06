import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { FormsModule } from '@angular/forms';
import { ContributionPointDescriptionPipe } from './contribution-point-description.pipe';
import { ContributionQuestionEditAnswerFormComponent } from './contribution-question-edit-answer-form.component';

describe('ContributionQuestionEditAnswerFormComponent', () => {
  let component: ContributionQuestionEditAnswerFormComponent;
  let fixture: ComponentFixture<ContributionQuestionEditAnswerFormComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [
        ContributionQuestionEditAnswerFormComponent,
        ContributionPointDescriptionPipe,
      ],
      imports: [
        FormsModule,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ContributionQuestionEditAnswerFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
