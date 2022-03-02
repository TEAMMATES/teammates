import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { ContributionQuestionResponseComponent } from './contribution-question-response.component';

describe('ContributionQuestionResponseComponent', () => {
  let component: ContributionQuestionResponseComponent;
  let fixture: ComponentFixture<ContributionQuestionResponseComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ContributionQuestionResponseComponent],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ContributionQuestionResponseComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
