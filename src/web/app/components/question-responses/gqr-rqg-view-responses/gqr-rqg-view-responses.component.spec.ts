import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { QuestionTextWithInfoModule } from '../../question-text-with-info/question-text-with-info.module';
import { PerQuestionViewResponsesModule } from '../per-question-view-responses/per-question-view-responses.module';
import { GqrRqgViewResponsesComponent } from './gqr-rqg-view-responses.component';

describe('GqrRqgViewResponsesComponent', () => {
  let component: GqrRqgViewResponsesComponent;
  let fixture: ComponentFixture<GqrRqgViewResponsesComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [GqrRqgViewResponsesComponent],
      imports: [
        QuestionTextWithInfoModule,
        PerQuestionViewResponsesModule,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(GqrRqgViewResponsesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
