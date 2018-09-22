<%@ page pageEncoding="UTF-8" %>
<h4 class="text-color-primary" id="calculateRanks">Rank Calculation For Rank Questions</h4>
<div id="contentHolder">
  <ul>
    <li>
      <b>Ranks Received </b> is a list of the actual ranks each recipient received. TEAMMATES processes the original responses, handling ties and unused ranks. For example, if giver A's original response is <code>{1, 3, 3, 5}</code> and Rank 5 is given to recipient B, after the processing, giver A's response will become <code>{1, 2, 2, 4}</code> and recipient B will have a Rank 4 in his <b>Ranks Received</b>, instead of the Rank 5 in the original response by giver A.
    </li>
    <li>
      The <b> Overall Rank </b> ranks the average rank each recipient receives. For example, if recipient A received the ranks <code>{1,2}</code> and recipient B received the ranks <code>{2,4,6}</code>, then recipient A and recipient B's average ranks are 1.5 and 4 respectively. By ranking these two averages, recipient A and B will get an <b>Overall Rank</b> of 1 and 2 respectively.
    </li>
  </ul>
</div>
