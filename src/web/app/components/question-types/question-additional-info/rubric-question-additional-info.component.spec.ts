import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RubricQuestionAdditionalInfoComponent } from './rubric-question-additional-info.component';

describe('RubricQuestionAdditionalInfoComponent', () => {
  let component: RubricQuestionAdditionalInfoComponent;
  let fixture: ComponentFixture<RubricQuestionAdditionalInfoComponent>;

  beforeEach(() => {
    fixture = TestBed.createComponent(RubricQuestionAdditionalInfoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
