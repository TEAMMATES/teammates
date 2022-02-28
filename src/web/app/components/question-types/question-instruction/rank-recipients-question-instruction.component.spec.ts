import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { RankRecipientsQuestionInstructionComponent } from './rank-recipients-question-instruction.component';

describe('RankRecipientsQuestionInstructionComponent', () => {
  let component: RankRecipientsQuestionInstructionComponent;
  let fixture: ComponentFixture<RankRecipientsQuestionInstructionComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [RankRecipientsQuestionInstructionComponent],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RankRecipientsQuestionInstructionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
