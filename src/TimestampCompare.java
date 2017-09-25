
import java.util.Comparator;

//Class to compare and sort Timestamp by host
public class TimestampCompare implements Comparator<Timestamp> {

	@Override
	public int compare(Timestamp t1, Timestamp t2) {
		return t1.getHost().compareTo(t2.getHost());
	}

}
