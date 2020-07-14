import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ConstsumRecipientsQuestionInstructionComponent } from './constsum-recipients-question-instruction.component';

describe('ConstsumRecipientsQuestionInstructionComponent', () => {
  let component: ConstsumRecipientsQuestionInstructionComponent;
  let fixture: ComponentFixture<ConstsumRecipientsQuestionInstructionComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ConstsumRecipientsQuestionInstructionComponent],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ConstsumRecipientsQuestionInstructionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
