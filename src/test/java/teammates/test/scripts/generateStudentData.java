package teammates.test.scripts;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;


public class generateStudentData {	
	
	//List of characters that can be used
	static final String characters = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXTZabcdefghiklmnopqrstuvwxyz";
	
	static ArrayList<String> teamnames = new ArrayList<String>(); 
	
	public static void main(String args[]) throws IOException{
		int studentnum = 100;
		int teamnum = 5;
		int namelength = 8;
		int teamlength = 3;
		
		
		if(args.length == 4){
			//Student num, team num, student name length, team name length
			studentnum = Integer.parseInt(args[0]);
			teamnum = Integer.parseInt(args[1]);
			namelength = Integer.parseInt(args[2]);
			teamlength = Integer.parseInt(args[3]);
		}
		else{
			System.out.println("Command arguments are java generateStudentData <number of students> <number of teams> <length of student names> <length of team name>");
			System.out.println("Invalid arguments. Default arguments of <100> <5> <8> <3> are used");
		}
		Random rnd = new Random();
	
		for (int i = 0; i < teamnum; i++){
			String team = new String();
			
			for (int j = 0; j < teamlength; j++){
				team += characters.charAt(rnd.nextInt(characters.length()));
			}
			
			teamnames.add("Team " + team);
		}
		
		for (int i = 0; i < studentnum; i++){
			String student = new String();
			String name = new String();
			
			for (int j = 0; j < namelength; j++){
				name += characters.charAt(rnd.nextInt(characters.length()));
			}
			
			student += teamnames.get(rnd.nextInt(teamnames.size())) + '|' + name + '|' + name + "@gmail.com";
			System.out.println(student);
		}

	}
}
