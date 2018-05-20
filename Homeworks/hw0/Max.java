
public class Max {
	public static void main (String[] args) {
     	int[] test = {1, 4, 3, 2};
		max(test);
	}

	private static void max (int[] a) {
		int[] b= a;
		int len = b.length;
		int temp = b[0];
		for (int i = 1; i < len; i += 1 ) {
			if (temp < b[i]) {
				temp = b[i];
			}
		}
		System.out.println(temp);
	}
}