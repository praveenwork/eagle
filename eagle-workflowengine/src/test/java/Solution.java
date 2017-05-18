import java.io.*;
import java.util.*;

public class Solution {

    public static void main(String[] args) {
        /* Enter your code here. Read input from STDIN. Print output to STDOUT. Your class should be named Solution. */
        Scanner scanner =  new Scanner(System.in);
        int n = scanner.nextInt();
        for(int i = 0;i<n;i++){
            String inputStrirng = scanner.next();
            System.out.println(palandromIndex(inputStrirng));
        }
        scanner.close();
    }
    
    private static int palandromIndex(String inputString){
    	int len = inputString.length();
        char[] iChars = inputString.toCharArray();
        boolean notMatch = false;
        int notMatchJ = len -1;	
        for(int i=0,j=len-1;i<len;){
			if (iChars[i] != (iChars[j])) {
				notMatch = true;
				if (j == 0) {
					System.out.println("return i = "+i);
					return i;
				} else {
					notMatchJ  = j;
					j--;
				}
			} else {
				if(notMatch){
					return notMatchJ;
				}
				i++;
				j--;
            }
        } 
        return -1;
    }
    
    
}