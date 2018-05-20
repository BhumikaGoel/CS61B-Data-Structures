public class ThreeSum {
	public static void main (String[] args) {
		int[] test = {-6, 3, 10, 200};
		System.out.println(threeSum(test));
	}

	private static boolean threeSum (int[] a) {
		for (int i=0; i < a.length; i+=1) {
			for (int j=0; j < a.length; j+=1) {
				for (int k=0; k < a.length; k+=1) {
					if (a[i]+a[j]+a[k] == 0) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
}