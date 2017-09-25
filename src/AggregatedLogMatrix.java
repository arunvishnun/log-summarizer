
public class AggregatedLogMatrix {
	
	private int min;
	private int max;
	private int count;
	private int totalServiceTime;
	
	public AggregatedLogMatrix() {
		this.totalServiceTime = 0;
		this.min = 0;
		this.max = 0;
		this.count = 0;
	}
	
	public int getMin() {
		return min;
	}
	public void setMin(int min) {
		this.min = min;
	}
	public int getMax() {
		return max;
	}
	public void setMax(int max) {
		this.max = max;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public int getTotalServiceTime() {
		return totalServiceTime;
	}
	public void setTotalServiceTime(int service) {
		this.totalServiceTime = service;
	}
	public void setAll(int service, int count, int min, int max) {
		this.totalServiceTime = service;
		this.count = count;
		this.min = min;
		this.max = max;
	}
	
}
