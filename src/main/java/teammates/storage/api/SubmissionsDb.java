package teammates.storage.api;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;

import teammates.storage.datastore.Datastore;
import teammates.storage.entity.Submission;
import teammates.common.Common;
import teammates.common.datatransfer.SubmissionData;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidParametersException;


/**
 * Manager for handling basic CRUD Operations only
 *
 */
public class SubmissionsDb {
	
	private static final Logger log = Common.getLogger();
	
	private PersistenceManager getPM() {
		return Datastore.getPersistenceManager();
	}
	

	
	
	

	
	
	
	
	
}
