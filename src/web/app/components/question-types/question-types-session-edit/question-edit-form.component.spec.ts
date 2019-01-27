import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { QuestionEditFormComponent } from './question-edit-form.component';
import { QuestionTypesSessionEditModule } from './question-types-session-edit.module';

describe('QuestionEditFormComponent', () => {
  let component: QuestionEditFormComponent;
  let fixture: ComponentFixture<QuestionEditFormComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        QuestionTypesSessionEditModule,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(QuestionEditFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
