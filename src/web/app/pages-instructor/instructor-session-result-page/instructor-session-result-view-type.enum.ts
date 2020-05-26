/**
 * Represents the different supported view types in instructor sessions result page.
 */
export enum InstructorSessionResultViewType {

  /**
   * Organize responses by questions.
   */
  QUESTION = 'QUESTION',

  /**
   * Organize responses by giver name, then recipient name, then questions.
   */
  GRQ = 'GRQ',

  /**
   * Organize responses by recipient name, then giver name, then questions.
   */
  RGQ = 'RGQ',

  /**
   * Organize responses by giver name, then questions.
   */
  GQR = 'GQR',

  /**
   * Organize responses by recipient name, then questions.
   */
  RQG = 'RQG',

}
