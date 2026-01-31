import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InstructorSessionResultQuestionViewComponent } from './instructor-session-result-question-view.component';

describe('InstructorSessionResultQuestionViewComponent', () => {
  let component: InstructorSessionResultQuestionViewComponent;
  let fixture: ComponentFixture<InstructorSessionResultQuestionViewComponent>;

  beforeEach(() => {
    fixture = TestBed.createComponent(InstructorSessionResultQuestionViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
