<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<%@ taglib tagdir="/WEB-INF/tags/static" prefix="ts" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="teammates.common.util.Const" %>
<c:set var="contactPage" value="<%= Const.ViewURIs.CONTACT %>" />
<c:set var="instructorHelpPage" value="<%= Const.ViewURIs.INSTRUCTOR_HELP %>" />
<t:staticPage currentPage="features">
    <div id="contentHolder">
        <br>
        <p>
            Here is an overview of some notable TEAMMATES features.
            The <a href="${instructorHelpPage}">help page</a> contains more details about how to use these features.
        </p>
        <br>
        <c:set var="teamPeerEval">
            Have a team project in your course? Set up a 'team peer evaluation session' for students to give anonymous peer feedback to team members.
            <br><br>
            Students can provide confidential peer evaluations to you too.
        </c:set>
        <ts:feature caption="Team peer evaluations" imgSuffix="teampeerevaluations" desc="${teamPeerEval}" />

        <c:set var="flexiblePath">
            There are many other feedback paths available. Some examples:<br>
            a) feedback between teams<br>
            b) from instructors to students<br>
            c) from each student to three other students
        </c:set>
        <ts:feature caption="Flexible feedback paths" imgSuffix="flexiblefeedbackpaths" desc="${flexiblePath}" />

        <c:set var="visibilityControl">
            If you plan to publish the responses collected, you can set the visibility level for each question i.e. who can see the<br>
            (i) response text,<br>
            (ii) the identity of the feedback giver, and<br>
            (iii) the identity of the feedback receiver.
        </c:set>
        <ts:feature caption="Powerful visibility control" imgSuffix="powerfulvisibilitycontrol" desc="${visibilityControl}" />

        <c:set var="reportsAndStats">
            There are many report formats for viewing responses collected, e.g. Group by team, then by feedback giver.
            <br><br>
            Some statistics for the responses are available too.
        </c:set>
        <ts:feature caption="Reports and statistics" imgSuffix="reportsandstatistics" desc="${reportsAndStats}" />

        <c:set var="fineGrainAccessControl">
            You can add more instructors (e.g. co-lecturers, visitors, tutors, etc.) to your courses and give them different access levels.
            <br><br>
            If required, you can even set which sessions of which section of students are accessible to a particular instructor.
        </c:set>
        <ts:feature caption="Fine-grain access control" imgSuffix="finegrainaccesscontrol" desc="${fineGrainAccessControl}" />

        <c:set var="questionTypes">
            Essay questions, MCQ questions, Multiple select questions, Numeric scale questions, ‘Distribute a fixed number of points among options’ questions, questions measuring contribution in team projects, and more choices coming soon...
            <br><br>
            You can even generate MCQ options from student names, as illustrated in the above example.
        </c:set>
        <ts:feature caption="Different question types" imgSuffix="differentquestiontypes" desc="${questionTypes}" />

        <c:set var="reusePastQns">
            Once you have a perfect set of questions configured for a session, you can reuse those questions later, without needing to configure the same questions from scratch again.
            <br><br>
            TEAMMATES also provides some session templates to choose from.
        </c:set>
        <ts:feature caption="Reuse past questions" imgSuffix="reusepastquestions" desc="${reusePastQns}" />

        <c:set var="noSignupRequired">
            Students can submit responses and view published responses using the unique links TEAMMATES email them, without having to login or signup.
            <br><br>
            If they login to TEAMMATES using their Google accounts (optional), they can access all TEAMMATES courses in one page and access even more features such as setting up profiles.
        </c:set>
        <ts:feature caption="No signup required for students" imgSuffix="nosignuprequired" desc="${noSignupRequired}" />

        <c:set var="downloadableData">
            You can download the collected responses (and statistics) as spreadsheets.
            <br><br>
            The collected data belong to you and you may delete your data from TEAMMATES any time.
        </c:set>
        <ts:feature caption="Downloadable data" imgSuffix="downloadabledata" desc="${downloadableData}" />

        <c:set var="shareableComments">
            You can add comments about students, teams, or responses collected. You can even share these comments with students/instructors.
            <br><br>
            Fine-grained visibility control is available for comments too.
            For example, you can give a comment to a student that the receiving student can see while other students in the course can only see the comment text but cannot see the recipient name.
        </c:set>
        <ts:feature caption="Shareable comments" imgSuffix="shareablecomments" desc="${shareableComments}" />

        <c:set var="studentProfiles">
            You can get students to upload more information about themselves, including a profile picture.
            Such extra data can help you remember current/past students better.
            <span class="tiny-text"> [<a href="https://www.flickr.com/photos/meatheadmovers">photo source</a>]</span>
            <br><br>
            You can access all data about a student in one page, just by typing a part of the student's name in our search box. In a single page you can see,<br>
            * Student's profile<br>
            * All feedback given/received by the student<br>
            * All comments received by the student
        </c:set>
        <ts:feature caption="Student profiles" imgSuffix="studentprofiles" desc="${studentProfiles}" />

        <c:set var="powerfulSearch">
            TEAMMATES has a powerful search feature to locate details about students or past comments.
            <br><br>
            Given above is an example of searching for a student whose name you are not very sure of.
        </c:set>
        <ts:feature caption="Powerful search" imgSuffix="powerfulsearch" desc="${powerfulSearch}" />

        <c:set var="bigCourses">
            TEAMMATES can support a course of any size (even beyond 1,000 students), as long as you divide the students into sections of no more than 100 students.
            There is no limit on the number of courses or sessions you could have either.
        </c:set>
        <ts:feature caption="Support for big courses" imgSuffix="supportforbigcourses" desc="${bigCourses}" />
        
        <br><br>
        For more details about these features, visit the <a href="${instructorHelpPage}" target="_blank">Instructor Help</a> page or <a href="${contactPage}">email us</a>.
        <br><br><br><br>
    </div>
</t:staticPage>