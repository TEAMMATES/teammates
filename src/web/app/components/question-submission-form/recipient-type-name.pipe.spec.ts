import { RecipientTypeNamePipe } from './recipient-type-name.pipe';
import { FeedbackParticipantType } from '../../../types/api-output';

describe('RecipientTypeNamePipe', () => {
  let pipe: RecipientTypeNamePipe;

  beforeEach(() => {
    pipe = new RecipientTypeNamePipe();
  });

  it('should create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should return "Team" for recipientType TEAMS', () => {
    expect(pipe.transform(FeedbackParticipantType.TEAMS,
        FeedbackParticipantType.STUDENTS)).toEqual('Team');
  });

  it('should return "Student" for recipientType STUDENTS', () => {
    expect(pipe.transform(FeedbackParticipantType.STUDENTS,
        FeedbackParticipantType.STUDENTS)).toEqual('Student');
  });

  it('should return "Instructor" for recipientType INSTRUCTORS', () => {
    expect(pipe.transform(FeedbackParticipantType.INSTRUCTORS,
        FeedbackParticipantType.STUDENTS)).toEqual('Instructor');
  });

  it('should return "Student" for OWN_TEAM_MEMBERS with giverType STUDENTS', () => {
    expect(pipe.transform(FeedbackParticipantType.OWN_TEAM_MEMBERS,
        FeedbackParticipantType.STUDENTS)).toEqual('Student');
  });

  it('should return "Instructor" for OWN_TEAM_MEMBERS with giverType INSTRUCTORS', () => {
    expect(pipe.transform(FeedbackParticipantType.OWN_TEAM_MEMBERS,
        FeedbackParticipantType.INSTRUCTORS)).toEqual('Instructor');
  });

  it('should return "Student" for OWN_TEAM_MEMBERS with giverType TEAMS', () => {
    expect(pipe.transform(FeedbackParticipantType.OWN_TEAM_MEMBERS,
        FeedbackParticipantType.TEAMS)).toEqual('Student');
  });

  it('should return "Unknown" for OWN_TEAM_MEMBERS with unknown giverType', () => {
    expect(pipe.transform(FeedbackParticipantType.OWN_TEAM_MEMBERS,
        'UNKNOWN' as any)).toEqual('Unknown');
  });

  it('should return "Unknown" for unknown recipientType', () => {
    expect(pipe.transform('UNKNOWN' as any, FeedbackParticipantType.STUDENTS)).toEqual('Unknown');
  });
});
