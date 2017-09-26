
public class Timestamp {
	private String currentWindow;
	private String host;
	public int height;
	public Timestamp left, right; 
	
	// Set the current window and host.
	public Timestamp(String currentWindow, String host) {
		this.currentWindow = currentWindow;
		this.setHost(host);
	}

	public String getCurrentWindow() {
		return currentWindow;
	}

	public void setCurrentWindow(String currentWindow) {
		this.currentWindow = currentWindow;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

}
