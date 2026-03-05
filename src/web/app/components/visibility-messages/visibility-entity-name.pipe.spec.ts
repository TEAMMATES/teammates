import { VisibilityEntityNamePipe } from './visibility-entity-name.pipe';
import {
    FeedbackParticipantType,
    FeedbackVisibilityType,
    NumberOfEntitiesToGiveFeedbackToSetting,
} from '../../../types/api-output';

describe('VisibilityEntityNamePipe', () => {
    let pipe: VisibilityEntityNamePipe;

    beforeEach(() => {
        pipe = new VisibilityEntityNamePipe();
    });

    it('should create', () => {
        expect(pipe).toBeTruthy();
    });

    it('transform: should return "Your team members" when visibilityType is GIVER_TEAM_MEMBERS', () => {
        expect(pipe.transform(FeedbackVisibilityType.GIVER_TEAM_MEMBERS)).toBe('Your team members');
    });

    it('transform: should return "Instructors in this course" when visibilityType is INSTRUCTORS', () => {
        expect(pipe.transform(FeedbackVisibilityType.INSTRUCTORS)).toBe('Instructors in this course');
    });

    it('transform: should return "Other students in the course" when visibilityType is STUDENTS', () => {
        expect(pipe.transform(FeedbackVisibilityType.STUDENTS)).toBe('Other students in the course');
    });

    it('transform: should return "Unknown" for invalid visibility types (default case)', () => {
        expect(pipe.transform('INVALID' as any)).toBe('Unknown');
    });

    it('transform: should return "unknown" for invalid participant types when visibility is RECIPIENT', () => {
        expect(pipe.transform(FeedbackVisibilityType.RECIPIENT, 'INVALID' as any)).toBe('unknown');
    });

    it('transform: should return singular "The receiving instructor" when setting is CUSTOM with count 1', () => {
        const result = pipe.transform(
            FeedbackVisibilityType.RECIPIENT,
            FeedbackParticipantType.INSTRUCTORS,
            NumberOfEntitiesToGiveFeedbackToSetting.CUSTOM,
            1
        );
        expect(result).toBe('The receiving instructor');
    });

    it('transform: should pluralize to "The receiving students" when setting is UNLIMITED', () => {
        const result = pipe.transform(
            FeedbackVisibilityType.RECIPIENT,
            FeedbackParticipantType.STUDENTS,
            NumberOfEntitiesToGiveFeedbackToSetting.UNLIMITED
        );
        expect(result).toBe('The receiving students');
    });

    it('transform: should pluralize to "The receiving teams" when setting is CUSTOM with count > 1', () => {
        const result = pipe.transform(
            FeedbackVisibilityType.RECIPIENT,
            FeedbackParticipantType.TEAMS,
            NumberOfEntitiesToGiveFeedbackToSetting.CUSTOM,
            2
        );
        expect(result).toBe('The receiving teams');
    });

    it('transform: should pluralize "STUDENTS_EXCLUDING_SELF" correctly', () => {
        const result = pipe.transform(
            FeedbackVisibilityType.RECIPIENT,
            FeedbackParticipantType.STUDENTS_EXCLUDING_SELF,
            NumberOfEntitiesToGiveFeedbackToSetting.UNLIMITED
        );
        expect(result).toBe('The receiving students');
    });
});