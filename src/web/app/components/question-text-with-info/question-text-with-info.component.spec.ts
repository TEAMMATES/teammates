import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { RouterTestingModule } from '@angular/router/testing';
import {
  QuestionAdditionalInfoModule,
} from '../question-types/question-additional-info/question-additional-info.module';
import { QuestionTextWithInfoComponent } from './question-text-with-info.component';

describe('QuestionTextWithInfoComponent', () => {
  let component: QuestionTextWithInfoComponent;
  let fixture: ComponentFixture<QuestionTextWithInfoComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [QuestionTextWithInfoComponent],
      imports: [
        RouterTestingModule,
        QuestionAdditionalInfoModule,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(QuestionTextWithInfoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
