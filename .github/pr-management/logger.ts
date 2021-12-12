/**
 * This module contains functions to that log using the core module with the format "label: itemToLog". 
 */

import * as core from '@actions/core'

////// functions to help with logging //////
function info(toPrint : any, label? : string) {
    core.info(`${label}: ${toPrint}`);
}

function jsonInfo(jsonToPrint: JSON, label? : string) {
    core.info(`${label}: ${JSON.stringify(jsonToPrint)}`);
}

function warn(toPrint : any, label? : string) {
    core.warning(`${label}: ${toPrint}`);
}

export const log = { 
    info: info, 
    warn: warn, 
    jsonInfo: jsonInfo 
};

// TODO encapsulating core.logging functions with log 