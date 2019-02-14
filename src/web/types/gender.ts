/**
 * Represents the gender of a student.
 */
export enum Gender {
  MALE,
  FEMALE,
  OTHER,
}
export namespace Gender {

  export const values: () => string[] = (): string[] =>
      Object.keys(Gender).filter((key: string) => isNaN(key as any) && key !== 'values');

  export const enums: () => Gender[] = (): Gender[] => {
    const genders: Gender[] = [];
    genders.push(Gender.MALE);
    genders.push(Gender.FEMALE);
    genders.push(Gender.OTHER);
    return genders;
  };
}
