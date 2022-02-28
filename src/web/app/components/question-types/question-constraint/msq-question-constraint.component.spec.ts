import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { MsqQuestionConstraintComponent } from './msq-question-constraint.component';

describe('MsqQuestionConstraintComponent', () => {
  let component: MsqQuestionConstraintComponent;
  let fixture: ComponentFixture<MsqQuestionConstraintComponent>;

  beforeEach(waitForAsync(() => {
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
