import { Course, Instructor, JoinState, Student } from '../types/api-output';

type GenericBuilder<T> = {
  [K in keyof T]: (value: T[K]) => GenericBuilder<T>;
} & { build(): T };

/**
 * A generic builder function that creates a builder object for constructing objects with specified initial values.
 *
 * @template T - The type of object being constructed.
 * @param initialValues - The initial values for the object being constructed.
 * @returns A generic builder object.
 * @example
 * // Create a course builder with initial values.
 * const courseBuilder = createBuilder<Course>({
 *   courseId: 'exampleId',
 *   courseName: '',
 *   timeZone: '',
 *   institute: '',
 *   creationTimestamp: 0,
 *   deletionTimestamp: 0,
 * });
 *
 * // Usage of builder:
 * const myCourse = courseBuilder
 *   .courseName('Introduction to TypeScript')
 *   .timeZone('UTC+0')
 *   .institute('My University')
 *   .creationTimestamp(Date.now())
 *   .build();
 */
export function createBuilder<T extends object>(initialValues: T): GenericBuilder<T> {
  const builder: any = {};

  (Object.keys(initialValues) as (keyof T)[]).forEach((key) => {
    builder[key] = (value: T[keyof T]) => {
      initialValues[key] = value;
      return builder;
    };
  });

  builder.build = () => ({ ...initialValues });

  return builder;
}

export const courseBuilder = createBuilder<Course>({
  courseId: 'exampleId',
  courseName: '',
  timeZone: '',
  institute: '',
  creationTimestamp: 0,
  deletionTimestamp: 0,
});

export const instructorBuilder = createBuilder<Instructor>({
  courseId: 'exampleId',
  email: '',
  name: '',
  joinState: JoinState.JOINED,
});

export const studentBuilder = createBuilder<Student>({
  courseId: 'exampleId',
  email: 'examplestudent@gmail.com',
  name: 'test-student',
  teamName: 'test-team-name',
  sectionName: 'test-section-name',
});
