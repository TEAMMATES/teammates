import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';

import { RubricQuestionStatisticsComponent } from './rubric-question-statistics.component';

describe('RubricQuestionStatisticsComponent', () => {
  let component: RubricQuestionStatisticsComponent;
  let fixture: ComponentFixture<RubricQuestionStatisticsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [RubricQuestionStatisticsComponent],
      imports: [FormsModule],
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
