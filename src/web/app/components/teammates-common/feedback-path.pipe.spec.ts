import {
  GiverTypeDescriptionPipe,
  RecipientTypeDescriptionPipe,
  RecipientTypeSimplifiedDescriptionPipe,
} from './feedback-path.pipe';
import { FeedbackParticipantType } from '../../../types/api-output';

describe('GiverTypeDescriptionPipe', () => {
  const pipe: GiverTypeDescriptionPipe = new GiverTypeDescriptionPipe();

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('transform FeedbackParticipantType.SELF', () => {
    expect(pipe.transform(FeedbackParticipantType.SELF))
      .toBe('Feedback session creator (i.e., me)');
  });

  it('transform FeedbackParticipantType.STUDENTS', () => {
    expect(pipe.transform(FeedbackParticipantType.STUDENTS))
      .toBe('Students in this course');
  });

  it('transform FeedbackParticipantType.INSTRUCTORS', () => {
    expect(pipe.transform(FeedbackParticipantType.INSTRUCTORS))
      .toBe('Instructors in this course');
  });

  it('transform FeedbackParticipantType.TEAMS', () => {
    expect(pipe.transform(FeedbackParticipantType.TEAMS))
      .toBe('Teams in this course');
  });

  it('transform FeedbackParticipantType.OWN_TEAM', () => {
    expect(pipe.transform(FeedbackParticipantType.OWN_TEAM))
        .toBe('Unknown');
  });

  it('transform default', () => {
    expect(pipe.transform('' as FeedbackParticipantType))
      .toBe('Unknown');
  });

});

describe('RecipientTypeDescriptionPipe', () => {
  const pipe: RecipientTypeDescriptionPipe = new RecipientTypeDescriptionPipe();

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('transform FeedbackParticipantType.SELF', () => {
    expect(pipe.transform(FeedbackParticipantType.SELF))
      .toBe('Giver (Self feedback)');
  });

  it('transform FeedbackParticipantType.STUDENTS', () => {
    expect(pipe.transform(FeedbackParticipantType.STUDENTS))
      .toBe('Students in the course');
  });

  it('transform FeedbackParticipantType.STUDENTS_EXCLUDING_SELF', () => {
    expect(pipe.transform(FeedbackParticipantType.STUDENTS_EXCLUDING_SELF))
        .toBe('Other students in the course');
  });

  it('transform FeedbackParticipantType.STUDENTS_IN_SAME_SECTION', () => {
    expect(pipe.transform(FeedbackParticipantType.STUDENTS_IN_SAME_SECTION))
      .toBe('Other students in the same section');
  });

  it('transform FeedbackParticipantType.INSTRUCTORS', () => {
    expect(pipe.transform(FeedbackParticipantType.INSTRUCTORS))
      .toBe('Instructors in the course');
  });

  it('transform FeedbackParticipantType.TEAMS', () => {
    expect(pipe.transform(FeedbackParticipantType.TEAMS))
      .toBe('Teams in the course');
  });

  it('transform FeedbackParticipantType.TEAMS_EXCLUDING_SELF', () => {
    expect(pipe.transform(FeedbackParticipantType.TEAMS_EXCLUDING_SELF))
        .toBe('Other teams in the course');
  });

  it('transform FeedbackParticipantType.TEAMS_IN_SAME_SECTION', () => {
    expect(pipe.transform(FeedbackParticipantType.TEAMS_IN_SAME_SECTION))
      .toBe('Other teams in the same section');
  });

  it('transform FeedbackParticipantType.OWN_TEAM', () => {
    expect(pipe.transform(FeedbackParticipantType.OWN_TEAM))
      .toBe("Giver's team");
  });

  it('transform FeedbackParticipantType.OWN_TEAM_MEMBERS', () => {
    expect(pipe.transform(FeedbackParticipantType.OWN_TEAM_MEMBERS))
      .toBe("Giver's team members");
  });

  it('transform FeedbackParticipantType.OWN_TEAM_MEMBERS_INCLUDING_SELF', () => {
    expect(pipe.transform(FeedbackParticipantType.OWN_TEAM_MEMBERS_INCLUDING_SELF))
      .toBe("Giver's team members and Giver");
  });

  it('transform FeedbackParticipantType.NONE', () => {
    expect(pipe.transform(FeedbackParticipantType.NONE))
      .toBe('Nobody specific (For general class feedback)');
  });

  it('transform default', () => {
    expect(pipe.transform('' as FeedbackParticipantType))
      .toBe('Unknown');
  });

});

describe('RecipientTypeSimplifiedDescriptionPipe', () => {
  const pipe: RecipientTypeSimplifiedDescriptionPipe = new RecipientTypeSimplifiedDescriptionPipe();

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('transform FeedbackParticipantType.STUDENTS', () => {
    expect(pipe.transform(FeedbackParticipantType.STUDENTS))
        .toBe('students');
  });

  it('transform FeedbackParticipantType.STUDENTS_EXCLUDING_SELF', () => {
    expect(pipe.transform(FeedbackParticipantType.STUDENTS_EXCLUDING_SELF))
        .toBe('students');
  });

  it('transform FeedbackParticipantType.STUDENTS_IN_SAME_SECTION', () => {
    expect(pipe.transform(FeedbackParticipantType.STUDENTS_IN_SAME_SECTION))
        .toBe('students');
  });

  it('transform FeedbackParticipantType.INSTRUCTORS', () => {
    expect(pipe.transform(FeedbackParticipantType.INSTRUCTORS))
        .toBe('instructors');
  });

  it('transform FeedbackParticipantType.TEAMS', () => {
    expect(pipe.transform(FeedbackParticipantType.TEAMS))
        .toBe('teams');
  });

  it('transform FeedbackParticipantType.TEAMS_EXCLUDING_SELF', () => {
    expect(pipe.transform(FeedbackParticipantType.TEAMS_EXCLUDING_SELF))
        .toBe('teams');
  });

  it('transform FeedbackParticipantType.TEAMS_IN_SAME_SECTION', () => {
    expect(pipe.transform(FeedbackParticipantType.TEAMS_IN_SAME_SECTION))
        .toBe('teams');
  });

  it('transform default', () => {
    expect(pipe.transform('' as FeedbackParticipantType))
        .toBe('Unknown');
  });

});
