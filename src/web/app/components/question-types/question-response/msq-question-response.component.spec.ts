import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { MsqQuestionResponseComponent } from './msq-question-response.component';

describe('MsqQuestionResponseComponent', () => {
  let component: MsqQuestionResponseComponent;
  let fixture: ComponentFixture<MsqQuestionResponseComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [MsqQuestionResponseComponent],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MsqQuestionResponseComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
