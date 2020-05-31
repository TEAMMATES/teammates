import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { RankOptionsQuestionStatisticsComponent } from './rank-options-question-statistics.component';

describe('RankOptionsQuestionStatisticsComponent', () => {
  let component: RankOptionsQuestionStatisticsComponent;
  let fixture: ComponentFixture<RankOptionsQuestionStatisticsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [RankOptionsQuestionStatisticsComponent],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RankOptionsQuestionStatisticsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
