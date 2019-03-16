/**
 * Represents how responses whose giver/evaluee comes from certain sections should be displayed or not.
 */
export enum InstructorSessionResultSectionType {

  /**
   * Show response if either the giver or evaluee is in the selected section
   */
  EITHER,

  /**
   * Show response if the giver is in the selected section
   */
  GIVER,

  /**
   * Show response if the evaluee is in the selected section
   */
  EVALUEE,

  /**
   * Show response only if both are in the selected section
   */
  BOTH,

}
