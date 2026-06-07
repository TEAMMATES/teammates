import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';

import { NoRecipientsWarningComponent } from './no-recipients-warning.component';
import { QuestionRecipientType } from '../../../types/api-output';

describe('NoRecipientsWarningComponent', () => {
  let component: NoRecipientsWarningComponent;
  let fixture: ComponentFixture<NoRecipientsWarningComponent>;

  const getWarning = (): HTMLElement | null => {
    const warning = fixture.debugElement.query(By.css('.alert.alert-warning'));
    return warning ? (warning.nativeElement as HTMLElement) : null;
  };

  beforeEach(() => {
    fixture = TestBed.createComponent(NoRecipientsWarningComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show the team members warning for OWN_TEAM_MEMBERS', () => {
    component.recipientType = QuestionRecipientType.OWN_TEAM_MEMBERS;
    fixture.detectChanges();

    expect(getWarning()?.textContent).toContain(
      "This question is for team members and you don't have any team members. Therefore, you will not be able to answer " +
        'this question.',
    );
  });

  it('should show the students warning for STUDENTS', () => {
    component.recipientType = QuestionRecipientType.STUDENTS;
    fixture.detectChanges();

    expect(getWarning()?.textContent).toContain(
      "This question is for students in this course and this course doesn't have any student. Therefore, you will not " +
        'be able to answer this question.',
    );
  });

  it('should show the teams warning for TEAMS', () => {
    component.recipientType = QuestionRecipientType.TEAMS;
    fixture.detectChanges();

    expect(getWarning()?.textContent).toContain(
      "This question is for teams in this course and this course doesn't have any team. Therefore, you will not be " +
        'able to answer this question.',
    );
  });

  it('should show the other teams warning for TEAMS_EXCLUDING_SELF', () => {
    component.recipientType = QuestionRecipientType.TEAMS_EXCLUDING_SELF;
    fixture.detectChanges();

    expect(getWarning()?.textContent).toContain(
      "This question is for other teams in this course and this course doesn't have any other team. Therefore, you will " +
        'not be able to answer this question.',
    );
  });

  it('should show the other students warning for STUDENTS_EXCLUDING_SELF', () => {
    component.recipientType = QuestionRecipientType.STUDENTS_EXCLUDING_SELF;
    fixture.detectChanges();

    expect(getWarning()?.textContent).toContain(
      "This question is for other students in this course and this course doesn't have any other student. Therefore, " +
        'you will not be able to answer this question.',
    );
  });

  it('should show the same section students warning for STUDENTS_IN_SAME_SECTION', () => {
    component.recipientType = QuestionRecipientType.STUDENTS_IN_SAME_SECTION;
    fixture.detectChanges();

    expect(getWarning()?.textContent).toContain(
      "This question is for other students in your section and your section doesn't have any other student. Therefore, " +
        'you will not be able to answer this question.',
    );
  });

  it('should show the same section teams warning for TEAMS_IN_SAME_SECTION', () => {
    component.recipientType = QuestionRecipientType.TEAMS_IN_SAME_SECTION;
    fixture.detectChanges();

    expect(getWarning()?.textContent).toContain(
      "This question is for other teams in your section and your section doesn't have any other team. Therefore, you " +
        'will not be able to answer this question.',
    );
  });

  it('should show the instructors warning for INSTRUCTORS', () => {
    component.recipientType = QuestionRecipientType.INSTRUCTORS;
    fixture.detectChanges();

    expect(getWarning()?.textContent).toContain(
      'This question is for instructors and there are no instructors you can give feedback to. Therefore, you will not ' +
        'be able to answer this question.',
    );
  });

  const recipientTypesWithoutWarning: QuestionRecipientType[] = [
    QuestionRecipientType.SELF,
    QuestionRecipientType.OWN_TEAM,
    QuestionRecipientType.OWN_TEAM_MEMBERS_INCLUDING_SELF,
    QuestionRecipientType.NONE,
  ];

  recipientTypesWithoutWarning.forEach((recipientType: QuestionRecipientType) => {
    it(`should not show any warning for ${recipientType}`, () => {
      component.recipientType = recipientType;
      fixture.detectChanges();

      expect(getWarning()).toBeNull();
    });
  });
});
