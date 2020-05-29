import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { RubricQuestionStatisticsComponent } from './rubric-question-statistics.component';

describe('RubricQuestionStatisticsComponent', () => {
  let component: RubricQuestionStatisticsComponent;
  let fixture: ComponentFixture<RubricQuestionStatisticsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [RubricQuestionStatisticsComponent],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RubricQuestionStatisticsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
