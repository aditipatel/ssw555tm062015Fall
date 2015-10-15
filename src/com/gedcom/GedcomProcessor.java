package com.gedcom;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import com.gedcom.Family;
import com.gedcom.Individual;

	class Individual {
	String indi = "";
	String name = "";
	String sex = "";
	ArrayList<String> famc = new ArrayList<String>();
	ArrayList<String> fams = new ArrayList<String>();
	Date birthDate = null;
	Date deathDate = null;
	Boolean isDeadBln = false;

	// constructor for Individual class

	public Individual() {

	}

	public Individual(String indi, String name, String sex, ArrayList<String> famc, ArrayList<String> fams,
			Date birthDate, Date deathDate, Boolean isDeadBln) {
		this.indi = indi;
		this.name = name;
		this.sex = sex;
		this.famc = famc;
		this.fams = fams;
		this.birthDate = birthDate;
		this.deathDate = deathDate;
		this.isDeadBln = isDeadBln;
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
				
				//FileReader file = new FileReader("Test3.ged");
				
				// This code is for command line input. 
				  FileReader file = new FileReader(new File(args[0]));
				if (args.length==0) {
				     System.out.println("Error: Bad command or filename. ");
				     System.exit(0);
				}
				
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
											case "FAMS":
													member.fams.add(inputElements[2].replace("@", ""));
															break;
											case "FAMC":
													member.famc.add(inputElements[2].replace("@", ""));
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
		       
		       checkUS27();     //List Individual Ages
		       
		       checkUS10();     //Check if marriage age is less than 14
		       
		       checkUS02(); // check if marriage date is before birth date

		       checkUS03(); //check if death date is before birth date
			
		       checkUS11(); // check if bigamy exists
		       
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

		static void checkUS27()
		{
			for(String itr: individualIdList) {
		    	 Individual i = individualMap.get(itr);	
		    	 Family f = familyMap.get(i.famc);
		    	 long years = getAge(i.birthDate,LocalDate.now());
				 System.out.println("Individual ID : " + itr + "\n"+"Name : " + i.name+"\n"+"Age "+years);
				 System.out.println(); 
			}
		}
		
		static void checkUS10()
		{
			for(String itr: familyList){
				Family f = familyMap.get(itr);
				if(f.marriageDate!=null)
				{
					checkMarriageAge(f,individualMap);
				}
				System.out.println();
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
		public static long getAge(Date from, LocalDate to)
		{
			LocalDate today = to;
			LocalDate ibday =  from.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			long years = ChronoUnit.YEARS.between(ibday, today);
			return years;
		}
		
		public static void checkMarriageAge(Family f,Map < String, Individual >individualMap)
		{
			Date husbandbday = individualMap.get(f.husbId).birthDate;
			Date wifebday = individualMap.get(f.wifeId).birthDate;
			long husbMarrAge = getAge(husbandbday,f.marriageDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
			long wifeMarrAge = getAge(wifebday,f.marriageDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
			
			if(husbMarrAge < 14|| wifeMarrAge < 14)
			{
				System.out.println("Error US10: Husband or wife's Age is less than 14 at the time of marriage for familyid:" + f.famId);
			}
		}
		
			/**
	 * This method checks if there is a invalid marriage date ie marriage date
	 * comes before birth date of an individual. It prints the name of the
	 * individuals who have invalid marriage dates.
	 */
	static void checkUS02() {
		for (String itr : individualIdList) {
			Individual i = individualMap.get(itr);
			Family f = familyMap.get(i.fams);

			if ((i.fams != null) && (!(i.fams.isEmpty()))
					&& (i.birthDate != null) && (f.marriageDate != null))
				if (i.birthDate.after(f.marriageDate)) {
					System.out.println("Error US02:Marriage date of " + i.name+"("+i.indi+")"
							+ " is before the birth date");
					System.out.println();
				}
		}
	} // end of checkUS02

	/**
	 * This method verifies that birth of an individual occurs before death of
	 * an individual
	 */

	static void checkUS03() {
		for (String itr : individualIdList) {
			Individual i = individualMap.get(itr);

			if ((i.birthDate != null) && (i.deathDate != null)) {
				if (i.deathDate.before(i.birthDate)) {
					System.out.println("Error US03:Death date of " + i.name +"("+i.indi+")"
							+ " occurs before the birth date");
				}
			}

		}// end of for loop

	}
	static void checkUS11() {
		for (String itr : individualIdList) {
			Individual i = individualMap.get(itr);
			int activemarriage = 0;
			int index = 0;
			if ((i.fams.size() > 1)) {
				do {
					Family f = familyMap.get(i.fams.get(index++));
					if (f.isDivorceBln == false) {
						++activemarriage;
					}
					if (activemarriage > 1) {
						System.out.println("Error US11: Bigamy detected for " + i.name + "(" + i.indi + ")");
						break;
					}
				}while (index<i.fams.size());
			}
		}
	}
}
