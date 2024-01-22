import { DragDropModule } from '@angular/cdk/drag-drop';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { MsqFieldComponent } from './msq-field/msq-field.component';
import { MsqQuestionEditDetailsFormComponent } from './msq-question-edit-details-form.component';
import { WeightFieldComponent } from './weight-field/weight-field.component';
import { TeammatesCommonModule } from '../../teammates-common/teammates-common.module';

describe('MsqQuestionEditDetailsFormComponent', () => {
  let component: MsqQuestionEditDetailsFormComponent;
  let fixture: ComponentFixture<MsqQuestionEditDetailsFormComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [
        MsqQuestionEditDetailsFormComponent,
        MsqFieldComponent,
        WeightFieldComponent,
      ],
      imports: [
        FormsModule,
        DragDropModule,
        TeammatesCommonModule,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MsqQuestionEditDetailsFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
