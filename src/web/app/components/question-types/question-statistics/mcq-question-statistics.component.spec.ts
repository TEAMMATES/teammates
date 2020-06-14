import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SortableTableModule } from '../../sortable-table/sortable-table.module';
import { McqQuestionStatisticsComponent } from './mcq-question-statistics.component';

describe('McqQuestionStatisticsComponent', () => {
  let component: McqQuestionStatisticsComponent;
  let fixture: ComponentFixture<McqQuestionStatisticsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [McqQuestionStatisticsComponent],
      imports: [SortableTableModule],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(McqQuestionStatisticsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
