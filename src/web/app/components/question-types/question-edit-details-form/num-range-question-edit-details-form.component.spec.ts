import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NumRangeQuestionEditDetailsFormComponent } from './num-range-question-edit-details-form.component';

describe('NumRangeQuestionEditDetailsFormComponent', () => {
  let component: NumRangeQuestionEditDetailsFormComponent;
  let fixture: ComponentFixture<NumRangeQuestionEditDetailsFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ NumRangeQuestionEditDetailsFormComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(NumRangeQuestionEditDetailsFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
