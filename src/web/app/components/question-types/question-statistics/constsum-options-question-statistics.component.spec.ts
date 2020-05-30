import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ConstsumOptionsQuestionStatisticsComponent } from './constsum-options-question-statistics.component';

describe('ConstsumOptionsQuestionStatisticsComponent', () => {
  let component: ConstsumOptionsQuestionStatisticsComponent;
  let fixture: ComponentFixture<ConstsumOptionsQuestionStatisticsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ConstsumOptionsQuestionStatisticsComponent],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ConstsumOptionsQuestionStatisticsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
