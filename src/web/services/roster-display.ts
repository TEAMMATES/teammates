import { ApiStringConst } from '../types/api-const';

/**
 * Label for the internal default section (no section chosen). Not the same as a section whose
 * instructor-chosen name matches {@link RESERVED_DEFAULT_SECTION_DISPLAY}.
 */
export const RESERVED_DEFAULT_SECTION_DISPLAY = 'No specific section';

/**
 * Label for the synthetic instructor team in results. Not the same as a student team literally
 * named {@link RESERVED_INSTRUCTOR_TEAM_DISPLAY}.
 */
export const RESERVED_INSTRUCTOR_TEAM_DISPLAY = 'Instructors';

/**
 * User-visible section name: maps the reserved default section constant to {@link RESERVED_DEFAULT_SECTION_DISPLAY},
 * and disambiguates a section whose name equals that phrase but is not the reserved constant.
 */
export function formatSectionNameForDisplay(sectionName: string): string {
  if (sectionName === ApiStringConst.DEFAULT_SECTION) {
    return RESERVED_DEFAULT_SECTION_DISPLAY;
  }
  if (sectionName === RESERVED_DEFAULT_SECTION_DISPLAY) {
    return `${RESERVED_DEFAULT_SECTION_DISPLAY} (named section)`;
  }
  return sectionName;
}

/**
 * User-visible team name: maps the reserved instructor team constant to {@link RESERVED_INSTRUCTOR_TEAM_DISPLAY},
 * and disambiguates a team whose name equals that word but is not the reserved constant.
 */
export function formatTeamNameForDisplay(teamName: string): string {
  if (teamName === ApiStringConst.USER_TEAM_FOR_INSTRUCTOR) {
    return RESERVED_INSTRUCTOR_TEAM_DISPLAY;
  }
  if (teamName === RESERVED_INSTRUCTOR_TEAM_DISPLAY) {
    return `${RESERVED_INSTRUCTOR_TEAM_DISPLAY} (named team)`;
  }
  return teamName;
}

/**
 * Applies {@link formatSectionNameForDisplay} then {@link formatTeamNameForDisplay} for CSV cells that may hold either.
 */
export function formatRosterLabelForCsvCell(value: string): string {
  return formatTeamNameForDisplay(formatSectionNameForDisplay(value));
}
