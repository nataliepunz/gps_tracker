package at.jku.se.gps_tracker.data;

@SuppressWarnings("serial")
public class SQLRollbackException extends Exception {
	
	public SQLRollbackException(String errorMessage) {
        super(errorMessage);
    }
	
}
