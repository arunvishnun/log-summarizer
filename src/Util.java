
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
	
	public static Date convertToDate(String date) {
		SimpleDateFormat formatter = new SimpleDateFormat(DATEFORMAT);
		Date newDate = null;
		try {

            newDate = formatter.parse(date);
            
        } catch (ParseException e) {
            e.printStackTrace();
        }
		return newDate;
	}

	public static String[] getLogParts(String logLine) {
		return logLine.split("heroku\\[router\\]\\:");
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
	
	public static void createCSV(Timestamp t, Map<String, AggregatedLogMatrix> aggregationMatrix, String filename) {
		String csvFile = filename;
		FileWriter writer;
		try {
			writer = new FileWriter(csvFile);
			
			inOrder(t, aggregationMatrix, writer);
			
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void inOrder(Timestamp t, Map<String, AggregatedLogMatrix> aggregationMatrix, FileWriter writer) {
		try {
			if (t != null) {
				AggregatedLogMatrix matrix = aggregationMatrix.get(t.getCurrentWindow() + t.getHost());
				List<String> outPutList = new ArrayList<String>();
				inOrder(t.left, aggregationMatrix, writer);
				if (matrix != null) {
					
					outPutList.add(t.getCurrentWindow());
					outPutList.add(t.getHost());
					outPutList.add(Integer.toString(matrix.getCount()));
					outPutList.add(Integer.toString(matrix.getTotalServiceTime()));
					outPutList.add(Integer.toString(matrix.getMin()));
					outPutList.add(Integer.toString(matrix.getMax()));

					Util.writeLine(writer, outPutList);
				}
				inOrder(t.right, aggregationMatrix, writer);
				
			} 
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
