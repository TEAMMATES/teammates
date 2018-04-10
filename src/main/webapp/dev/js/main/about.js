(function () {
    function getLinkToUrl(url, name) {
        return `<a href="${url}" target="_blank" rel="noopener noreferrer">${name}</a>`;
    }

    function getGitHubLink(username, name) {
        return getLinkToUrl(`https://github.com/${username}`, name || `@${username}`);
    }

    function listDownPastPositions(pastPositions) {
        if (!pastPositions) {
            return '';
        }
        if (typeof pastPositions === 'object') {
            return `<br>${pastPositions.join('<br>')}`;
        }
        return ` (${pastPositions})`;
    }

    $.getJSON(`${window.location.origin}/data/developers.json`, (data) => {
        $('#contributors-count').html(data.contributors.length + data.committers.length + data.teammembers.length);

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
                        ${committer.image ? `<img src="${committer.image}" alt="${committer.name}" width="120px"><br>` : ''}
                        ${getGitHubLink(committer.username, committer.name)}
                        (${committer.startPeriod} - ${(committer.endPeriod ? committer.endPeriod : '')})
                    </li>`
            );
        });

        $.each(data.teammembers, (i, teammember) => {
            let url;
            if (teammember.url) {
                url = getLinkToUrl(teammember.url, teammember.name);
            } else if (teammember.username) {
                url = getGitHubLink(teammember.username, teammember.name);
            } else {
                url = teammember.name;
            }
            if (teammember.currentPosition) {
                const img = teammember.image
                        ? `<div class="col-xs-8 col-xs-offset-2 col-sm-5 col-sm-offset-0 col-md-4 col-lg-3">
                            <img class="img-responsive" src="${teammember.image}" alt="${teammember.name}">
                        </div>`
                        : '';
                $('#teammembers-current').append(
                        `<div class="row margin-top-7px${img ? '' : ' margin-bottom-35px'}">
                            ${img}
                            <div class="col-xs-10 col-xs-offset-1 col-sm-7 col-sm-offset-0 col-md-8 margin-top-7px">
                                <strong>${url}</strong>
                                <br><br>
                                <strong>${teammember.currentPosition}</strong>
                                ${listDownPastPositions(teammember.pastPositions)}
                            </div>
                        </div>`
                );
            } else {
                $('#teammembers-past').append(
                        `<li>
                            ${teammember.image
                                    ? `<img src="${teammember.image}" alt="${teammember.name}" width="120px"><br>` : ''}
                            ${url}
                            ${listDownPastPositions(teammember.pastPositions)}
                        </li>`
                );
            }
        });
    });
}());
