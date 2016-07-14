QUnit.module('instructorStudentList.js');

QUnit.test('filterSection()', function(assert) {

    if (!$('#show_email').is(':checked')) {
        $('#show_email').click();
    }

    // Manually reset view first
    $('tr[id^="student-c"]').show();
    $('#emails').show();
    $('.div[id^="course-"]').show();

    // Deselect 1 section and select back usin Select All
    $('#section_check-1-0').click();
    assert.equal($('#studentsection-c1\\.0').is(':hidden'), true, 'Section not selected is hidden');
    assert.equal($('#section_all').is(':checked'), false, 'Select all check should be removed');

    assert.equal($('#student_email-c0\\.0').is(':visible'), true, '[Team 1]Alice Betsy\'s email visible');
    assert.equal($('#student_email-c0\\.1').is(':visible'), true, '[Team 2]Hugh Ivanov\'s email visible');
    assert.equal($('#student_email-c0\\.2').is(':visible'), true, '[Team 3]Benny Charles\'s email visible');
    assert.equal($('#student_email-c1\\.0').is(':hidden'), true,
                 '[Team 1]Duplicate Benny Charles\'s email should be hidden');
    assert.equal($('#student_email-c1\\.1').is(':hidden'), true, '[Team 1]Carlos Santanna\'s email hidden');
    assert.equal($('#student_email-c1\\.2').is(':hidden'), true, '[Team 1]Charlie D\'s email hidden');
    assert.equal($('#student_email-c1\\.3').is(':hidden'), true, '[Team 2]Denny Charlés\'s email hidden');
    assert.equal($('#student_email-c1\\.4').is(':hidden'), true, '[Team 2]Emma F\'s email hidden');
    assert.equal($('#student_email-c1\\.5').is(':hidden'), true, '[Team 2]Frank Gatsby\'s email hidden');
    assert.equal($('#student_email-c1\\.6').is(':hidden'), true, '[Team 3]Gabriel Hobb\'s email hidden');
    assert.equal($('#student_email-c1\\.7').is(':hidden'), true, '[Team 3]Hans Iker\'s email hidden');
    assert.equal($('#student_email-c1\\.8').is(':hidden'), true, '[Team 3]Ian Jacobsson\'s email hidden');
    assert.equal($('#student_email-c1\\.9').is(':hidden'), true, '[Team 3]James K\'s email hidden');

    $('#section_all').click();
    assert.equal($('#studentsection-c0\\.0').is(':visible'), true, 'All sections should be visible');
    assert.equal($('#studentsection-c0\\.1').is(':visible'), true, 'All sections should be visible');
    assert.equal($('#studentsection-c1\\.0').is(':visible'), true, 'All sections should be visible');
    assert.equal($('#section_check-0-0').is(':checked'), true, 'Course 2 Section A should be re-selected');
    assert.equal($('#section_check-0-1').is(':checked'), true, 'Course 2 Section B should be re-selected');
    assert.equal($('#section_check-1-0').is(':checked'), true, 'Course 3 Section C should be re-selected');

    assert.equal($('[id^=student_email]:visible').length, 12, 'All emails (minus duplicate) should be visible again');

     // Deselect 1 section and select back by selecting that section
    $('#section_check-0-1').click();
    $('#team_check-0-10-0').click(); // deselect the invalid team section
    assert.equal($('#studentsection-c0\\.1').is(':hidden'), true, 'Section not selected is hidden');
    assert.equal($('#section_all').is(':checked'), false, 'Select all check should be removed');

    assert.equal($('#student_email-c0\\.0').is(':visible'), true, '[Team 1]Alice Betsy\'s email visible');
    assert.equal($('#student_email-c0\\.1').is(':hidden'), true, '[Team 2]Hugh Ivanov\'s email hidden');
    assert.equal($('#student_email-c0\\.2').is(':hidden'), true, '[Team 3]Benny Charles\'s email hidden');
    assert.equal($('#student_email-c1\\.0').is(':visible'), true,
                 '[Team 1]Duplicate Benny Charles\'s email should be visible');
    assert.equal($('#student_email-c1\\.1').is(':visible'), true, '[Team 1]Carlos Santanna\'s email visible');
    assert.equal($('#student_email-c1\\.2').is(':visible'), true, '[Team 1]Charlie D\'s email visible');
    assert.equal($('#student_email-c1\\.3').is(':visible'), true, '[Team 2]Denny Charlés\'s email visible');
    assert.equal($('#student_email-c1\\.4').is(':visible'), true, '[Team 2]Emma F\'s email visible');
    assert.equal($('#student_email-c1\\.5').is(':visible'), true, '[Team 2]Frank Gatsby\'s email visible');
    assert.equal($('#student_email-c1\\.6').is(':visible'), true, '[Team 3]Gabriel Hobb\'s email visible');
    assert.equal($('#student_email-c1\\.7').is(':visible'), true, '[Team 3]Hans Iker\'s email visible');
    assert.equal($('#student_email-c1\\.8').is(':visible'), true, '[Team 3]Ian Jacobsson\'s email visible');
    assert.equal($('#student_email-c1\\.9').is(':visible'), true, '[Team 3]James K\'s email visible');

    $('#section_check-0-1').click();
    assert.equal($('#studentsection-c0\\.0').is(':visible'), true, 'All sections should be visible');
    assert.equal($('#studentsection-c0\\.1').is(':visible'), true, 'All sections should be visible');
    assert.equal($('#studentsection-c1\\.0').is(':visible'), true, 'All sections should be visible');
    assert.equal($('#section_check-0-1').is(':checked'), true, 'Course 2 Section B should be re-selected');
    assert.equal($('#team_check-0-1-0').is(':checked'), true, 'Course 2 Section B Team 2 should be selected');
    assert.equal($('#team_check-0-1-1').is(':checked'), true, 'Course 2 Section B Team 3 should be selected');
    assert.equal($('#team_check-0-10-0').is(':checked'), false, 'Course 2 Invalid Section Team should not be selected');

    assert.equal($('[id^=student_email]:visible').length, 12, 'All emails (minus duplicate) should be visible again');
});

QUnit.test('filterTeam()', function(assert) {
    // Initialize by checking the options box and show email
    if (!$('#option_check').is(':checked')) {
        $('#option_check').click();
    }
    if (!$('#show_email').is(':checked')) {
        $('#show_email').click();
    }

    // Manually reset view first
    $('tr[id^="student-c"]').show();
    $('#emails').show();
    $('.div[id^="course-"]').show();

    // Deselect 2 team, and select back using Select All
    $('#team_check-0-0-0').click();
    $('#team_check-1-0-0').click();
    assert.equal($('#studentteam-c0\\.0\\.0').is(':hidden'), true, 'Team not selected is hidden');
    assert.equal($('#studentteam-c1\\.0\\.0').is(':hidden'), true, 'Team not selected is hidden');
    assert.equal($('#team_all').is(':checked'), false, 'Select all check should be removed');

    assert.equal($('#student_email-c0\\.0').is(':hidden'), true, '[Team 1]Alice Betsy\'s email hidden');
    assert.equal($('#student_email-c0\\.1').is(':visible'), true, '[Team 2]Hugh Ivanov\'s email visible');
    assert.equal($('#student_email-c0\\.2').is(':visible'), true, '[Team 3]Benny Charles\'s email visible');
    assert.equal($('#student_email-c1\\.0').is(':hidden'), true,
                 '[Team 1]Duplicate Benny Charles\'s email should be hidden');
    assert.equal($('#student_email-c1\\.1').is(':hidden'), true, '[Team 1]Carlos Santanna\'s email visible');
    assert.equal($('#student_email-c1\\.2').is(':hidden'), true, '[Team 1]Charlie D\'s email visible');
    assert.equal($('#student_email-c1\\.3').is(':visible'), true, '[Team 2]Denny Charlés\'s email visible');
    assert.equal($('#student_email-c1\\.4').is(':visible'), true, '[Team 2]Emma F\'s email hidden');
    assert.equal($('#student_email-c1\\.5').is(':visible'), true, '[Team 2]Frank Gatsby\'s email hidden');
    assert.equal($('#student_email-c1\\.6').is(':visible'), true, '[Team 3]Gabriel Hobb\'s email hidden');
    assert.equal($('#student_email-c1\\.7').is(':visible'), true, '[Team 3]Hans Iker\'s email visible');
    assert.equal($('#student_email-c1\\.8').is(':visible'), true, '[Team 3]Ian Jacobsson\'s email visible');
    assert.equal($('#student_email-c1\\.9').is(':visible'), true, '[Team 3]James K\'s email visible');

    $('#team_all').click();
    assert.equal($('#studentteam-c0\\.0\\.0').is(':visible'), true, 'All teams should be visible');
    assert.equal($('#studentteam-c0\\.1\\.0').is(':visible'), true, 'All teams should be visible');
    assert.equal($('#studentteam-c0\\.1\\.1').is(':visible'), true, 'All teams should be visible');
    assert.equal($('#studentteam-c1\\.0\\.0').is(':visible'), true, 'All teams should be visible');
    assert.equal($('#studentteam-c1\\.0\\.1').is(':visible'), true, 'All teams should be visible');
    assert.equal($('#studentteam-c1\\.0\\.2').is(':visible'), true, 'All teams should be visible');
    assert.equal($('#team_check-0-0-0').is(':checked'), true, 'Course 2 Section A Team 1 checkbox should be re-selected');
    assert.equal($('#team_check-0-1-0').is(':checked'), true, 'Course 2 Section B Team 2 checkbox should be re-selected');
    assert.equal($('#team_check-0-1-1').is(':checked'), true, 'Course 2 Section B Team 3 checkbox should be re-selected');
    assert.equal($('#team_check-1-0-0').is(':checked'), true, 'Course 3 Section C Team 1 checkbox should be re-selected');
    assert.equal($('#team_check-1-0-1').is(':checked'), true, 'Course 3 Section C Team 2 checkbox should be re-selected');
    assert.equal($('#team_check-1-0-2').is(':checked'), true, 'Course 3 Section C Team 3 checkbox should be re-selected');

    assert.equal($('[id^=student_email]:visible').length, 12, 'All emails (minus duplicate) should be visible again');

    // Deselect 1 team, and select back using the specific course check
    $('#team_check-1-0-2').click();
    assert.equal($('#studentteam-c1\\.0\\.2').is(':hidden'), true, 'Team not selected is hidden');
    assert.equal($('#team_all').is(':checked'), false, 'Select all check should be removed');

    assert.equal($('#student_email-c0\\.0').is(':visible'), true, '[Team 1]Alice Betsy\'s email visible');
    assert.equal($('#student_email-c0\\.1').is(':visible'), true, '[Team 2]Hugh Ivanov\'s email visible');
    assert.equal($('#student_email-c0\\.2').is(':visible'), true, '[Team 3]Benny Charles\'s email visible');
    assert.equal($('#student_email-c1\\.0').is(':hidden'), true,
                 '[Team 1]Duplicate Benny Charles\'s email should be hidden');
    assert.equal($('#student_email-c1\\.1').is(':visible'), true, '[Team 1]Carlos Santanna\'s email visible');
    assert.equal($('#student_email-c1\\.2').is(':visible'), true, '[Team 1]Charlie D\'s email visible');
    assert.equal($('#student_email-c1\\.3').is(':visible'), true, '[Team 2]Denny Charlés\'s email visible');
    assert.equal($('#student_email-c1\\.4').is(':visible'), true, '[Team 2]Emma F\'s email visible');
    assert.equal($('#student_email-c1\\.5').is(':visible'), true, '[Team 2]Frank Gatsby\'s email visible');
    assert.equal($('#student_email-c1\\.6').is(':hidden'), true, '[Team 3]Gabriel Hobb\'s email hidden');
    assert.equal($('#student_email-c1\\.7').is(':hidden'), true, '[Team 3]Hans Iker\'s email hidden');
    assert.equal($('#student_email-c1\\.8').is(':hidden'), true, '[Team 3]Ian Jacobsson\'s email hidden');
    assert.equal($('#student_email-c1\\.9').is(':hidden'), true, '[Team 3]James K\'s email hidden');

    $('#team_check-1-0-2').click();
    assert.equal($('#studentteam-c1\\.0\\.2').is(':visible'), true, 'Team selected is visible');
    assert.equal($('#team_check-1-0-2').is(':checked'), true, 'Course 1 Team 1 checkbox should be re-selected');

    assert.equal($('[id^=student_email]:visible').length, 12, 'All emails (minus 1 duplicate) should be visible again');
});

QUnit.test('filterEmails()', function(assert) {
    // The method has been tested by UI test, and testFilter(Course|Team|Name) above.
    assert.expect(0);
});

