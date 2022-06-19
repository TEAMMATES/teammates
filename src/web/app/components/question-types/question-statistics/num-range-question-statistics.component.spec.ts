import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NumRangeQuestionStatisticsComponent } from './num-range-question-statistics.component';

describe('NumRangeQuestionStatisticsComponent', () => {
  let component: NumRangeQuestionStatisticsComponent;
  let fixture: ComponentFixture<NumRangeQuestionStatisticsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ NumRangeQuestionStatisticsComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(NumRangeQuestionStatisticsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
