var
	fs = require('fs'),
	path = require('path'),
	Validator = require('./../../lib/Validator'),
	validator,
	options
;

exports.tests = {
	'should override the settings by rcconfig': function(test) {
		options = {
			trailingspaces: false,
			newlineMaximum: false,
			indentation: 'spaces',
			spaces: 2,
			newline: false,
			ignores: ['js-comments'],
			rcconfig: __dirname + '/.testconfigrc'
		};

		// fake loading:
		validator = new Validator(options);
		validator._path = __filename;
		validator._loadSettings();

		// // newline:
		test.ok(validator._settings.newline !== options.newline);
		test.equal(validator._settings.newline, true);

		// // indentation will be overwritten:
		test.ok(validator._settings.indentation !== options.indentation);
		test.equal(validator._settings.indentation, 'tabs');

		// // spaces will be overwritten:
		test.ok(validator._settings.spaces !== options.spaces);
		test.equal(validator._settings.spaces, 'tab');

		// // trailingspaces will be overwritten:
		test.ok(validator._settings.trailingspaces !== options.trailingspaces);
		test.equal(validator._settings.trailingspaces, true);

		// // newlineMaximum will be unchanged:
		test.equal(validator._settings.newlineMaximum, options.newlineMaximum);

		test.done();
	},

	'should throw an exception if rcconfig has no valid path': function(test) {
		test.throws(function() {
			validator = new Validator({rcconfig: '.'});
			validator.validate(__filename);
		}, Error);

		test.throws(function() {
			validator = new Validator({rcconfig: __dirname});
			validator.validate(__filename);
		}, Error);

		test.throws(function() {
			validator = new Validator({rcconfig: __dirname + '/path/that/doesnt/existis/.testconfigrc'});
			validator.validate(__filename);
		}, Error);

		test.done();
	},

	'should load load default file by appname "lintspaces" when setting is set to "true"': function(test) {
		var
			configFile = path.join(__dirname.toString(), '..', '..', '.' + Validator.APPNAME + 'rc'),
			expected = {
				indentation: 'spaces',
				trailingspaces: true
			},
			validator
		;

		// create config file:
		fs.writeFileSync(configFile, JSON.stringify(expected));

		// fake loading:
		validator = new Validator({rcconfig: true});
		validator._path = __filename;
		validator._loadSettings();

		// remove config file:
		fs.unlinkSync(configFile);

		// test for expected properties:
		test.equal(validator._settings.indentation, expected.indentation);
		test.equal(validator._settings.trailingspaces, expected.trailingspaces);

		test.done();
	}
};
