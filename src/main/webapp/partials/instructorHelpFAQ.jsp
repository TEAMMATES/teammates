<%@ page pageEncoding="UTF-8" %>
<h4 class="text-color-primary" id="faq">
  Frequently Asked Questions
</h4>
<div id="contentHolder">
  <br>
  <div>
    <ul>
      <li id="faq-view-student-list-in-course">
        <b>How do I view a list of students in a course?</b>
        <div class="helpSectionContent">
          Go to the ‘Courses’ page, click ‘View’ for the relevant course.
          <br>
          <br>
        </div>
      </li>
      <li id="faq-change-details-after-enrolling">
        <b>How do I change student details after enrolling?</b>
        <div class="helpSectionContent">
          Choose to ‘Edit’ a student from the student list (see ‘How do I view a list of students in a course?’ given above).
          <br>
          <br>Note: Moving a student to a different team (i.e. changing a students team id) will affect all existing sessions in that course.
          <br>
          <br>
        </div>
      </li>
      <li id="faq-add-students-to-course">
        <b>How do I add more students to a course?</b>
        <div class="helpSectionContent">
          You can use the ‘Enrol’ feature to add more students. If a student you are adding has the same email address as an existing student in the class, they will be considered the same student and the name, team name and the comment will be updated to the new values.
          <br>
          <br>
        </div>
      </li>
      <li id="faq-delete-students-from-course">
        <b>How do I delete students from a course?</b>
        <div class="helpSectionContent">
          The ‘Delete’ link is available from the student list (see ‘How do I view a list of students in a course?’ given above).
          <br>
          <br>
        </div>
      </li>
      <li id="faq-change-googleID-of-student">
        <b>How do I change the Google ID associated with a student?</b>
        <div class="helpSectionContent">
          Please contact us for assistance.
          <br>
          <br>
        </div>
      </li>
      <li id="faq-if-courses-displayed-to-students-disappear">
        <b>What to do if a student says his/her courses have disappeared from the system?</b>
        <div class="helpSectionContent">
          The most likely reason for this is that the student has changed the primary email address associated with his/her Google ID. Please ask the student to email
          <a href="mailto:teammates@comp.nus.edu.sg">teammates@comp.nus.edu.sg</a> so that we help him to rectify the problem.
          <br>
          <br>
        </div>
      </li>
      <li id="faq-interpret-contribution-values-in-results">
        <b>How do I interpret/use contribution values in the results of ‘Team contribution’ type questions?</b>
        <div class="helpSectionContent">
          <ul>
            <li>
              <span class="text-bold gray">E (Equal share)</span> is a relative measure. e.g. For a 3-person team, an ‘Equal share’ means ‘a third of the total work done’.
            </li>
            <li>
              <span class="text-bold gray">CC (Claimed Contribution)</span> : This is what the student claimed he contributed.
            </li>
            <li>
              <span class="text-bold gray">Ratings Received </span>: These are the peer opinions as to how much the student contributed. These values have been adjusted to neutralize any attempts by students to boost their own standing by rating others low.
            </li>
            <li>
              <span class="text-bold gray">PC (Perceived Contribution)</span>: This is the average value of the ‘Ratings Received’. This can be considered as the
              <span class="italic">team’s perception of how much the student contributed</span>.
            </li>
            <li>
              <span class="text-bold gray">Diff</span> : The difference between the claimed contribution (CC) and the perceived contribution (PC). This value can be used to identify those who have over/under-estimated their own contribution.
            </li>
          </ul>
        </div>
        <div class="helpSectionContent">
          <br> The above values can be used to identify relative contribution levels of students in a team. If you use these values for grading, also refer the ‘Interpret contribution numbers with care’ caveat in the
          <a href="#tips">Tips for conducting 'team peer evaluation' sessions</a> section below.
          <br>
          <br>
        </div>
      </li>

      <li id="faq-if-students-cannot-submit-an-evaluation">
        <b>What to do if a student says he cannot submit an evaluation due to some technical glitch?</b>
        <div class="helpSectionContent">
          Please ask the student to contact us with the details.
          <br> The ability for instructors to submit on behalf of a student is coming soon.
          <br>
          <br>
        </div>
      </li>
      <li id="faq-entered-contribution-differs-from-results">
        <b>How come the actual contribution values entered by the student is different from the values shown in the results?</b>
        <div class="helpSectionContent">
          This is because the system ‘normalizes’ those values so that there is no artificial inflation of contribution. For example, if a student says everyone contributed ‘Equal share + 10%’, the system automatically normalize it to ‘Equal share’ because in reality that is what the student means.
          <br>‘Normalize’ here means scale up/down the values so that the (sum of contributions) = (
          <span class="italic">n</span> x Equal Share) where
          <span class="italic">n</span> is the number of students being reviewed.
          <br>
          <br>
        </div>
      </li>
      <li id="faq-can-student-influence-his-contribution">
        <b>Can a student influence his ‘perceived contribution’ value by awarding himself a larger contribution?</b>
        <div class="helpSectionContent">
          No. The perceived contribution is calculated based on what his team members perceive as his contribution. His own opinion about his own contribution is not considered for the calculation.
          <br>
          <br>Students enter contribution estimates for self and team members, using the scale Equal share + x%. e.g. Equal share -10%
          <br>
          <br>Based on those values, we try to deduce the student's answer to the following two questions:
          <div class="helpSectionContent">
            (a) In your opinion, what portion of the project did you do?
            <br> (b) In your opinion, if your teammates are doing the project by themselves without you, how do they compare against each other in terms of contribution?
            <br>
            <br>
          </div>
          In the calculation, we do not allow (a) to affect (b). We use (b) to calculate the average perceived contribution for each student. A more detailed version of this calculation can be found
          <a href="https://docs.google.com/document/d/1hjQQHYM3YId0EUSrGnJWG5AeFpDD_G7xg_d--7jg3vU/pub?embedded=true#h.n5u2xs6z9y0g">here</a>.
        </div>
      </li>
      <br>
      <li id="faq-how-contribution-numbers-calculated">
        <b>How are the contribution numbers calculated?</b>
        <div class="helpSectionContent">
          You can find the details of the contribution calculation scheme
          <a href="https://docs.google.com/document/d/1hjQQHYM3YId0EUSrGnJWG5AeFpDD_G7xg_d--7jg3vU/pub?embedded=true#h.n5u2xs6z9y0g">here</a>.
        </div>
      </li>
      <br>
      <li id="faq-is-google-account-compulsory-for-student">
        <b>Is it compulsory for students to use Google accounts?</b>
        <div class="helpSectionContent">
          Student can submit feedback and view results without Google accounts. TEAMMATES send unique URL for each of those. But it is more convenient for them if they use Google accounts.
          <br>
          <br>
        </div>
      </li>
      <li id="faq-if-course-has-no-teams">
        <b>What if my course doesn’t have teams?</b>
        <div class="helpSectionContent">
          They can use a dummy value for the ‘Team’ column.
          <br>
          <br>
        </div>
      </li>
      <li id="faq-size-limit-for-course">
        <b>Is there a size limit for a course?</b>
        <div class="helpSectionContent">
          No. But courses bigger than 100 students need to divide the course into sections so that TEAMMATES know how to paginate reports when the entire report is too big to show in one go.
          <br>
          <br>
        </div>
      </li>
    </ul>
    <p align="right">
      <a href="#Top">Back to Top
      </a>
    </p>
  </div>
  <div class="separate-content-holder">
    <hr>
  </div>
</div>
