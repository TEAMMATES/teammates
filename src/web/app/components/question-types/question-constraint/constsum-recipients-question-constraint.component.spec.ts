import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ConstsumRecipientsQuestionConstraintComponent } from './constsum-recipients-question-constraint.component';

describe('ConstsumRecipientsQuestionConstraintComponent', () => {
  let component: ConstsumRecipientsQuestionConstraintComponent;
  let fixture: ComponentFixture<ConstsumRecipientsQuestionConstraintComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ConstsumRecipientsQuestionConstraintComponent],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ConstsumRecipientsQuestionConstraintComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
