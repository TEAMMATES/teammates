(function () {
    function getGitHubLink(username, name) {
        return `<a href="https://github.com/${username}" target="_blank" rel="noopener noreferrer">${name || `@${username}`}</a>`;
    }

    $.getJSON(`${window.location.origin}/data/developers.json`, (data) => {
        $.each(data.contributors, (i, contributor) => {
            const $div = contributor.multiple ? $('#contributors-multiple') : $('#contributors-single');
            $div.append(
                `<li>
                    ${contributor.username ? getGitHubLink(contributor.username, contributor.name) : contributor.name}
                </li>`
            );
        });

        $.each(data.committers, (i, committer) => {
            const $div = committer.endPeriod ? $('#committers-past') : $('#committers-current');
            $div.append(
                `<li>
                    ${committer.image
                            ? `<img src="${committer.image}" alt="${committer.name}" width="120px"><br>` : ''}
                    ${getGitHubLink(committer.username, committer.name)}
                    (${committer.startPeriod} - ${(committer.endPeriod ? committer.endPeriod : '')})
                </li>`
            );
        });
    });
}());
