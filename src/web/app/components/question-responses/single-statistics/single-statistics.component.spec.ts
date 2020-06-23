import { HttpClientTestingModule } from '@angular/common/http/testing';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { QuestionStatisticsModule } from '../../question-types/question-statistics/question-statistics.module';
import { SingleStatisticsComponent } from './single-statistics.component';

describe('SingleStatisticsComponent', () => {
  let component: SingleStatisticsComponent;
  let fixture: ComponentFixture<SingleStatisticsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [SingleStatisticsComponent],
      imports: [QuestionStatisticsModule, HttpClientTestingModule],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SingleStatisticsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
