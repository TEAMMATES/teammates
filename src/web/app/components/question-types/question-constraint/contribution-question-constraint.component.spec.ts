import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ContributionQuestionConstraintComponent } from './contribution-question-constraint.component';

describe('ContributionQuestionConstraintComponent', () => {
  let component: ContributionQuestionConstraintComponent;
  let fixture: ComponentFixture<ContributionQuestionConstraintComponent>;

  beforeEach(async(() => {
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
