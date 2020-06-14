import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SortableTableModule } from '../../sortable-table/sortable-table.module';
import { ConstsumRecipientsQuestionStatisticsComponent } from './constsum-recipients-question-statistics.component';

describe('ConstsumRecipientsQuestionStatisticsComponent', () => {
  let component: ConstsumRecipientsQuestionStatisticsComponent;
  let fixture: ComponentFixture<ConstsumRecipientsQuestionStatisticsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ConstsumRecipientsQuestionStatisticsComponent],
      imports: [SortableTableModule],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ConstsumRecipientsQuestionStatisticsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
