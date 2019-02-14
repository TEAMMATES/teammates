/**
 * Represents the gender of a student.
 */
export enum Gender {
  MALE,
  FEMALE,
  OTHER,
}
export namespace Gender {

  export const enumValues: () => Gender[] = (): Gender[] => {
    const genders: Gender[] = [];
    genders.push(Gender.MALE);
    genders.push(Gender.FEMALE);
    genders.push(Gender.OTHER);
    return genders;
  };
}
