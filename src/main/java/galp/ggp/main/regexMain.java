package galp.ggp.main;

public class regexMain {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		String s1 = "( <= ( goal xplayer 50 ) ( not ( line x ) ) ( not ( line o ) ) ( not open ) )";
//		String s2 = "( <= terminal ( not open ) )";
		String s1 = "( <= ( next ( cell ?m ?n x ) ) ( does xplayer ( mark ?m ?n ) ) ( true ( cell ?m ?n b ) ) )";

		String patternNot = "\\( not [a-zA-Z ]*\\)";
		String patternNotWParenth = "\\([ ]*not[ ]*\\([a-zA-Z ]*\\)[ ]*\\)";
		String s3 = s1.replaceAll("does", "legal");
		System.out.println(s1);
		System.out.println(s3);
//		String patternNot = "\\( not [a-zA-Z ]*\\)";
//		String patternNotWParenth = "\\([ ]*not[ ]*\\([a-zA-Z ]*\\)[ ]*\\)";
//		String s11 = s1.replaceAll(pattern1, "( trueValue )");
//		String s12 = s1.replaceAll(pattern2, "( trueValue )");
//		String s112 = s11.replaceAll(pattern2, "( trueValue )");
//		String s21 = s2.replaceAll(pattern1, "( trueValue )");
//		String s22 = s2.replaceAll(pattern2, "( trueValue )");
//		String s212 = s21.replaceAll(pattern2, "( trueValue )");
//
//		System.out.println("s1  : " + s1);
//		System.out.println("s11 : " + s11);
//		System.out.println("s12 : " + s12);
//		System.out.println("s112: " + s112);
//		System.out.println("s2  : " + s2);
//		System.out.println("s21 : " + s21);
//		System.out.println("s22 : " + s22);
//		System.out.println("s212: " + s212);

	}

}
