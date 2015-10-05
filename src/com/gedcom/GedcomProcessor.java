
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

	class Individual {
		String indi = "";
		String name = "";
		String sex = "";
		String famc = "";
		String fams = "";
		Date birthDate	= null;
		Date deathDate	= null;
		Boolean isDeadBln = false;
		
		//constructor for Individual class
		
		public Individual() {
			
		}
		
		public Individual(String indi,String name,String sex, String famc, String fams, Date birthDate, Date deathDate, Boolean isDeadBln)
		{
		this.indi = indi;
		this.name = name;
		this.sex = sex;
		this.famc = famc;
		this.fams = fams;
		this.birthDate = birthDate;
		this.deathDate = deathDate;
		}
	}

	class Family {
		
		String famId = "";
		String husbId = "";
		String wifeId = "";
		ArrayList<String> childIds = new ArrayList<String>();
		Date marriageDate = null;
		Date divorceDate = null;
		Boolean isDivorceBln = false;
		
		public Family() {
			
		}
		
		public Family(String famId, String husbId, String wifeId, ArrayList<String> childIds, Date marriageDate, Date divorceDate, Boolean isDBoolean) {
			this.famId = famId;
			this.husbId = husbId;
			this.wifeId = wifeId;
			this.childIds = childIds;
			this.marriageDate = marriageDate;
			this.divorceDate = divorceDate;
			this.isDivorceBln = isDBoolean;
		}	
	}

	public class GedcomProcessor {
		
		static Map < String, Individual > individualMap = new HashMap <> (); 
		static Map < String, Family > familyMap = new HashMap <> (); 
		static ArrayList<String> individualIdList = new ArrayList<>();
		static ArrayList<String> familyList = new ArrayList<>();
				
		public static void main(String[] args) throws IOException{

			ArrayList<String> validTags = new ArrayList<String>();
			validTags.add("INDI");
			validTags.add("NAME"); 
			validTags.add("SEX");
			validTags.add("BIRT");
			validTags.add("DEAT");
			validTags.add("FAMC");
			validTags.add("FAMS");
			validTags.add("FAM");
			validTags.add("MARR");
			validTags.add("HUSB");
			validTags.add("WIFE");
			validTags.add("CHIL");
			validTags.add("DIV");
			validTags.add("DATE");
			validTags.add("HEAD");
			validTags.add("TRLR");
			validTags.add("NOTE");
	        
			

			try {
				
				FileReader file = new FileReader("Test3.ged");
				
				/* This code is for command line input. 
				 * FileReader file = new FileReader(new File(args[0]));
				if (args.length==0) {
				     System.out.println("Error: Bad command or filename. ");
				     System.exit(0);
				}*/
				
				PrintStream outs = new PrintStream(new File("output.txt"));
		        System.setOut(outs);
			
		        Scanner inputFileData = new Scanner(file);
		        Individual member = null;
		        Family family = null;
		        
		       while(inputFileData.hasNextLine()) {				
					String line = inputFileData.nextLine();
					String inputElements[] = line.split(" "); 
					
					switch(inputElements[0]) {
					
						case "0":	if(inputElements.length > 2 && validTags.contains(inputElements[2])) {
										
										switch(inputElements[2]) {
							
											case  "INDI" :  member = new Individual();
															member.indi = inputElements[1].replace("@", "");
															individualMap.put(member.indi, member);
															individualIdList.add(member.indi);
															break;
															
											case  "FAM" :   family = new Family();
															family.famId = inputElements[1].replace("@", "");
															familyMap.put(family.famId, family);
															familyList.add(family.famId);
															break;
										}
									}
									break;
									
						case "1" :	if(inputElements.length > 1 && validTags.contains(inputElements[1])) {
							
										switch(inputElements[1]) {
																
											case "NAME" :	member.name = inputElements[2] + " " + inputElements[3].replace("/", "") ;
															break;
											case "SEX" :	member.sex = inputElements[2];
															break;
											case "FAMS" :	member.fams = inputElements[2].replace("@", "");							
															break;
											case "FAMC" :	member.famc = inputElements[2].replace("@", "");							
															break;
											case "HUSB" :	family.husbId = inputElements[2].replace("@", "");							
															break;
											case "WIFE" :	family.wifeId = inputElements[2].replace("@", "");
															break;
											case "CHIL" :	family.childIds.add(inputElements[2].replace("@", ""));							
															break;			
											case "BIRT" : 	member.birthDate = convertStringToDate(inputFileData.nextLine());
															break;
											case "DEAT" : 	member.deathDate = convertStringToDate(inputFileData.nextLine());
															member.isDeadBln = true;
															break;											
											case "MARR" : 	family.marriageDate = convertStringToDate(inputFileData.nextLine());
															break;
											case "DIV" : 	family.divorceDate = convertStringToDate(inputFileData.nextLine());
															family.isDivorceBln = true;
															break;
										}
									}
									break;
					
						}
		       }
		       
		       inputFileData.close();
		       
		       checkUS01(); 	//Dates before current date
		       
		       checkUS33(); 	//List orphans
		       
		       outs.close();
			}
			catch(FileNotFoundException e) {
				e.printStackTrace();
			}
			
		}//end of main 
			
		static void checkUS01() {
			
			for(String itr: individualIdList) {
		    	 Individual i = individualMap.get(itr);		    	   
		    	 if(i.birthDate != null && i.birthDate.after(new Date())) {
				     System.out.println("Error US01: Birth date of "+i.name+" (" + i.indi +") occurs before current date.");
				     System.out.println();
		    	 }
			     if(i.deathDate != null && i.deathDate.after(new Date())) {
				     System.out.println("Error US01: Death date of "+i.name+" (" + i.indi +") occurs before current date.");
				     System.out.println();
		    	 }
			}
			
			for(String itr: familyList) {
		    	 Family f = familyMap.get(itr);		    	   
		    	 if(f.marriageDate != null && f.marriageDate.after(new Date()))	{	    		   
				     System.out.println("Error US01: Marriage date of "+individualMap.get(f.husbId).name+" (" + f.husbId +") and " + individualMap.get(f.wifeId).name+" (" + f.wifeId +") occurs before current date.");
				     System.out.println();
		    	 }
			     if(f.divorceDate != null && f.divorceDate.after(new Date())) {
				     System.out.println("Error US01: Divorce date of "+individualMap.get(f.husbId).name+" (" + f.husbId +") and "+ individualMap.get(f.wifeId).name+" (" + f.wifeId +") occurs before current date.");
				     System.out.println();
		    	 }
			}
		}
		
		static void checkUS33() {
			
			for(String itr: individualIdList) {
		    	 Individual i = individualMap.get(itr);	
		    	 Family f = familyMap.get(i.famc);
		    	 if(getAge(i.birthDate) < 18 && f != null && individualMap.get(f.husbId).isDeadBln == true && individualMap.get(f.wifeId).isDeadBln == true ) {
		    		 System.out.println("List US33: "+i.name+" (" + i.indi +") is orphan in family " + i.famc+ ".");
				     System.out.println();
		    	 }
			}
		}

		static Date convertStringToDate(String nextLineString) {
			
			Date date = null;
			String splitString[] = nextLineString.split(" ");
			
			if(splitString[0].equals("2") && splitString.length > 1 && splitString[1].equals("DATE")) {
				String dateString = splitString[2] + splitString[3] + splitString[4];
				
			    DateFormat df = new SimpleDateFormat("ddMMMyyyy");
			    try {
			        date = df.parse(dateString);
			    }
			    catch ( Exception ex ){
			        System.out.println(ex);
			    }				
			}
			return date;
		}		
		
		static int getAge(Date birthDate) {
			
			int age = 0;
			Calendar birthDateCal = Calendar.getInstance();
			birthDateCal.setTime(birthDate);
			Calendar todayCal = Calendar.getInstance();
			age = todayCal.get(Calendar.YEAR) - birthDateCal.get(Calendar.YEAR);
			if (todayCal.get(Calendar.DAY_OF_YEAR) < birthDateCal.get(Calendar.DAY_OF_YEAR))
				age--;
			return age;
		}
	}
