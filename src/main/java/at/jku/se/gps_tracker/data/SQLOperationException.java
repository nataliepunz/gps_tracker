package at.jku.se.gps_tracker.data;

@SuppressWarnings("serial")
public class SQLOperationException extends Exception {
	
	public SQLOperationException(String errorMessage) {
        super(errorMessage);
    }
	
}
