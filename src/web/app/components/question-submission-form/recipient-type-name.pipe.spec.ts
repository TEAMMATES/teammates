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

  it('should return "Team" for TEAMS', () => {
    expect(pipe.transform(FeedbackParticipantType.TEAMS, FeedbackParticipantType.STUDENTS)).toEqual('Team');
  });

  it('should return "Student" for STUDENTS', () => {
    expect(pipe.transform(FeedbackParticipantType.STUDENTS, FeedbackParticipantType.STUDENTS)).toEqual('Student');
  });

  it('should return "Instructor" for INSTRUCTORS', () => {
    expect(pipe.transform(FeedbackParticipantType.INSTRUCTORS, FeedbackParticipantType.STUDENTS)).toEqual('Instructor');
  });

  // ... you can continue with the other FeedbackParticipantTypes

  describe('for OWN_TEAM_MEMBERS and OWN_TEAM_MEMBERS_INCLUDING_SELF', () => {
    it('should return "Student" when giverType is STUDENTS', () => {
      expect(pipe.transform(FeedbackParticipantType.OWN_TEAM_MEMBERS, FeedbackParticipantType.STUDENTS)).toEqual('Student');
      expect(pipe.transform(FeedbackParticipantType.OWN_TEAM_MEMBERS_INCLUDING_SELF, FeedbackParticipantType.STUDENTS)).toEqual('Student');
    });

    it('should return "Instructor" when giverType is INSTRUCTORS', () => {
      expect(pipe.transform(FeedbackParticipantType.OWN_TEAM_MEMBERS, FeedbackParticipantType.INSTRUCTORS)).toEqual('Instructor');
      expect(pipe.transform(FeedbackParticipantType.OWN_TEAM_MEMBERS_INCLUDING_SELF, FeedbackParticipantType.INSTRUCTORS)).toEqual('Instructor');
    });

    it('should return "Student" when giverType is TEAMS', () => {
      expect(pipe.transform(FeedbackParticipantType.OWN_TEAM_MEMBERS, FeedbackParticipantType.TEAMS)).toEqual('Student');
      expect(pipe.transform(FeedbackParticipantType.OWN_TEAM_MEMBERS_INCLUDING_SELF, FeedbackParticipantType.TEAMS)).toEqual('Student');
    });

    it('should return "Unknown" for any other giverType', () => {
      expect(pipe.transform(FeedbackParticipantType.OWN_TEAM_MEMBERS, 'ANY_OTHER_TYPE' as FeedbackParticipantType)).toEqual('Unknown');
      expect(pipe.transform(FeedbackParticipantType.OWN_TEAM_MEMBERS_INCLUDING_SELF, 'ANY_OTHER_TYPE' as FeedbackParticipantType)).toEqual('Unknown');
    });
  });
});
