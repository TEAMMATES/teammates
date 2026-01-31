import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { ContributionQuestionStatisticsComponent } from './contribution-question-statistics.component';

describe('ContributionQuestionStatisticsComponent', () => {
  let component: ContributionQuestionStatisticsComponent;
  let fixture: ComponentFixture<ContributionQuestionStatisticsComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      providers: [provideRouter([])],
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
