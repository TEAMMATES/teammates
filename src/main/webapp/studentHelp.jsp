<%@ page pageEncoding="UTF-8" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<t:helpPage>
  <h1>Help for Students</h1><br>
  <h4 class="text-color-primary">Getting Started</h4>
  <br>
  <ol type="1">
    <li>
      <span class="text-bold">Join the course</span> by clicking the link given in the email you received from TEAMMATES.<br>
      Note that you need to join a course only once per course.<br>
      Also note that joining a course is optional. If you do not join, you can still submit feedback and view feedback responses using links sent to you by TEAMMATES.
      If you join, you get access to extra features such as the ability to see all your feedback sessions in a single home page.<br>
    </li>
    <br>
    <li>
      <span class="text-bold">Do the pending submissions</span> (if any).
    </li>
    <br>
    <li>
      You can <span class="text-bold">edit your submissions</span> up to the closing time of the respective session.
    </li>
    <br>
    <li>
      You can <span class="text-bold">view session results</span> after the instructor has published session results.
      The system will notify you via email when results are available for viewing.
    </li>
  </ol>
  <hr>
  <h4 class="text-color-primary">Frequently Asked Questions</h4>
  <br>
  <ul>
    <li>
      <div>
        <b>What should I do if I cannot see any/some of my courses I should be able to see in the system?</b>
      </div>
      <div>
        These are the possible reasons:
        <ol>
          <li>
            You used a different Google account to access TEAMMATES in the past.
            In that case, you need to use the same Google account to access TEAMMATES again.
            Logout and re-login using the other Google account.
          </li>
          <li>
            You changed the primary email from a non-Gmail address to a Gmail address recently.
            In that case, email <a href="mailto:teammates@comp.nus.edu.sg">teammates@comp.nus.edu.sg</a> so that we can reconfigure your account to use the new Gmail address.
          </li>
        </ol>
      </div>
    </li>
    <br>
    <li>
      <div>
        <b>What should I do if I'm unable to submit my responses?</b>
      </div>
      <div>
        Email <a href="mailto:teammates@comp.nus.edu.sg">us</a> so that we can help you submit.
        Note that we cannot do much if the submission deadline is over. It is not up to us (the TEAMMATES team) to accept overdue submissions.
      </div>
    </li>
    <br>
    <li>
      <div>
        <b>How are contribution question results used in grading?</b>
      </div>
      <div>
          TEAMMATES does not calculate grades. It is up to the instructors to use contribution question results in any way they want.
          However, TEAMMATES recommend that contribution question results are used only as flags to identify teams with contribution imbalances.
          Once identified, the instructor is recommended to investigate further before taking action.
      </div>
    </li>
    <br>
    <li>
      <div>
        <b>How are the scores for contribution questions calculated?</b>
      </div>
      <div>
          Here are the important things to note:
          <ul class="bulletedList">
            <li>
              The contribution you attribute to yourself is not used when calculating the perceived
              contribution of you or team members.
            </li>
            <li>
              From the estimates you submit, we try to deduce the answer to this question:
              In your opinion, if your teammates are doing the project by themselves without you,
              how do they compare against each other in terms of contribution? This is because we
              want to deduce your unbiased opinion about your team members’ contributions. Then,
              we use those values to calculate the average perceived contribution of each team member.
            </li>
            <li>
              When deducing the above, we first adjust the estimates you submitted to remove artificial
              inflations/deflations. For example, giving everyone [Equal share + 20%] is as same as giving
              everyone [Equal share] because in both cases all members have done a similar share of work.
            </li>
            <li>
              The team’s view has been scaled up/down so that the sum of values in your
              view matches the sum of values in team’s view. That way, you can make a direct comparison
              between your view and the team’s view. As a result, the values you see in team’s view may
              not match the values your team members see in their results.
            </li>
          </ul>
          The actual calculation is a bit complex and the finer details can be found
          <a href="/technicalInformation.jsp#calculatePointsContribution">here</a>.
      </div>
    </li>
  </ul>
</t:helpPage>
