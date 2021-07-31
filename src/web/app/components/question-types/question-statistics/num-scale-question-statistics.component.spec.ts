import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SortableTableModule } from '../../sortable-table/sortable-table.module';
import { NumScaleQuestionStatisticsComponent } from './num-scale-question-statistics.component';
import ResponseTestData from './test-data/numScaleQuestionResponses.json';

describe('NumScaleQuestionStatisticsComponent', () => {
  let component: NumScaleQuestionStatisticsComponent;
  let fixture: ComponentFixture<NumScaleQuestionStatisticsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [NumScaleQuestionStatisticsComponent],
      imports: [SortableTableModule],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(NumScaleQuestionStatisticsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should calculate statistics correctly', () => {
    component.responses = JSON.parse(JSON.stringify(ResponseTestData.responses));
    component.question.maxScale = 5;
    component.question.minScale = 0;

    const expectedMin: number = 1;
    const expectedMax: number = 5;
    const expectedAverage: number = 2.67;
    const expectedAverageExcludingSelf: number = 2.67;

    component.calculateStatistics();

    const team: string = 'Instructors';
    const recipient: string = 'Instructor';
    expect(component.teamToRecipientToScores
        [team][recipient].min).toEqual(expectedMin);
    expect(component.teamToRecipientToScores
        [team][recipient].max).toEqual(expectedMax);
    expect(component.teamToRecipientToScores
        [team][recipient].average).toEqual(expectedAverage);
    expect(component.teamToRecipientToScores
        [team][recipient].averageExcludingSelf).toEqual(expectedAverageExcludingSelf);
  });

  it('should calculate statistics correctly if responses are zero', () => {
    component.responses = JSON.parse(JSON.stringify(ResponseTestData.responsesAtZero));
    component.question.maxScale = 5;
    component.question.minScale = 0;

    const expectedMin: number = 0;
    const expectedMax: number = 0;
    const expectedAverage: number = 0;
    const expectedAverageExcludingSelf: number = 0;

    component.calculateStatistics();

    const team: string = 'Instructors';
    const recipient: string = 'Instructor';
    expect(component.teamToRecipientToScores
        [team][recipient].min).toEqual(expectedMin);
    expect(component.teamToRecipientToScores
        [team][recipient].max).toEqual(expectedMax);
    expect(component.teamToRecipientToScores
        [team][recipient].average).toEqual(expectedAverage);
    expect(component.teamToRecipientToScores
        [team][recipient].averageExcludingSelf).toEqual(expectedAverageExcludingSelf);

  });

  it('should calculate statistics correctly if self-response exists', () => {
    component.responses = JSON.parse(JSON.stringify(ResponseTestData.responsesWithSelf));
    component.question.maxScale = 5;
    component.question.minScale = 0;

    const expectedMin: number = 2;
    const expectedMax: number = 5;
    const expectedAverage: number = 3.5;
    const expectedAverageExcludingSelf: number = 3;

    component.calculateStatistics();

    const team: string = 'Instructors';
    const recipient: string = 'Instructor';
    expect(component.teamToRecipientToScores
        [team][recipient].min).toEqual(expectedMin);
    expect(component.teamToRecipientToScores
        [team][recipient].max).toEqual(expectedMax);
    expect(component.teamToRecipientToScores
        [team][recipient].average).toEqual(expectedAverage);
    expect(component.teamToRecipientToScores
        [team][recipient].averageExcludingSelf).toEqual(expectedAverageExcludingSelf);
  });
});
