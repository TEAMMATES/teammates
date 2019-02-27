import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TextQuestionResponseComponent } from './text-question-response.component';

describe('TextQuestionResponseComponent', () => {
  let component: TextQuestionResponseComponent;
  let fixture: ComponentFixture<TextQuestionResponseComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [TextQuestionResponseComponent],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TextQuestionResponseComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
