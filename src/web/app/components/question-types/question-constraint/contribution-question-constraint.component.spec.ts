import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { ContributionQuestionConstraintComponent } from './contribution-question-constraint.component';

describe('ContributionQuestionConstraintComponent', () => {
  let component: ContributionQuestionConstraintComponent;
  let fixture: ComponentFixture<ContributionQuestionConstraintComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ContributionQuestionConstraintComponent],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ContributionQuestionConstraintComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
