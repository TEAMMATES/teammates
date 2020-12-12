interface Link {
  name: string;
  link: string;
}

/**
 * Type for instructor banner content
 */
export interface InstructorBannerContentType {
  title: string;
  content: string;
  links: Link[];
}

const instructorBannerContent: InstructorBannerContentType = {
  title: 'Instructor Banner Title',
  content: 'Instructor Banner Content',
  links: [
    {
      name: 'Email',
      link: 'something@something.com',
    },
  ],
};
export default instructorBannerContent;
