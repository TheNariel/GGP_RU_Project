package galp.ggp.main;

public class avgMain {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int i = 0;
		double a = 0;

		for (int j = 1; j < 11; j++) {
			i++;
			a = a*(i-1)/i+j/i;
			System.out.println(j);
			System.out.println(i);
			System.out.println(a);
			System.out.println();
		}
		System.out.println(((1.0+2+3+4+5+6+7+8+9+10)/10));


	}

}

