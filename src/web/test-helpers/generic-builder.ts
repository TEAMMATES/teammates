import { Course, Instructor, JoinState } from '../types/api-output';

type GenericBuilder<T> = {
  [K in keyof T]: (value: T[K]) => GenericBuilder<T>;
} & { build(): T };

export function createBuilder<T>(initialValues: T): GenericBuilder<T> {
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
