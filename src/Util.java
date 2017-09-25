
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;


public final class Util {

	private static final String DATEFORMAT = "yyyy-MM-dd'T'HH:mm:ss";
	private static final char DEFAULT_SEPARATOR = ',';
	
	public static String formatDate(String date, String resolution) {
		Date formattedDate = null;
		SimpleDateFormat dateFormat = new SimpleDateFormat(DATEFORMAT);

		try {
			formattedDate = (Date) dateFormat.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		Calendar c = Calendar.getInstance();
		c.setTime(formattedDate);
		int second = c.get(Calendar.SECOND);
		second = second * -1;
		if (second != 0) {
			c.add(Calendar.SECOND, second);
		}

		return dateFormat.format(c.getTime());
	}
	
	public static String getCurrentTimestamp(String logLine, String resolution) {
		String parts[];
		String currentTimestamp = null;
		parts = getLogParts(logLine);
		currentTimestamp = parts[0].split("\\+")[0];
		return Util.formatDate(currentTimestamp, resolution);
	}

	public static String[] getLogParts(String logLine) {
		return logLine.split("heroku\\[router\\]\\:");
	}
	
	public static void writeToCSV(List<Timestamp> timestampList, Map<String, AggregatedLogMatrix> aggregationMatrix, String outputFile) {	
		String csvFile = outputFile;
		FileWriter writer;
		try {
			writer = new FileWriter(csvFile);
			for(Timestamp list: timestampList) {
				
				AggregatedLogMatrix matrix = aggregationMatrix.get(list.getCurrentWindow() + list.getHost());
				List<String> outPutList = new ArrayList<String>();
				
				outPutList.add(list.getCurrentWindow());
				outPutList.add(list.getHost());
				outPutList.add(Integer.toString(matrix.getCount()));
				outPutList.add(Integer.toString(matrix.getTotalServiceTime()));
				outPutList.add(Integer.toString(matrix.getMin()));
				outPutList.add(Integer.toString(matrix.getMax()));
				
				writeLine(writer, outPutList);
				/*
				System.out.println(list.getCurrentWindow() 
						+ "," + list.getHost() 
						+ "," + matrix.getCount() 
						+ "," + matrix.getTotalServiceTime()
						+ "," + matrix.getMin()
						+ "," + matrix.getMax()
						);*/
				
				
			}
			writer.flush();
			writer.close();

		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}
	
	public static void writeLine(Writer w, List<String> values) throws IOException {

		boolean first = true;
		StringBuilder sb = new StringBuilder();
		
		for (String value : values) {
			if (!first) {
				sb.append(DEFAULT_SEPARATOR);
			}
			sb.append(value.replace("\"", "\"\""));
	
			first = false;
		}
		sb.append("\n");
		w.append(sb.toString());

	}
}
