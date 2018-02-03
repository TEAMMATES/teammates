<%@ page pageEncoding="UTF-8" %>
<h4 class="text-color-primary" id="calculatePointsContribution">Point Calculation For Contribution Questions</h4>
<div id="contentHolder">
  <br>
  <ul>
    <li>
      <span class="text-bold">General Mechanism</span>
      <div class="helpSectionContent">
        Students enter contribution estimates for themselves and their team members using the contribution scale.
        <br>
        <br> Based on those values, a student's answer to the following two questions are deduced:
        <br> (a) In your opinion, what portion of the project did you do?
        <br> (b) In your opinion, if your teammates are doing the project by themselves without you, how do they compare against each other in terms of contribution?
        <br>
        <br> In the calculation, we do not allow (a) to affect (b). We use (b) to calculate the average perceived contribution for each student.
        <br>
      </div>
      <br>
    </li>
    <li>
      <span class="text-bold">Calculation Scheme</span>
      <div class="helpSectionContent">
        The calculation scheme seeks to answer the two questions above. While somewhat complicated, the process can actually be broken down into a few steps.
        <br>
        <br>
        <ol type="1">
          <li>
            <span class="text-bold">Calculate normalized values</span>
            <div class="helpSectionContent">
              The submitted values are first normalized. This is required because the total points entered might not sum up to 100 * team size.
              <br>
              <br> To normalize a value, the value is multiplied with a normalization factor. For example, consider the following scenario for
              a student working in a team of four:
              <br>
              <br> Submitted values: 90 (self), 110, 130, N/A
              <br> Total: 90 + 110 + 130 = 330
              <br> Normalization factor: (100 * 3) / (90 + 110 + 130) = 300 / 330
              <br> Normalized values: 82 (self), 100, 118, N/A
              <br> Normalized total: 82 + 100 + 118 = 300
              <br>
              <br> Since the student gave himself a normalized value of 82, it can be deduced that the student thinks he did 'Equal Share -18%'.
              Thus, this answers question (a) above.
              <br>
              <br>
            </div>
          </li>
          <li>
            <span class="text-bold">Calculate peer contribution ratio values</span>
            <div class="helpSectionContent">
              Then, the normalized values are used to calculate peer contribution ratio values. These values remove self-rating bias
              as the self rating is ignored and the remaining values are normalized.
              <br>
              <br> Following the same example from above:
              <br>
              <br> Normalized values: 82 (self), 100, 118, N/A
              <br> To calculate unbiased normalized values:
              <br> &nbsp; 82 -> ignored
              <br> &nbsp; 100 -> 100 * (200 / (100 + 118)) = 92
              <br> &nbsp; 118 -> 118 * (200 / (100 + 118)) = 108
              <br> Unbiased normalized values: [self (ignored)], 92, 108, N/A
              <br> Unbiased normalized total: 200
              <br>
              <br> From this, it can be deduced that the student thinks his peer contribution ratio is 92:108 and that he is unsure
              of the last teammate in his team. Thus, this answers question (b) above.
              <br>
              <br>
            </div>
          </li>
          <li>
            <span class="text-bold">Calculate normalized average perceived contribution ratio</span>
            <div class="helpSectionContent">
              Now, the normalized average perceived contribution ratio can be calculated for a student based on the peer contribution ratio
              value he received from his teammates. Note that this method of calculation naturally eliminates self-rating bias since
              the peer contribution ratio values do not include self-ratings.
              <br>
              <br> The following steps are taken to calculate the normalized average perceived contribution ratio for a student:
              <ol type="i">
                <li>
                  <span>Calculate average perceived contribution ratio</span>
                  <div class="helpSectionContent">
                    For each student, take the average of the peer contribution ratios that others have given him.
                    <br>
                    <br>
                  </div>
                </li>
                <li>
                  <span>Calculate normalized average perceived contribution ratio</span>
                  <div class="helpSectionContent">
                    For each student, normalize the average obtained in (i) above. The normalization process is similar to how
                    the submitted values are normalized in (1), which means that the normalized average value is simply the average
                    value multiplied with a normalization factor.
                    <br>
                    <br> The normalized average perceived contribution ratio represents the relative work distribution among team
                    members as based on the unbiased opinions of team members.
                    <br>
                    <br>
                  </div>
                </li>
              </ol>
            </div>
          </li>
          <li>
            <span class="text-bold">Calculate denormalized peer contribution ratio</span>
            <div class="helpSectionContent">
              Finally, the peer contribution ratio is denormalized so that the total number of points initially given by each student
              matches the total sum of the peer contribution ratios displayed. In this way, the student can compare his input (i.e. his
              opinion of the team's work distribution) with the team's opinion since the totals before and after the point calculation
              scheme now tally.
              <br>
              <br> The denormalization process is as follow:
              <ol type="i">
                <li>
                  <span>Find filtered perceived values</span>
                  <div class="helpSectionContent">
                    For each student, their filtered perceived values for each team member (including themselves) are equivalent
                    to the normalized average peer contribution ratio if the student has given the team member a valid rating.
                    Otherwise, the rating is simply "NA".
                    <br>
                    <br>
                  </div>
                </li>
                <li>
                  <span>Find filtered actual values</span>
                  <div class="helpSectionContent">
                    Again, for each student, their filtered actual values for each team member (including themselves) are equivalent
                    to the actual values which they submitted for each of their team member if the student has given the team member a valid rating.
                    Otherwise, the rating is simply "NA".
                    <br>
                    <br>
                  </div>
                </li>
                <li>
                  <span>Find the denormalized peer contribution ratio</span>
                  <div class="helpSectionContent">
                    The 'normalization' factor in this case is given by: (sum of the filtered actual values) / (sum of filtered perceived values).
                    <br>
                    <br> To find the denormalized peer contribution ratio, multiply the normalized average peer contribution found by the corresponding
                    'normalization' factor.
                    <br>
                    <br>
                  </div>
                </li>
              </ol>
            </div>
          </li>
        </ol>
        <div class="helpSectionContent">
          After the results of the feedback session are published, the instructors and students will have different views of the results.
          <br>
          <br> For students:
          <br> &nbsp; Claimed contribution: normalized submitted values [see 1.]
          <br> &nbsp; Perceived contribution: denormalized peer contribution ratio [see 4.iii]
          <br>
          <br> For instructors:
          <br> &nbsp; Claimed contribution: normalized submitted values [see 1.]
          <br> &nbsp; Perceived contribution: normalized average perceived contribution ratio [see 3.ii]
          <br>
          <br>
        </div>
      </div>
    </li>
    <li>
      <span class="text-bold">Full Example</span>
      <div class="helpSectionContent">
        Here is a full example to illustrate the points calculation scheme for contribution questions.
        <br>
        <br>
        <div class="container-fluid text-center">
          <img src="/images/technical_contributionexample.png" alt="contribution_example" class="img-responsive">
        </div>
        <br> In this example, the instructors and students should see the following results:
        <br>
        <br> For students:
        <br> &nbsp; Claimed contribution: yellow values
        <br> &nbsp; Perceived contribution: orange values
        <br>
        <br> For instructors:
        <br> &nbsp; Claimed contribution: yellow values
        <br> &nbsp; Perceived contribution: blue values
        <br>
        <br> Indeed, here is the corresponding instructor view:
        <br>
        <br>
        <div class="container-fluid text-center">
          <img src="/images/technical_contributioninstructorview.png" alt="contribution_example_instructor_view" class="img-responsive">
        </div>
      </div>
    </li>
  </ul>
</div>
