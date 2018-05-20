public class ThreeSumDistinct {
	public static void main (String[] args) {
		int[] test = {-6, 3, 10, 200};
		System.out.println(threeSumDistinct(test));
	}

	private static boolean threeSumDistinct (int[] a) {
		for (int i=0; i < a.length; i+=1) {
			for (int j=i; j < a.length; j+=1) {
				for (int k=j; k < a.length; k+=1) {
					if (a[i]+a[j]+a[k] == 0) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
}