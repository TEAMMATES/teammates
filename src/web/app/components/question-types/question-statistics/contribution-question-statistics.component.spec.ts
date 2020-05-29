import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ContributionQuestionStatisticsComponent } from './contribution-question-statistics.component';

describe('ContributionQuestionStatisticsComponent', () => {
  let component: ContributionQuestionStatisticsComponent;
  let fixture: ComponentFixture<ContributionQuestionStatisticsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ContributionQuestionStatisticsComponent],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ContributionQuestionStatisticsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
