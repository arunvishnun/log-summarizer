import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main {
	private static final String DEFAULT_DATE = "1753-01-01T00:00:00";

	public static void main(String[] args) throws IOException {
		String resolution = "60";
		String outputFile = args[1];
		long startTime = System.currentTimeMillis();
		
		readLogFile(args[0], outputFile, resolution);
		
		long elapsedTime = System.currentTimeMillis() - startTime;
		System.out.println("Execution time in milliseconds: " + elapsedTime);
	}

	public static void readLogFile(String filename, String outputFile, String resolution) throws IOException {
		
		String line;
		String host = "";

		AggregatedLogMatrix matrix = null;

		// Final object to be used for iteration and CSV creation.Contains Nodes that can be traversed inorder (sorted order) while writing to file.
		AVLTree logParser = new AVLTree();

		// Map with Key = timestamp + host; value = an AggregatedLogMatrix .
		Map<String, AggregatedLogMatrix> aggregationMap = new HashMap<String, AggregatedLogMatrix>();

		FileInputStream inputStream = null;
		Scanner sc = null;

		try {
			inputStream = new FileInputStream(filename);
			sc = new Scanner(inputStream, "UTF-8");

			// Set a default current window to start with.
			Timestamp timestamp = new Timestamp(Util.formatDate(DEFAULT_DATE,
					resolution), host);
			
			List<String> hostList = new ArrayList<String>();
			String previousWindow = timestamp.getCurrentWindow();
			while (sc.hasNextLine()) {

				line = sc.nextLine();
				
				String newWindow = Util.getCurrentTimestamp(line, resolution);

				// Get host and service time for this log line.
				String keyValuePairs[] = Util.getLogParts(line)[1].trim().split(" ");

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
				
				if(!previousWindow.equals(newWindow)) {
					for(String h: hostList) {
						logParser.root = logParser.insert(logParser.root, newWindow, h);
						matrix = new AggregatedLogMatrix();
						aggregationMap.put(newWindow + h, matrix);
					}
				}
				previousWindow = newWindow;
				if (aggregationMap.containsKey(newWindow + host)) {
					matrix = aggregationMap.get(newWindow + host);
					
					matrix.setTotalServiceTime(matrix.getTotalServiceTime()
							+ service);
					matrix.setCount(matrix.getCount() + 1);
					if (service < matrix.getMin() || matrix.getMin() == 0)
						matrix.setMin(service);
					if (service > matrix.getMax())
						matrix.setMax(service);

					aggregationMap.put(newWindow + host, matrix);
				} else {
					matrix = new AggregatedLogMatrix();
					matrix.setAll(service, 1, service, service);
					aggregationMap.put(newWindow + host, matrix);
					
					// newWindow + host key is not availble in Map. ie Either new window or new host. So add it.
					logParser.root = logParser.insert(logParser.root, newWindow, host);
				}
				
				if(!hostList.contains(host)) hostList.add(host);
			}

			Util.createCSV(logParser.root, aggregationMap, outputFile);
			
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
