import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { McqFieldComponent } from './mcq-field/mcq-field.component';
import { McqQuestionEditDetailsFormComponent } from './mcq-question-edit-details-form.component';
import { WeightFieldComponent } from './weight-field/weight-field.component';

describe('McqQuestionEditDetailsFormComponent', () => {
  let component: McqQuestionEditDetailsFormComponent;
  let fixture: ComponentFixture<McqQuestionEditDetailsFormComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        McqQuestionEditDetailsFormComponent,
        McqFieldComponent,
        WeightFieldComponent],
      imports: [
        FormsModule,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(McqQuestionEditDetailsFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
