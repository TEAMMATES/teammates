import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { NumScaleQuestionResponseComponent } from './num-scale-question-response.component';

describe('NumScaleQuestionResponseComponent', () => {
  let component: NumScaleQuestionResponseComponent;
  let fixture: ComponentFixture<NumScaleQuestionResponseComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [NumScaleQuestionResponseComponent],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(NumScaleQuestionResponseComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
