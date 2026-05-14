import { QuestionGiverType, QuestionRecipientType } from '../../../types/api-output';
import {
  GiverTypeDescriptionPipe,
  RecipientTypeDescriptionPipe,
  RecipientTypeSimplifiedDescriptionPipe,
} from './feedback-path.pipe';

describe('GiverTypeDescriptionPipe', () => {
  const pipe: GiverTypeDescriptionPipe = new GiverTypeDescriptionPipe();

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('transform QuestionRecipientType.SELF', () => {
    expect(pipe.transform(QuestionGiverType.SELF)).toBe('Feedback session creator (i.e., me)');
  });

  it('transform QuestionGiverType.STUDENTS', () => {
    expect(pipe.transform(QuestionGiverType.STUDENTS)).toBe('Students in this course');
  });

  it('transform QuestionGiverType.INSTRUCTORS', () => {
    expect(pipe.transform(QuestionGiverType.INSTRUCTORS)).toBe('Instructors in this course');
  });

  it('transform QuestionGiverType.TEAMS', () => {
    expect(pipe.transform(QuestionGiverType.TEAMS)).toBe('Teams in this course');
  });

  it('transform QuestionGiverType.OWN_TEAM', () => {
    expect(pipe.transform('OWN TEAM' as QuestionGiverType)).toBe('Unknown');
  });

  it('transform default', () => {
    expect(pipe.transform('' as QuestionGiverType)).toBe('Unknown');
  });
});

describe('RecipientTypeDescriptionPipe', () => {
  const pipe: RecipientTypeDescriptionPipe = new RecipientTypeDescriptionPipe();

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('transform QuestionRecipientType.SELF', () => {
    expect(pipe.transform(QuestionRecipientType.SELF)).toBe('Giver (Self feedback)');
  });

  it('transform QuestionRecipientType.STUDENTS', () => {
    expect(pipe.transform(QuestionRecipientType.STUDENTS)).toBe('Students in the course');
  });

  it('transform QuestionRecipientType.STUDENTS_EXCLUDING_SELF', () => {
    expect(pipe.transform(QuestionRecipientType.STUDENTS_EXCLUDING_SELF)).toBe('Other students in the course');
  });

  it('transform QuestionRecipientType.STUDENTS_IN_SAME_SECTION', () => {
    expect(pipe.transform(QuestionRecipientType.STUDENTS_IN_SAME_SECTION)).toBe('Other students in the same section');
  });

  it('transform QuestionRecipientType.INSTRUCTORS', () => {
    expect(pipe.transform(QuestionRecipientType.INSTRUCTORS)).toBe('Instructors in the course');
  });

  it('transform QuestionRecipientType.TEAMS', () => {
    expect(pipe.transform(QuestionRecipientType.TEAMS)).toBe('Teams in the course');
  });

  it('transform QuestionRecipientType.TEAMS_EXCLUDING_SELF', () => {
    expect(pipe.transform(QuestionRecipientType.TEAMS_EXCLUDING_SELF)).toBe('Other teams in the course');
  });

  it('transform QuestionRecipientType.TEAMS_IN_SAME_SECTION', () => {
    expect(pipe.transform(QuestionRecipientType.TEAMS_IN_SAME_SECTION)).toBe('Other teams in the same section');
  });

  it('transform QuestionRecipientType.OWN_TEAM', () => {
    expect(pipe.transform(QuestionRecipientType.OWN_TEAM)).toBe("Giver's team");
  });

  it('transform QuestionRecipientType.OWN_TEAM_MEMBERS', () => {
    expect(pipe.transform(QuestionRecipientType.OWN_TEAM_MEMBERS)).toBe("Giver's team members");
  });

  it('transform QuestionRecipientType.OWN_TEAM_MEMBERS_INCLUDING_SELF', () => {
    expect(pipe.transform(QuestionRecipientType.OWN_TEAM_MEMBERS_INCLUDING_SELF)).toBe(
      "Giver's team members and Giver",
    );
  });

  it('transform QuestionRecipientType.NONE', () => {
    expect(pipe.transform(QuestionRecipientType.NONE)).toBe('Nobody specific (For general class feedback)');
  });

  it('transform default', () => {
    expect(pipe.transform('' as QuestionRecipientType)).toBe('Unknown');
  });
});

describe('RecipientTypeSimplifiedDescriptionPipe', () => {
  const pipe: RecipientTypeSimplifiedDescriptionPipe = new RecipientTypeSimplifiedDescriptionPipe();

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('transform QuestionRecipientType.STUDENTS', () => {
    expect(pipe.transform(QuestionRecipientType.STUDENTS)).toBe('students');
  });

  it('transform QuestionRecipientType.STUDENTS_EXCLUDING_SELF', () => {
    expect(pipe.transform(QuestionRecipientType.STUDENTS_EXCLUDING_SELF)).toBe('students');
  });

  it('transform QuestionRecipientType.STUDENTS_IN_SAME_SECTION', () => {
    expect(pipe.transform(QuestionRecipientType.STUDENTS_IN_SAME_SECTION)).toBe('students');
  });

  it('transform QuestionRecipientType.INSTRUCTORS', () => {
    expect(pipe.transform(QuestionRecipientType.INSTRUCTORS)).toBe('instructors');
  });

  it('transform QuestionRecipientType.TEAMS', () => {
    expect(pipe.transform(QuestionRecipientType.TEAMS)).toBe('teams');
  });

  it('transform QuestionRecipientType.TEAMS_EXCLUDING_SELF', () => {
    expect(pipe.transform(QuestionRecipientType.TEAMS_EXCLUDING_SELF)).toBe('teams');
  });

  it('transform QuestionRecipientType.TEAMS_IN_SAME_SECTION', () => {
    expect(pipe.transform(QuestionRecipientType.TEAMS_IN_SAME_SECTION)).toBe('teams');
  });

  it('transform default', () => {
    expect(pipe.transform('' as QuestionRecipientType)).toBe('Unknown');
  });
});
