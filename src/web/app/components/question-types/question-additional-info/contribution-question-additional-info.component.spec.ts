import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { ContributionQuestionAdditionalInfoComponent } from './contribution-question-additional-info.component';

describe('ContributionQuestionAdditionalInfoComponent', () => {
  let component: ContributionQuestionAdditionalInfoComponent;
  let fixture: ComponentFixture<ContributionQuestionAdditionalInfoComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ContributionQuestionAdditionalInfoComponent],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ContributionQuestionAdditionalInfoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
