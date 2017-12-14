module.exports = {
	encoding: 'utf8',
	newline: false,
	newlineMaximum: false,
	indentation: false, // 'tabs' or 'spaces' or false
	spaces: 4, // amount of spaces when 'indentation' is set to 'spaces'
	indentationGuess: false, // guess indentation
	trailingspaces: false,
	trailingspacesToIgnores: false, // ignore trailingspaces in ignored lines
	trailingspacesSkipBlanks: false, // skip trailingspaces in blank lines
	ignores: false, // pattern or string for lines to ignore
	editorconfig: false, // path to editor-config file
	rcconfig: false, // path to rc-config file
	allowsBOM: false,
	end_of_line: false // 'LF' or 'CRLF' or 'CR' or false to disable checking
};
