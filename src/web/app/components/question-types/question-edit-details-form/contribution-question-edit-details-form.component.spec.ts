import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { ContributionQuestionEditDetailsFormComponent } from './contribution-question-edit-details-form.component';
import { DEFAULT_CONTRIBUTION_QUESTION_DETAILS } from '../../../../types/default-question-structs';

describe('ContributionQuestionEditDetailsFormComponent', () => {
  let component: ContributionQuestionEditDetailsFormComponent;
  let fixture: ComponentFixture<ContributionQuestionEditDetailsFormComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      providers: [provideRouter([])],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ContributionQuestionEditDetailsFormComponent);
    component = fixture.componentInstance;
    component.model = DEFAULT_CONTRIBUTION_QUESTION_DETAILS();
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
