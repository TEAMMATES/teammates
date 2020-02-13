import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { TeammatesCommonModule } from '../../teammates-common/teammates-common.module';
import { McqQuestionResponseComponent } from './mcq-question-response.component';

describe('McqQuestionResponseComponent', () => {
  let component: McqQuestionResponseComponent;
  let fixture: ComponentFixture<McqQuestionResponseComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [McqQuestionResponseComponent],
      imports: [
        TeammatesCommonModule,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(McqQuestionResponseComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
