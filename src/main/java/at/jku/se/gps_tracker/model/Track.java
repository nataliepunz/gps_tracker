package at.jku.se.gps_tracker.model;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
/**
 * class for Track objects
 * extends from AbstractTrack
 * @author Ozan
 */
public class Track extends AbstractTrack {
	/**
	 * represents the date of the Track
	 * @author Ozan
	 */
	private final LocalDate date;
	
	/**
	 * represents the startTime of the Track
	 * @author Ozan
	 */
	private final LocalTime startTime;
	
	/**
	 * saves the fileName of the associated file to this track
	 * necessary for database operations
	 * @author Ozan
	 */
	private final String fileName;
	
	/**
	 * saves the parentDirectory of the associated file to this track
	 * necessary for database operations
	 * @author Ozan
	 */
	private final String parentDirectory;
	
	/**
	 * saves the associated trackpoints to this track
	 * @author Ozan
	 */
	private List<TrackPoint> trackPoints;
	
	/**
	 * returns track object out of given TrackBuilder due to high number of variables
	 * @author Ozan
	 * @param trackBuilder
	 */
	private Track(TrackBuilder trackBuilder) {
		super(trackBuilder.name,trackBuilder.distance,trackBuilder.duration,trackBuilder.averageBPM,trackBuilder.maximumBPM,trackBuilder.elevation);
		this.fileName=trackBuilder.fileName;
		this.date=trackBuilder.trackDate;
		this.startTime = trackBuilder.trackTime;
		this.parentDirectory=trackBuilder.parentDirectory;
		this.trackPoints = trackBuilder.trackPoints;
	}

	/**
	 * returns empty track object
	 * @author Nuray
	 */
	public Track() {
		this.date = null;
		this.startTime = null;
		this.fileName = null;
		this.parentDirectory = null;
	}
	
	/**
	 * return the date of the track
	 * @author Ozan
	 * @return date of track as LocalDate
	 */
	public LocalDate getDate() {
		return date;
	}
	
	/**
	 * return the startTime of the track
	 * @author Ozan
	 * @return startTime of track as LocalTime
	 */
	public LocalTime getStartTime() {
		return startTime;
	}
	
	/**
	 * return the list of associated trackpoints
	 * @author Ozan
	 * @return trackpoints as List TrackPoint
	 */
	public List<TrackPoint> getTrackPoints() {
		return this.trackPoints;
	}

	/**
	 * returns the fileName of the associated file
	 * @author Ozan
	 * @return fileName as String
	 */
	public String getFileName() {
		return this.fileName;
	}
	
	/**
	 * returns the parentDirectory of the associated file
	 * @author Ozan
	 * @return parentDirectory as String
	 */
	public String getParentDirectory() {
		return this.parentDirectory;
	}
	
	/**
	 * sets the associated trackPoints
	 * @author Ozan
	 * @param trackpoints as List TrackPoint
	 */
	public void setTrackPoints(List<TrackPoint> trackpoints) {
		this.trackPoints=trackpoints;
	}
	
	/**
	 * usage of Builder Pattern due to high number of variables
	 * @author Ozan
	 */
	public static class TrackBuilder{
		/**
		 * the name of the track
		 * @author Ozan
		 */
		private String name;
		
		/**
		 * the file name of the associated file
		 * @author Ozan
		 */
		private String fileName;
		
		/**
		 * the parentDirectory of the associated file
		 * @author Ozan
		 */
		private String parentDirectory;
		
		/**
		 * the date of the track
		 * @author Ozan
		 */
		private LocalDate trackDate;
		
		/**
		 * the startTime of the track
		 * @author Ozan
		 */
		private LocalTime trackTime;
		
		/**
		 * the distance of the track
		 * @author Ozan
		 */
		private double distance;
		
		/**
		 * the duration of the track
		 * @author Ozan
		 */
		private Duration duration;
		
		/**
		 * the averageBPM of the track
		 * @author Ozan
		 */
		private int averageBPM;
		
		/**
		 * the maximumBPM of the track
		 * @author Ozan
		 */
		private int maximumBPM;
		
		/**
		 * the elevation gains of the track
		 * @author Ozan
		 */
		private double elevation;
		
		/**
		 * the associated trackpoints of the track
		 * @author Ozan
		 */
		private List<TrackPoint> trackPoints;

		/**
		 * constructor to create a TrackBuilder object, the parameter are the minimum necessary information for a valid track
		 * @author Ozan
		 * @param parentDirectory
		 * @param fileName
		 * @param name
		 * @param trackDate
		 * @param trackTime
		 */
		public TrackBuilder (String parentDirectory, String fileName, String name, LocalDate trackDate, LocalTime trackTime) {
			this.name=name;
			this.fileName=fileName;
			this.parentDirectory=parentDirectory;
			this.trackDate=trackDate;
			this.trackTime=trackTime;
		}

		/**
		 * add distance of Track to TrackBuilder
		 * @author Ozan
		 * @param distance
		 * @return
		 */
		public TrackBuilder distance(double distance) {
			this.distance=distance;
			return this;
		}
	
		/**
		 * add duration of Track to TrackBuilder
		 * @author Ozan
		 * @param duration
		 * @return
		 */
		public TrackBuilder duration(Duration duration) {
			this.duration=duration;
			return this;
		}

		/**
		 * add averageBPM of Track to TrackBuilder
		 * @author Ozan
		 * @param averageBPM
		 * @return
		 */
		public TrackBuilder averageBPM(int averageBPM) {
			this.averageBPM=averageBPM;
			return this;
		}

		/**
		 * add maximumBPM of Track to TrackBuilder
		 * @author Ozan
		 * @param maximumBPM
		 * @return
		 */
		public TrackBuilder maximumBPM(int maximumBPM) {
			this.maximumBPM=maximumBPM;
			return this;
		}

		/**
		 * add elevation gains of Track to TrackBuilder
		 * @author Ozan
		 * @param elevation
		 * @return
		 */
		public TrackBuilder elevation(double elevation) {
			this.elevation=elevation;
			return this;
		}
    
		/**
		 * add trackPoints associated to Track to TrackBuilder
		 * @author Ozan
		 * @param trackPoints
		 * @return
		 */
		public TrackBuilder trackPoints(List<TrackPoint> trackPoints) {
			this.trackPoints=trackPoints;
			return this;
		}
    
		/**
		 * "build" the Track out of information in TrackBuilder
		 * @author Ozan		
		 * @return Track out of TrackBuilder
		 */
		public Track build() {
			return new Track(this);
		}

	}

}