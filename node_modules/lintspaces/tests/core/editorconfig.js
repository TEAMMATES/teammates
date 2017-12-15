var
	fs = require('fs'),
	path = require('path'),
	Validator = require('./../../lib/Validator'),
	validator,
	options
;

exports.tests = {
	'should override the settings by editorconfig': function(test) {
		options = {
			trailingspaces: false,
			newlineMaximum: false,
			indentation: 'spaces',
			spaces: 2,
			newline: false,
			ignores: ['js-comments'],
			endOfLine: false,
			editorconfig: '.editorconfig'
		};

		// fake loading:
		validator = new Validator(options);
		validator._path = __filename;
		validator._loadSettings();

		// newline:
		test.ok(validator._settings.newline !== options.newline);
		test.equal(validator._settings.newline, true);

		// indentation will be overwritten:
		test.ok(validator._settings.indentation !== options.indentation);
		test.equal(validator._settings.indentation, 'tabs');

		// spaces will be overwritten:
		test.ok(validator._settings.spaces !== options.spaces);
		test.equal(validator._settings.spaces, 'tab');

		// trailingspaces will be overwritten:
		test.ok(validator._settings.trailingspaces !== options.trailingspaces);
		test.equal(validator._settings.trailingspaces, true);

		// newlineMaximum will be unchanged:
		test.equal(validator._settings.newlineMaximum, options.newlineMaximum);

		// endOfLine will be overriden:
		test.ok(validator._settings.endOfLine !== options.endOfLine);
		test.equal(validator._settings.endOfLine, 'lf');

		test.done();
	},

	'should load specific settings by extension': function(test) {
		options = {
			editorconfig: '.editorconfig'
		};

		// Load editorconfig with extension where options are disabled
		validator = new Validator(options);
		validator._path = __dirname + '/fixures/file.example';
		validator._loadSettings();

		test.equal(validator._settings.trailingspaces, false);
		test.equal(validator._settings.newline, false);
		test.equal(validator._settings.endOfLine, 'lf');


		// Load editorconfig with extension where options are enabled
		validator = new Validator(options);
		validator._path = __dirname + '/fixures/file.other-example';
		validator._loadSettings();

		test.equal(validator._settings.trailingspaces, true);
		test.equal(validator._settings.newline, true);
		test.equal(validator._settings.endOfLine, 'crlf');

		test.done();
	},

	'should throw an exception if editorconfig has no valid path': function(test) {
		test.throws(function() {
			validator = new Validator({editorconfig: '.'});
			validator.validate(__filename);
		}, Error);

		test.throws(function() {
			validator = new Validator({editorconfig: __dirname});
			validator.validate(__filename);
		}, Error);

		test.throws(function() {
			validator = new Validator({editorconfig: __dirname + '/path/that/doesnt/existis/.editorconfig'});
			validator.validate(__filename);
		}, Error);

		test.done();
	},

	'should overwrite previous loaded rcconfig values': function(test) {
		var
			configFile = path.join(__dirname.toString(), '..', '..', '.' + Validator.APPNAME + 'rc'),
			rcconfig = {
				indentation: 'spaces',
				trailingspaces: false,
				newline: false
			},
			validator
		;

		// create config file:
		fs.writeFileSync(configFile, JSON.stringify(rcconfig));

		// fake loading:
		validator = new Validator({
			rcconfig: true,
			editorconfig: '.editorconfig',
			newline: 'foo'
		});
		validator._path = __filename;
		validator._loadSettings();

		// remove config file:
		fs.unlinkSync(configFile);

		// test for expected properties by editorconfig:
		test.equal(validator._settings.indentation, 'tabs');
		test.equal(validator._settings.trailingspaces, true);
		test.equal(validator._settings.newline, true);

		test.done();
	}
};
