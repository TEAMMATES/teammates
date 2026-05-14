import { QuestionGiverType, QuestionRecipientType } from '../../../types/api-output';
import { RecipientTypeNamePipe } from './recipient-type-name.pipe';

describe('RecipientTypeNamePipe', () => {
  let pipe: RecipientTypeNamePipe;

  beforeEach(() => {
    pipe = new RecipientTypeNamePipe();
  });

  it('should create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should return "Team" for recipientType TEAMS', () => {
    expect(pipe.transform(QuestionRecipientType.TEAMS, QuestionGiverType.STUDENTS)).toEqual('Team');
  });

  it('should return "Student" for recipientType STUDENTS', () => {
    expect(pipe.transform(QuestionRecipientType.STUDENTS, QuestionGiverType.STUDENTS)).toEqual('Student');
  });

  it('should return "Instructor" for recipientType INSTRUCTORS', () => {
    expect(pipe.transform(QuestionRecipientType.INSTRUCTORS, QuestionGiverType.STUDENTS)).toEqual('Instructor');
  });

  it('should return "Student" for OWN_TEAM_MEMBERS with giverType STUDENTS', () => {
    expect(pipe.transform(QuestionRecipientType.OWN_TEAM_MEMBERS, QuestionGiverType.STUDENTS)).toEqual('Student');
  });

  it('should return "Instructor" for OWN_TEAM_MEMBERS with giverType INSTRUCTORS', () => {
    expect(pipe.transform(QuestionRecipientType.OWN_TEAM_MEMBERS, QuestionGiverType.INSTRUCTORS)).toEqual('Instructor');
  });

  it('should return "Student" for OWN_TEAM_MEMBERS with giverType TEAMS', () => {
    expect(pipe.transform(QuestionRecipientType.OWN_TEAM_MEMBERS, QuestionGiverType.TEAMS)).toEqual('Student');
  });

  it('should return "Unknown" for OWN_TEAM_MEMBERS with unknown giverType', () => {
    expect(pipe.transform(QuestionRecipientType.OWN_TEAM_MEMBERS, 'UNKNOWN' as any)).toEqual('Unknown');
  });

  it('should return "Unknown" for unknown recipientType', () => {
    expect(pipe.transform('UNKNOWN' as any, QuestionGiverType.STUDENTS)).toEqual('Unknown');
  });
});
