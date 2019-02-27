import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { InstructorSessionsResultQuestionViewComponent } from './instructor-sessions-result-question-view.component';

describe('InstructorSessionsResultQuestionViewComponent', () => {
  let component: InstructorSessionsResultQuestionViewComponent;
  let fixture: ComponentFixture<InstructorSessionsResultQuestionViewComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [InstructorSessionsResultQuestionViewComponent],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(InstructorSessionsResultQuestionViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
