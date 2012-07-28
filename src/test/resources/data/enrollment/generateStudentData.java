import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;


public class generateStudentData {	
	//number of students
	static int studentnum = 300;
	
	//Length of name
	static int namelength = 8;
	
	//Length of team name
	static int teamlength = 3;
	
	//List of characters that can be used
	static final String characters = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXTZabcdefghiklmnopqrstuvwxyz";
	
	public static void main(String args[]) throws IOException{
		Random rnd = new Random();
		
		FileWriter fstream = new FileWriter(studentnum + " students.txt");
		BufferedWriter out = new BufferedWriter(fstream);
		
		for (int i = 0; i < studentnum; i++){
			String student = new String();
			String name = new String();
			String team = new String();
			
			for (int j = 0; j < teamlength; j++){
				team += characters.charAt(rnd.nextInt(characters.length()));
			}
			
			for (int j = 0; j < namelength; j++){
				name += characters.charAt(rnd.nextInt(characters.length()));
			}
			
			student += "Team " + team + '|' + name + '|' + name + "@gmail.com";
			out.write(student);
			out.newLine();
		}
		
		out.close();
	}
}
