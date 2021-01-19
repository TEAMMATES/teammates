import { FeedbackParticipantType } from '../../../types/api-output';
import { GiverTypeDescriptionPipe, RecipientTypeDescriptionPipe } from './feedback-path.pipe';

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
      .toBe('Other students in the course');
  });

  it('transform FeedbackParticipantType.INSTRUCTORS', () => {
    expect(pipe.transform(FeedbackParticipantType.INSTRUCTORS))
      .toBe('Instructors in the course');
  });

  it('transform FeedbackParticipantType.TEAMS', () => {
    expect(pipe.transform(FeedbackParticipantType.TEAMS))
      .toBe('Other teams in the course');
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

  it('transform FeedbackParticipantType.TEAMS_EXCLUDING_SELF', () => {
    expect(pipe.transform(FeedbackParticipantType.TEAMS_EXCLUDING_SELF))
        .toBe('Unknown');
  });

  it('transform default', () => {
    expect(pipe.transform('' as FeedbackParticipantType))
      .toBe('Unknown');
  });

});
