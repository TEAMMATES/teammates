interface Link {
  name: string;
  link: string;
}

/**
 * Type for student banner content
 */
export interface StudentBannerContentType {
  title: string;
  content: string;
  links: Link[];
}

const studentBannerContent: StudentBannerContentType = {
  title: 'Student Banner Title',
  content: 'Student Banner Content',
  links: [
    {
      name: 'Email',
      link: 'something@something.com',
    },
  ],
};
export default studentBannerContent;
