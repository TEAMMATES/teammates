import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ConstsumQuestionResponseComponent } from './constsum-question-response.component';

describe('ConstsumQuestionResponseComponent', () => {
  let component: ConstsumQuestionResponseComponent;
  let fixture: ComponentFixture<ConstsumQuestionResponseComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ConstsumQuestionResponseComponent],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ConstsumQuestionResponseComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
