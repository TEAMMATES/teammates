import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { McqQuestionEditDetailsFormComponent } from './mcq-question-edit-details-form.component';

describe('McqQuestionEditDetailsFormComponent', () => {
  let component: McqQuestionEditDetailsFormComponent;
  let fixture: ComponentFixture<McqQuestionEditDetailsFormComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [McqQuestionEditDetailsFormComponent],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(McqQuestionEditDetailsFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
