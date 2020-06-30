/**
 * Represents how responses whose giver/evaluee comes from certain sections should be displayed or not.
 */
export enum InstructorSessionResultSectionType {

  /**
   * Show response if either the giver or evaluee is in the selected section
   */
  EITHER = 'EITHER',

  /**
   * Show response if the giver is in the selected section
   */
  GIVER = 'GIVER',

  /**
   * Show response if the evaluee is in the selected section
   */
  EVALUEE = 'EVALUEE',

  /**
   * Show response only if both are in the selected section
   */
  BOTH = 'BOTH',

}
