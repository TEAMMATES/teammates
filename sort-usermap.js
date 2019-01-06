const fs = require('fs');
const userMapData = require('./src/main/webapp/data/userMapData.json');

const userMapPath = './src/main/webapp/data/userMapData.json';

// sort institute names in each country case insensitively
const countries = Object.keys(userMapData.institutes);
for (let i = 0; i < countries.length; i += 1) {
    const country = countries[i];
    userMapData.institutes[country].sort((a, b) => a.toLowerCase().localeCompare(b.toLowerCase()));
}

// write back to the file
const sortedUserMapData = `${JSON.stringify(userMapData, null, 2)}\n`;
fs.writeFile(userMapPath, sortedUserMapData, (err) => {
    if (err) throw err;
});
