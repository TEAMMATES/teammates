package teammates.storage.entity;

import javax.jdo.annotations.Extension;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Text;

/**
 * Represents a unique user in the system. 
 */
@PersistenceCapable
public class LogEntry {

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	@Extension(vendorName = "datanucleus", key = "gae.unindexed", value = "true")
	private Long id;

	@Persistent
	@Extension(vendorName = "datanucleus", key = "gae.unindexed", value = "true")
	private String servletName;
	
	@Persistent
	@Extension(vendorName = "datanucleus", key = "gae.unindexed", value = "true")
	private String action;
	
	@Persistent
	@Extension(vendorName = "datanucleus", key = "gae.unindexed", value = "true")
	private boolean hasModifiedEntities;

	@Persistent
	@Extension(vendorName = "datanucleus", key = "gae.unindexed", value = "true")
	private boolean toShow;

	@Persistent
	@Extension(vendorName = "datanucleus", key = "gae.unindexed", value = "true")
	private String role;

	@Persistent
	@Extension(vendorName = "datanucleus", key = "gae.unindexed", value = "true")
	private String name;
	
	@Persistent
	@Extension(vendorName = "datanucleus", key = "gae.unindexed", value = "true")
	private String googleId;
	
	@Persistent
	@Extension(vendorName = "datanucleus", key = "gae.unindexed", value = "true")
	private String email;
	
	@Persistent
	@Extension(vendorName = "datanucleus", key = "gae.unindexed", value = "true")
	private Text message;
	
	@Persistent
	@Extension(vendorName = "datanucleus", key = "gae.unindexed", value = "true")
	private String url;

	@Persistent
	private long createdAt;

	/**
	 * Instantiates a new activity log. 
	 */
	public LogEntry(String servletName, String action, boolean toShow,
			String role, String name, String googleId, String email, String message, String url, long time) {
		this.setServletName(servletName);
		this.setAction(action);
		this.setToShow(toShow);
		this.setRole(role);
		this.setName(name);
		this.setGoogleId(googleId);
		this.setEmail(email);
		this.setMessage(message);
		this.setUrl(url);
		this.setCreatedAt(time);
		// TODO : add proper methods here ?
		this.setHasModifiedEntities(false);
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public boolean isToShow() {
		return toShow;
	}

	public void setToShow(boolean toShow) {
		this.toShow = toShow;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGoogleId() {
		return googleId;
	}

	public void setGoogleId(String googleId) {
		this.googleId = googleId;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMessage() {
		return message.toString();
	}

	public void setMessage(String message) {
		this.message = new Text(message);
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public long getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(long createdAt) {
		this.createdAt = createdAt;
	}

	public String getServletName() {
		return servletName;
	}

	public void setServletName(String servletName) {
		this.servletName = servletName;
	}

	public boolean isHasModifiedEntities() {
		return hasModifiedEntities;
	}

	public void setHasModifiedEntities(boolean hasModifiedEntities) {
		this.hasModifiedEntities = hasModifiedEntities;
	}
	
}
