import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { MsqQuestionConstraintComponent } from './msq-question-constraint.component';

describe('MsqQuestionConstraintComponent', () => {
  let component: MsqQuestionConstraintComponent;
  let fixture: ComponentFixture<MsqQuestionConstraintComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [MsqQuestionConstraintComponent],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MsqQuestionConstraintComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
