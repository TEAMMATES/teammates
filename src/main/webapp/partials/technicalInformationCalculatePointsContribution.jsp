<%@ page pageEncoding="UTF-8" %>
<h4 class="text-color-primary" id="calculatePointsContribution">Point Calculation For Contribution Questions</h4>
<div id="contentHolder">
  <br>
  <ul>
    <li>
      <span class="text-bold">General Mechanism</span>
      <div class="helpSectionContent">
        Students enter contribution estimates for themselves and their team members using the contribution scale
        (see <a href="https://github.com/TEAMMATES/teammates/blob/master/docs/glossary.md#product-related"> glossary</a>).
        <br>
        <br>Based on those values, a student's answer to the following two questions are deduced:
        <br>(a) In your opinion, what portion of the project did you do?
        <br>(b) In your opinion, if your teammates are doing the project by themselves without you, how do they compare against each other in terms of contribution?
        <br>
        <br>In the calculation, we do not allow (a) to affect (b). We use (b) to calculate the average perceived contribution for each student.
        <br>
      </div>
      <br>
    </li>
    <li>
      <span class="text-bold">Calculation Scheme</span>
      <div class="helpSectionContent">
        <ol type="1">
          <li>
            <span class="text-bold">Calculate <code>normalizedClaimed</code> values</span>
            <div class="helpSectionContent">
              This is required because the total of points entered might not sum up to <code>100 * (team size)</code>.
              <br>
              <br>
              <code>(normalized value) = (original value) * (normalization factor)</code>
              <br>
              <br>
              <div class="bs-example">
                entered values: 90 [self], 110,130, N/A (total = 330)
                <br>normalization factor: (100 * 3) / (90 + 110 + 130) = 300 / 330
                <br>normalized: 82, 100, 118, N/A
                <br>normalized total = 300 (i.e. 100 * number of inputs)
              </div>
              This answers the question (a) above. The student thinks he did 'Equal share - 18%' (as indicated by 82).
              <br>
              <br>
            </div>
          </li>
          <li>
            <span class="text-bold">Calculate <code>peerContributionRatio</code> values by removing self-rating bias</span>
            <div class="helpSectionContent">
              Here, we ignore the self rating and normalize remaining values.
              <br>
              <br>
              <div class="bs-example">
                normalized input (from above): 82,100, 118, N/A
                <br>Calculating unbiased values:
                <br>&nbsp;82 → ignored.
                <br>&nbsp;100 → 100 * 200 / (100 + 118) = 92
                <br>&nbsp;118 → 118 * 200 / (100 + 118) = 108
                <br>Unbiased values: [self (ignored)], 92, 108, N/A
                <br>Unbiased values total = 200 (100 * number of ratings)
              </div>
              This answers the question (b) above. In the example above, the student thinks his teammates contribution ratio is 92:108 and is unsure of the third teammate.
              <br>
              <br>
            </div>
          </li>
          <li>
            <span class="text-bold">Calculate <code>averagePerceivedContributionRatio</code></span>
            <div class="helpSectionContent">
              Next, we calculate <code>averagePerceivedContributionRatio</code> among team members, independent of (a). This consists of these steps:
              <br>
              <br>
              <ol type="i">
                <li>
                  <span class="text-bold">Calculate <code>averagePerceived</code>:</span>
                  <div class="helpSectionContent">
                    For each student, take the average of <code>peerContributionRatio</code>that others have given him.
                    <br>
                    <br>
                  </div>
                </li>
                <li>
                  <span class="text-bold">Calculate <code>normalizedAveragePerceived</code>:</span>
                  <div class="helpSectionContent">
                    Normalize the averages, similar to how input was normalized.
                    <br>
                    <br>
                    <code>
                      normalizedAveragePerceived = averagePerceived * normalizationFactor
                      <br>normalizationFactor = 100 * (number of students with averagePerceived values)/(sum of averagePerceived)
                    </code>
                    <br>
                    <br>This is the relative work distribution among team members based on unbiased opinions of team members.
                    <br>
                    <br>
                  </div>
                </li>
                <li>
                  <span class="text-bold">Calculate <code>normalizedPeerContributionRatio</code></span>
                  <div class="helpSectionContent">
                    Since we normalized the averages (in previous step), we also normalize the value that were averaged in the first place. This is such that average and averaged tallies with each other.
                    <br>
                    <br>
                    <code>normalizedPeerContributionRatio = peerContributionRatio * normalizationFactor</code>
                    <br>
                    <br>
                  </div>
                </li>
              </ol>
            </div>
          </li>
          <li>
            <span class="text-bold">Denormalize <code>normalizedAveragePerceived</code></span>
            <div class="helpSectionContent">
              For each student, denormalize <code>normalizedAveragePerceived</code>. We scale back to match the total of original
              input by student. That way, student can compare his input (i.e., his opinion of the team’s work distribution)
              with the team’s opinion. In the example used above, we should use 330/300 as the denormalizing factor for that student.
              The result could be something like this:
              <br>
              <br>
              <div class="bs-example">
                student’s opinion: 90 [self], 110, 130, N/A (total = 330)
                <br>team’s opinion : 95, 105, 125, 115 (total = 440)
              </div>
              Value transformation steps: input (i.e. claimed) → <code>normalizedClaimed</code> →
              <code>peerContributionRatio</code> → <code>averagePerceived</code> →
              <code>normalizedAveragePerceived</code> → <code>denormalizedAveragePerceived</code> →
              <code>normalizedPeerContributionRatio</code>
              <br>
              <br>
            </div>
          </li>
        </ol>
      </div>
      Student view:
      <ul>
        <li>
          for claimed contribution, show: same as what the student entered initially (otherwise, the student will be confused as to how the value got changed)
        </li>
        <li>
          for perceived contribution, show: <code>denormalizedAveragePerceived</code>
        </li>
      </ul>
      <br>Instructor view:
      <ul>
        <li>
          for claimed contribution, show: <code>normalizedClaimed</code>
        </li>
        <li>
          for perceived contribution, show: <code>normalizedAveragePerceived</code>
        </li>
      </ul>
      <br>Note:
      <ul>
        <li>
          Scenario 1: If students give 0 points to each other, then everyone should receive Equal Share and difference should be 0.
        </li>
        <li>
          Scenario 2: If students are not sure or do not submit the evaluation, then Perceived/Claimed for Instructor should be shown as N/A instead of Equal Share. In this case, difference too should be shown as N/A.
        </li>
      </ul>
    </li>
  </ul>
</div>
