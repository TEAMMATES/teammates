import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { RankOptionsQuestionInstructionComponent } from './rank-options-question-instruction.component';

describe('RankOptionsQuestionInstructionComponent', () => {
  let component: RankOptionsQuestionInstructionComponent;
  let fixture: ComponentFixture<RankOptionsQuestionInstructionComponent>;

  beforeEach(async(() => {
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
