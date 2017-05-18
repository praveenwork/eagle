import java.util.Arrays;

public class Test{

	public static void main(String[] arg){
		String inputString = "the sky is blue";
		String[] wordsArray = inputString.split(" ");
		String[] result = new String[wordsArray.length];
		for(int i=wordsArray.length-1, j=0;i>=0;i--,j++){
			result[j] = new String(wordsArray[i]);
		}
		String results = Arrays.toString(result);
		System.out.println(results);
	}

}
