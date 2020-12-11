interface Link {
    name: string;
    link: string;
}
export interface InstructorBannerContentType {
    title: string;
    content: string;
    links: Link[];
}
const InstructorBannerContent : InstructorBannerContentType = {
    title: "Instructor Banner Title",
    content: "Instructor Banner Content",
    links: [
        {
            name: "Email",
            link: "something@something.com"
        }
    ]
}
export default InstructorBannerContent;