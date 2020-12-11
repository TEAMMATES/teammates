interface Link {
    name: string;
    link: string;
}
export interface StudentBannerContentType {
    title: string;
    content: string;
    links: Link[];
}
const StudentBannerContent : StudentBannerContentType = {
    title: "Student Banner Title",
    content: "Student Banner Content",
    links: [
        {
            name: "Email",
            link: "something@something.com"
        }
    ]
};
export default StudentBannerContent;