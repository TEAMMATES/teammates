import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { RubricQuestionResponseComponent } from './rubric-question-response.component';

describe('RubricQuestionResponseComponent', () => {
  let component: RubricQuestionResponseComponent;
  let fixture: ComponentFixture<RubricQuestionResponseComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [RubricQuestionResponseComponent],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RubricQuestionResponseComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
