import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { FormsModule } from '@angular/forms';
import { RubricQuestionEditDetailsFormComponent } from './rubric-question-edit-details-form.component';

describe('RubricQuestionEditDetailsFormComponent', () => {
  let component: RubricQuestionEditDetailsFormComponent;
  let fixture: ComponentFixture<RubricQuestionEditDetailsFormComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        FormsModule,
      ],
      declarations: [RubricQuestionEditDetailsFormComponent],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RubricQuestionEditDetailsFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
