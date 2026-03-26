import { ApiStringConst } from '../types/api-const';
import {
  RESERVED_DEFAULT_SECTION_DISPLAY,
  RESERVED_INSTRUCTOR_TEAM_DISPLAY,
  formatRosterLabelForCsvCell,
  formatSectionNameForDisplay,
  formatTeamNameForDisplay,
} from './roster-display';

describe('roster-display', () => {
  describe('formatSectionNameForDisplay', () => {
    it('should map reserved default section constant to the standard label', () => {
      expect(formatSectionNameForDisplay(ApiStringConst.DEFAULT_SECTION)).toEqual(RESERVED_DEFAULT_SECTION_DISPLAY);
    });

    it('should disambiguate a section literally named like the default label', () => {
      expect(formatSectionNameForDisplay(RESERVED_DEFAULT_SECTION_DISPLAY)).toEqual(
          `${RESERVED_DEFAULT_SECTION_DISPLAY} (named section)`);
    });

    it('should pass through other section names', () => {
      expect(formatSectionNameForDisplay('Tutorial 1')).toEqual('Tutorial 1');
    });
  });

  describe('formatTeamNameForDisplay', () => {
    it('should map reserved instructor team constant to the standard label', () => {
      expect(formatTeamNameForDisplay(ApiStringConst.USER_TEAM_FOR_INSTRUCTOR)).toEqual(
          RESERVED_INSTRUCTOR_TEAM_DISPLAY);
    });

    it('should disambiguate a team literally named Instructors', () => {
      expect(formatTeamNameForDisplay(RESERVED_INSTRUCTOR_TEAM_DISPLAY)).toEqual(
          `${RESERVED_INSTRUCTOR_TEAM_DISPLAY} (named team)`);
    });

    it('should pass through other team names', () => {
      expect(formatTeamNameForDisplay('Team A')).toEqual('Team A');
    });
  });

  describe('formatRosterLabelForCsvCell', () => {
    it('should apply section then team formatting', () => {
      expect(formatRosterLabelForCsvCell(ApiStringConst.DEFAULT_SECTION)).toEqual(RESERVED_DEFAULT_SECTION_DISPLAY);
      expect(formatRosterLabelForCsvCell(ApiStringConst.USER_TEAM_FOR_INSTRUCTOR)).toEqual(
          RESERVED_INSTRUCTOR_TEAM_DISPLAY);
    });
  });
});
