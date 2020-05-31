import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { MsqQuestionStatisticsComponent } from './msq-question-statistics.component';

describe('MsqQuestionStatisticsComponent', () => {
  let component: MsqQuestionStatisticsComponent;
  let fixture: ComponentFixture<MsqQuestionStatisticsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [MsqQuestionStatisticsComponent],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MsqQuestionStatisticsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
