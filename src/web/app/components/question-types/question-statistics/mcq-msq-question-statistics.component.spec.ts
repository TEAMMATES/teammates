import { ComponentFixture, TestBed } from '@angular/core/testing';

import { McqMsqQuestionStatisticsComponent } from './mcq-msq-question-statistics.component';

describe('McqMsqQuestionStatisticsComponent', () => {
  let component: McqMsqQuestionStatisticsComponent;
  let fixture: ComponentFixture<McqMsqQuestionStatisticsComponent>;

  beforeEach(() => {
    fixture = TestBed.createComponent(McqMsqQuestionStatisticsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
