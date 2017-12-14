#!/usr/bin/env node

'use strict';

// It's colours I tell ya. Colours!!!
require('colors'); // Oh yeah, this enables adding colour code to strings

var fs = require('fs')
  , map = require('lodash.map')
  , util = require('util')
  , path = require('path')
  , program = require('commander')
  , glob = require('glob')
  , Validator = require('lintspaces')
  , version = require('./package.json').version
  , validator = null
  , targetFiles = null
  , files = null;


/**
 * Accumlate list items.
 * @param {String}
 */
function list(list) {
  list = list || [];

  return function (entry) {
    list.push(entry);
    return list;
  };
}


/**
 * Check does the provided editorconfig exist
 * @param {String}
 */
function resolveEditorConfig (e) {
  if (e) {
    e = path.resolve(e);

    if (!fs.existsSync(e)) {
      console.log('Error: Specified .editorconfig "%s" doesn\'t exist'.red, e);
      process.exit(1);
    }

    return e;
  }

  return e;
}


program.version(version)
  .option('-n, --newline', 'Require newline at end of file.')
  .option('-g, --guessindentation', 'Tries to guess the indention of a line ' +
    'depending on previous lines.')
  .option('-b, --skiptrailingonblank', 'Skip blank lines in trailingspaces ' +
    'check.')
  .option('-it, --trailingspacestoignores', 'Ignore trailing spaces in ' +
    'ignores.')
  .option('-l, --maxnewlines <n>', 'Specify max number of newlines between' +
    ' blocks.', parseInt)
  .option('-t, --trailingspaces', 'Tests for useless whitespaces' +
    ' (trailing whitespaces) at each lineending of all files.')
  .option('-d, --indentation <s>', 'Check indentation is "tabs" or "spaces".')
  .option('-s, --spaces <n>', 'Used in conjunction with -d to set number of ' +
    'spaces.', parseInt)
  .option('-i, --ignores <items>', 'Comma separated list of ignores built in ' +
    'ignores.', list(), [])
  .option('-r, --regexIgnores <items>', 'Comma separated list of ignores that' +
    ' should be parsed as Regex', list(), [])
  .option('-e, --editorconfig <s>', 'Use editorconfig specified at this ' +
   'file path for settings.', resolveEditorConfig)
  .option('-o, --allowsBOM', 'Sets the allowsBOM option to true')
  .option('-v, --verbose', 'Be verbose when processing files')
  .option('-., --matchdotfiles', 'Match dotfiles')
  .option('--endOfLine <s>')
  .parse(process.argv);

// Map regexIgnores to RegExp objects
program.regexIgnores = map(program.regexIgnores, function (r) {
  return new RegExp(r);
});

// Setup validator with user options
validator = new Validator({
  newline: program.newline,
  newlineMaximum: program.maxnewlines,
  trailingspaces: program.trailingspaces,
  indentation: program.indentation,
  spaces: program.spaces,
  ignores: program.ignores,
  editorconfig: program.editorconfig,
  indentationGuess: program.guessindentation,
  trailingspacesSkipBlanks: program.skiptrailingonblank,
  trailingspacesToIgnores: program.trailingspacesToIgnores,
  allowsBOM: program.allowsBOM,
  verbose: program.verbose,
  endOfLine: program.endOfLine
});


// Resolve all glob patterns and merge them into one array
targetFiles = Array.prototype.concat.apply([], program.args.map(function(file) {
  return glob.sync(file, {
    dot: program.matchdotfiles,
  });
}));

targetFiles = targetFiles
  .filter(fs.existsSync.bind(fs))
  .filter(function (path) {
    return fs.statSync(path).isFile()
  });


// Run validation
if (program.verbose) {
  console.info('Number of files to check: ' + targetFiles.length);
}
for (var file in targetFiles) {
  var filepath = path.resolve(targetFiles[file]);
  if (program.verbose) {
    console.info('Checking: ' + filepath);
  }
  validator.validate(filepath);
}
files = validator.getInvalidFiles();


// Output results
for (var file in files) {
  var curFile = files[file];
  console.warn(util.format('\nFile: %s', file).red.underline);

  for (var line in curFile) {
    var curLine = curFile[line];

    for(var err in curLine) {
      var curErr = curLine[err]
        , msg = ''
        , errMsg = curErr.type;

      if (errMsg.toLowerCase() === 'warning') {
        errMsg = errMsg.red;
      } else {
        errMsg = errMsg.green;
      }

      msg = util.format('Line: %s %s [%s]', line, curErr.message, errMsg);

      console.warn(msg);
    }
  }
}


// Give error exit code if required
if (Object.keys(files).length) {
  process.exit(1);
}
