#TEAMMATES Developer Web Site

TEAMMATES is a free online tool for managing peer evaluations and other 
feedback paths of your students. It is provided as a cloud-based service for 
educators/students and is currently used by several universities across the world.
This is the developer web site for TEAMMATES. Click [here](http://teammatesv4.appspot.com/)
 to go to the TEAMMATES product website

<img src="http://4-19.teammatesv4.appspot.com/images/overview.png" width='600'>

<img src="https://teammatesv4.appspot.com/images/raised-edge.png" width='600'>

##Interested to join TEAMMATES developer team?

We welcome contributions from developers, especially students. Here are some resources: 
  * [**Contributor Orientation Guide**](https://docs.google.com/document/d/1cY9pmEmw4dC6Z4LjN1WUTsynJ0jVPgbWsFhdyBy_wCU/pub?embedded=true) : This document describes what you need to know/do to become a contributor.
  * [**Project ideas page**] (https://docs.google.com/document/d/1fAvYvQr0E93OsZgyneaXGX0jaMA-zptTIxqLn83xwN0/pub?embedded=true) : These are for those who would like to do a relatively bigger projects with TEAMMATES (e.g., summer internships).
  * [**Instructions for Google Summer of Code applicants**] (https://docs.google.com/document/d/1Iu63WRIw8uz8liEW089IQHDQTRbh-QoOLMyfTPXNOa0/pub?embedded=true) : Read this before you apply to work for TEAMMATES under GSoC.

##Documentation For Developers
Here are some documents important for TEAMMATES developers:

### Main documents
  * [**Specification**](https://docs.google.com/document/d/1hjQQHYM3YId0EUSrGnJWG5AeFpDD_G7xg_d--7jg3vU/pub?embedded=true) 
    (short name: **Spec**) : Product specification, written from the developers 
    point of view. The user's point of view is [here](http://teammatesv4.appspot.com/features.html).
  * The project [**vision, challenges, and principles**](http://teammatesv4.appspot.com/dev/devman.html#project) from a developer's perspective.
  * Software [**Design**](http://teammatesv4.appspot.com/dev/devman.html#design) - Architecture, APIs, etc.
  * How to [**set up the developer environment**](/devdocs/settingUp.md)- e.g., tools and libraries used.
  * The development [**process**](/devdocs/process.md).
  
  
### Supplementary documents 
  * [**Resources bin**](https://docs.google.com/document/pub?id=1LymZ6oeEA6TZRzgW7X2FUxA2MPbZTjUrzIx6si_5ThI&embedded=true)
    : Learning resources for the use of developers (tutorials, cheat sheets etc).
  * [**Decision analysis**](https://docs.google.com/document/pub?id=1o6pNPshCp9S31ymHY0beQ1DVafDa1_k_k7bpxZo5GeU&embedded=true)
    : Analysis of problems encountered, solutions considered, rationale for selection etc.
  * The [**glossary**](http://teammatesv4.appspot.com/dev/devman.html#glossary) of terms used in the project.
  * **Coding standards** for :
    [Java](https://docs.google.com/document/pub?id=1iAESIXM0zSxEa5OY7dFURam_SgLiSMhPQtU0drQagrs&embedded=true), 
    [JSP](http://teammatesv4.appspot.com/dev/jspstyleguide.html), 
    [JavaScript](http://google-styleguide.googlecode.com/svn/trunk/javascriptguide.xml),
    [CSS](https://docs.google.com/document/d/1wA9paRA9cS7ByStGbhRRUZLEzEzimrNQjIDPVqy1ScI/pub), 
    [HTML](http://teammatesv4.appspot.com/dev/htmlstyleguide.html)
  * [**Developer Troubleshooting Guide**](https://docs.google.com/document/d/1_p7WOGryOStPfTGA_ZifE1kVlskb1zfd3HZwc4lE4QQ/pub?embedded=true)
  * **Best practices** for : 
    [UI design](https://docs.google.com/document/d/1Vj59--sCYUfxgg-iLI53hMfc6YY1gguATTN_KXTQVUo/pub?embedded=true), 
    [Coding](https://docs.google.com/document/d/14EFJzdhp10qQ9iZ-FwsS1FAf42voe4exrlbefo_zYaU/pub?embedded=true), 
    [Testing](https://docs.google.com/document/d/1aK-1ubIA59fbNsEujwvWKqx7itwLPTD6mzFo4F_oEoI/pub?embedded=true)
  * [**Version History**](https://github.com/TEAMMATES/repo/milestones?direction=desc&sort=due_date&state=closed)
  
   
  * **Technical reports** about TEAMMATES : In-depth descriptions about various aspects of the project
      * [An Analysis of Question Types](https://docs.google.com/document/d/1SH8VkaUH_kv3bT3c8AKiPDJS2Y-XhzZvNb4umavmfCE/pub?embedded=true) - by Low Weilin 
      * [Measuring Scalability and Performance](https://docs.google.com/document/pub?id=1C7fn11fKsgGUx0AT_nH9ZQBi3G7o5zpYqwIIAC40CxU&embedded=true) - by James Dam Tuan Long 
      * [Improving Scalability and Performance](https://docs.google.com/document/pub?id=1v_RYw_Hu1-TExVi0A7d3kxX0CTgFaUtfV1_qYXBhwWs&embedded=true) - by James Dam Tuan Long
      * [Data Backup and Disaster Recovery](https://docs.google.com/document/d/1ECDOy2JUXKLz8t44lXj2t0nvqDtJCjyHM7_HA8DV1fA/pub?embedded=true) - by Lee Shaw Wei Shawn
      * [Dealing with Eventual Consistency](https://docs.google.com/document/d/11HUDa-PlzEEk4-liWlsjC9UbicbfYO1hJMxx_cCEEVE/pub?embedded=true) - by Lee Shaw Wei Shawn
      * [Dealing with Intermittent Null Pointer Exceptions](https://docs.google.com/document/d/1A_QtW8uDFGeeu2KOiWwyuvgm7Jm9pS7nOvmy9B42v_I/pub?embedded=true) - by Lee Shaw Wei Shawn	  
      * [Using Task Queues](https://docs.google.com/document/d/1phgT2hhQ9KkI6jYf6a7N-51CVrLBDmsGapTt_7m3Sp8/pub?embedded=true) - by Hunar Khanna, Lee Shaw Wei Shawn
      * [Using AJAX for dynamic page updates] (https://docs.google.com/document/d/1GjTlIgoZg-vWKGG2b6Bh7ipL9_rV9syymOl93O0iqM4/pub?embedded=true) - by Hunar Khanna
      * [Dealing with character sets](https://docs.google.com/document/d/1ad3olbiqMk4i3geEebzpuqJhXVcLAjOOJQ0fvdifj84/pub?embedded=true) - by Oo Theong Siang
      * [Modifying entity classes with legacy data](https://docs.google.com/document/d/1syHy4BPrM7TkCP7PJYvYZmt7rkRROWqYvBh8w_ZJt1w/pub?embedded=true) - by Oo Theong Siang
      * [Notes on the 'comments' feature] (https://docs.google.com/document/d/1YsgDySCOQbLl7Wc5JYPtKqd_hNvGMwea4OqtVOb0HBM/pub?embedded=true) - by Xie Kai, Ju Junchao
      * [Notes on 'access control'] (https://docs.google.com/document/d/1g-1YhhYMCwO4NGJzn26HVzLxzm5PI6JAFxUNtHEEGDc/pub?embedded=true) - by Gu Junchao
      * [Notes on 'profiles' feature] (https://docs.google.com/document/d/1TGVkdu6JlOFX_2dJoeZu1avkiKj8ZqFcpBREJhKK78c/pub?embedded=true) - by Thyagesh Manikandan

##License
TEAMMATES is released under GPLv2
