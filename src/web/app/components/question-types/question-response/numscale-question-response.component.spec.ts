import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { NumscaleQuestionResponseComponent } from './numscale-question-response.component';

describe('NumscaleQuestionResponseComponent', () => {
  let component: NumscaleQuestionResponseComponent;
  let fixture: ComponentFixture<NumscaleQuestionResponseComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [NumscaleQuestionResponseComponent],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(NumscaleQuestionResponseComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
