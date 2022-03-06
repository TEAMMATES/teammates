import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { RankOptionsQuestionInstructionComponent } from './rank-options-question-instruction.component';

describe('RankOptionsQuestionInstructionComponent', () => {
  let component: RankOptionsQuestionInstructionComponent;
  let fixture: ComponentFixture<RankOptionsQuestionInstructionComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [RankOptionsQuestionInstructionComponent],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RankOptionsQuestionInstructionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
