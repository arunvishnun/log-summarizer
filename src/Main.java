import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main {
	private static final String DEFAULT_DATE = "1753-01-01T00:00:00";

	public static void main(String[] args) throws IOException {

//		long startTime = System.currentTimeMillis();
		
		String resolution = "60";
		String outputFile = args[1];
		readLogFile(args[0], outputFile, resolution);

		/*
		long endTime = System.currentTimeMillis();
		long totalTime = endTime - startTime;

		NumberFormat formatter = new DecimalFormat("#0.00000");
		System.out.print("Execution time is "
				+ formatter.format(totalTime / 1000d) + " seconds");
		 */
	}

	public static void readLogFile(String filename, String outputFile, String resolution) throws IOException {
		String newWindow = null;
		String line;
		String host = "";
		
		AggregatedLogMatrix matrix = null;
		List<Timestamp> sortedList = new ArrayList<Timestamp>();
		TimestampCompare timestampCompare = new TimestampCompare();

		// Final object to be used for iteration and CSV creation.
		List<Timestamp> logParser = new ArrayList<Timestamp>();

		// Map with Key = timestamp + host; value = an AggregatedLogMatrix .
		Map<String, AggregatedLogMatrix> aggregationMatrix = new HashMap<String, AggregatedLogMatrix>();

		FileInputStream inputStream = null;
		Scanner sc = null;

		try {
			inputStream = new FileInputStream(filename);
			sc = new Scanner(inputStream, "UTF-8");

			// Set a default current window to start with.
			Timestamp timestamp = new Timestamp(Util.formatDate(DEFAULT_DATE,
					resolution), host);

			List<String> hostList = null;
			while (sc.hasNextLine()) {

				line = sc.nextLine();
				newWindow = Util.getCurrentTimestamp(line, resolution);

				// Get host and service time for this log line.
				String keyValuePairs[] = Util.getLogParts(line)[1].trim()
						.split(" ");
				int service = 0;
				for (String pair : keyValuePairs) {
					String[] entry = pair.split("=");
					String value = entry[1].trim();
					String key = entry[0].trim();

					if (key.equals("host")) {
						host = value.substring(1, value.length() - 1);
					}

					if (key.equals("service")) {
						service = Integer.parseInt(value.substring(0,
								value.length() - 2));
					}
				}

				String previousWindow = timestamp.getCurrentWindow();

				// Check if the new timestamp in same window.
				if (previousWindow.equals(newWindow)) {
					// Logs are within the window.

					// Check if current host is already available, otherwise add it.
					if (!hostList.contains(host)) {
						// Host is not available in the same window, so add it
						// and create a new timestamp object and push it to log list
						hostList.add(host);
						
						timestamp = new Timestamp(newWindow, host);
						
						matrix = new AggregatedLogMatrix();
						matrix.setAll(service, 1, service, service);
						
						aggregationMatrix.put(newWindow + host, matrix);
						sortedList.add(timestamp);
					} else {
						// Host already added in the same window; Perform
						// aggregation operations.

						// Get from aggregationMatrix and set it back after
						// aggregation.
						matrix = aggregationMatrix.get(newWindow + host);
						matrix.setTotalServiceTime(matrix.getTotalServiceTime() + service);
						matrix.setCount(matrix.getCount() + 1);
						if(service < matrix.getMin()) matrix.setMin(service);
						if(service > matrix.getMax()) matrix.setMax(service);
						
						aggregationMatrix.put(newWindow + host, matrix);
					}
				} else {

					hostList = new ArrayList<String>();
					hostList.add(host);

					timestamp = new Timestamp(newWindow, host);

					matrix = new AggregatedLogMatrix();
					matrix.setAll(service, 1, service, service);
					
					aggregationMatrix.put(newWindow + host, matrix);
					
					sortedList.sort(timestampCompare);
					logParser.addAll(sortedList);
					
					sortedList = new ArrayList<Timestamp>();
					sortedList.add(timestamp);
				}
			}

			sortedList.sort(timestampCompare);
			logParser.addAll(sortedList);
			Util.writeToCSV(logParser, aggregationMatrix, outputFile);

			// note that Scanner suppresses exceptions
			if (sc.ioException() != null) {
				throw sc.ioException();
			}
		} finally {
			if (inputStream != null) {
				inputStream.close();
			}
			if (sc != null) {
				sc.close();
			}

		}
	}
}
