import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TextQuestionStatisticsComponent } from './text-question-statistics.component';

describe('TextQuestionStatisticsComponent', () => {
  let component: TextQuestionStatisticsComponent;
  let fixture: ComponentFixture<TextQuestionStatisticsComponent>;

  beforeEach(() => {
    fixture = TestBed.createComponent(TextQuestionStatisticsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
