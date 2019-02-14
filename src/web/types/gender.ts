/**
 * Represents the gender of a student.
 */
export enum Gender {

  /**
   * Male gender.
   */
  MALE,

  /**
   * Female gender.
   */
  FEMALE,

  /**
   * Not specified gender.
   */
  OTHER,
}

export namespace Gender {

  /**
   * Returns an array containing the values of Gender enum.
   */
  export const enumValues: () => Gender[] = (): Gender[] => {
    const genders: Gender[] = [];
    genders.push(Gender.MALE);
    genders.push(Gender.FEMALE);
    genders.push(Gender.OTHER);
    return genders;
  };
}
