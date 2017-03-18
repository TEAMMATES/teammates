import names

FILENAME = 'InstructorCourseEnrollPageScaleTestData'

HEADER = "Section | Team | Name | Email | Comments\n"


def generate_students(num):
    student_names = []
    students = HEADER
    for i in range(num):
        cur_name = names.get_full_name()
        while cur_name in student_names:
            cur_name = names.get_full_name()
        cur_username = cur_name.lower().replace(" ", ".")
        students += ('Section ' + str(i // 100 + 1) + ' | Team '
                     + str(i // 100 + 1) + ' | ' + cur_name + ' | '
                     + cur_username + '.tmms@gmail.tmt' + ' | \n')
    return students


student_groups = [10, 20, 50, 75, 100, 150]

for i in student_groups:
    with open(FILENAME + str(i), 'w') as file:
        file.write(generate_students(i))
