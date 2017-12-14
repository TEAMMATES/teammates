var
	merge = require('merge'),
	Messages = require('./../../lib/constants/messages'),
	Validator = require('./../../lib/Validator'),
	validator,
	report,
	expected,
	file
;

exports.tests = {
	'Should pass on CRLF-only files': function (test) {
		file = __dirname + '/fixtures/CRLF_only.txt';
		validator = new Validator({endOfLine: 'CRLF'});
		validator.validate(file);
		report = validator.getInvalidFiles();
		expected = {};
		test.deepEqual(report, expected);
		test.done();
	},

	'Should fail on CR-only files': function (test) {
		file = __dirname + '/fixtures/CR_only.txt';
		validator = new Validator({endOfLine: 'CRLF'});
		validator.validate(file);
		report = validator.getInvalidFiles();
		expected = {};
		expected[file] = {
		        '1': [merge({}, Messages.END_OF_LINE, {line: 1}, {payload: {expected: 'CRLF', end_of_line: 'CR'}})],
		        '2': [merge({}, Messages.END_OF_LINE, {line: 2}, {payload: {expected: 'CRLF', end_of_line: 'CR'}})],
		        '3': [merge({}, Messages.END_OF_LINE, {line: 3}, {payload: {expected: 'CRLF', end_of_line: 'CR'}})],
			'4': [merge({}, Messages.END_OF_LINE, {line: 4}, {payload: {expected: 'CRLF', end_of_line: 'CR'}})],
			'5': [merge({}, Messages.END_OF_LINE, {line: 5}, {payload: {expected: 'CRLF', end_of_line: 'CR'}})],
			'6': [merge({}, Messages.END_OF_LINE, {line: 6}, {payload: {expected: 'CRLF', end_of_line: 'CR'}})],
			'7': [merge({}, Messages.END_OF_LINE, {line: 7}, {payload: {expected: 'CRLF', end_of_line: 'CR'}})]
		};
		test.deepEqual(report, expected);
		test.done();
	},

	'Should fail on LF-only files': function(test) {
		file = __dirname + '/fixtures/LF_only.txt';
		validator = new Validator({endOfLine: 'CRLF'});
		validator.validate(file);
		report = validator.getInvalidFiles();
		expected = {};
		expected[file] = {
		        '1': [merge({}, Messages.END_OF_LINE, {line: 1}, {payload: {expected: 'CRLF', end_of_line: 'LF'}})],
		        '2': [merge({}, Messages.END_OF_LINE, {line: 2}, {payload: {expected: 'CRLF', end_of_line: 'LF'}})],
		        '3': [merge({}, Messages.END_OF_LINE, {line: 3}, {payload: {expected: 'CRLF', end_of_line: 'LF'}})],
			'4': [merge({}, Messages.END_OF_LINE, {line: 4}, {payload: {expected: 'CRLF', end_of_line: 'LF'}})],
			'5': [merge({}, Messages.END_OF_LINE, {line: 5}, {payload: {expected: 'CRLF', end_of_line: 'LF'}})],
			'6': [merge({}, Messages.END_OF_LINE, {line: 6}, {payload: {expected: 'CRLF', end_of_line: 'LF'}})],
			'7': [merge({}, Messages.END_OF_LINE, {line: 7}, {payload: {expected: 'CRLF', end_of_line: 'LF'}})]

		};
		test.deepEqual(report, expected);
		test.done();
	},

	'Should fail on mixed files': function(test) {
		file = __dirname + '/fixtures/mixed_endings.txt';
		validator = new Validator({endOfLine: 'CRLF'});
		validator.validate(file);
		report = validator.getInvalidFiles();
		expected = {};
		expected[file] = {
		        '1': [merge({}, Messages.END_OF_LINE, {line: 1}, {payload: {expected: 'CRLF', end_of_line: 'CR'}})],
		        '2': [merge({}, Messages.END_OF_LINE, {line: 2}, {payload: {expected: 'CRLF', end_of_line: 'LF'}})]
		};
		test.deepEqual(report, expected);
		test.done();
	}
};

