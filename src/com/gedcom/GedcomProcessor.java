



import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;


	class Individual {
	String indi = "";
	String name = "";
	String sex = "";
	ArrayList<String> famc = new ArrayList<String>();
	ArrayList<String> fams = new ArrayList<String>();
	Date birthDate = null;
	Date deathDate = null;
	Boolean isDeadBln = false;
	String firstName="";
	String lastName="";

	// constructor for Individual class

	public Individual() {

	}

	public Individual(String indi, String name, String sex, ArrayList<String> famc, ArrayList<String> fams,
			Date birthDate, Date deathDate, Boolean isDeadBln,String firstName,String lastName) {
		this.indi = indi;
		this.name = name;
		this.sex = sex;
		this.famc = famc;
		this.fams = fams;
		this.birthDate = birthDate;
		this.deathDate = deathDate;
		this.isDeadBln = isDeadBln;
		this.firstName = firstName;
		this.lastName = lastName;
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
															member.firstName = inputElements[2];
															member.lastName =  inputElements[3].replace("/", "");
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
		       
		       checkUS21(); // check Correct gender for role
		       
		       checkUS28(); // List siblings in families by age

		       
		       checkUS08(); //Check if birth is before marriage or death in the family.
		       
		       checkUS06(); //checks if the divorce date is before the death date of both husband and wife
		       
		       checkUS16();//All male members of a family should have the same last name
		       
		       checkUS30();//This method prints all the individuals who are living married.
		       
		       checkUS29();//This method lists all the individuals who are deceased.
		       
		       checkUS19(); //Check if there are any marriages to first cousins
		       
		       checkUS17(); //Check for marriage between decendants
		       
		       checkUS15(); // Check for Fewer than 15 siblings
		       
		       checkUS31();  // List living single
		       
		       checkUS36(); //List recent deaths
		       
		       checkUS25(); //Unique first names in families
		       
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
		    		 System.out.println("List US33: "+i.name+"(" + i.indi +") is orphan in family " + i.famc+ ".");
				     System.out.println();
		    	 }
			}
		}

		static void checkUS27()
		{
			for(String itr: individualIdList) {
		    	 Individual i = individualMap.get(itr);
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
		
		static void checkUS21()
		{
			System.out.println();
			for(String itr: familyList) {			
		    	 Family f = familyMap.get(itr);	
		    	 if(f.husbId != null && !(individualMap.get(f.husbId).sex).equals("M"))	{	    		   
				     System.out.println("Error US21: Husband "+individualMap.get(f.husbId).name+"(" + f.husbId +") in family"+"(" + f.famId +") should be male." );	    
				     System.out.println();
		    	 }
		    	 if(f.wifeId != null && !(individualMap.get(f.wifeId).sex).equals("F"))	{	    
			    	 System.out.println("Error US21: Wife "+individualMap.get(f.wifeId).name+"(" + f.wifeId +") in family"+"(" + f.famId +") should be female." );
			    	 System.out.println();
		    	 }
		    	
			}
		}
		
		static void checkUS28()
		{
			System.out.println("US28: List siblings in families by age" );
			System.out.println();
			for(String itr: familyList) {
				 Family f = familyMap.get(itr);					    
		    	 if(f.childIds != null && !f.childIds.isEmpty())	{
		    		System.out.println("	List siblings in family"+"(" + f.famId +") by age" );	
					for (int j = 0; j < f.childIds.size() - 1; j++) {
				        for (int k = j + 1; k < f.childIds.size(); k++) {
				            if (getAge(individualMap.get(f.childIds.get(j)).birthDate) > getAge(individualMap.get(f.childIds.get(k)).birthDate)) {
				                String temp = f.childIds.get(k);
				                f.childIds.set(k, f.childIds.get(j));
				                f.childIds.set(j, temp);
				            }
				        }
				    }
					
					for (int j = 0; j < f.childIds.size(); j++) {
						 Individual i = individualMap.get(f.childIds.get(j));
						 System.out.println("		"+(j+1)+ ".  "+i.name+"(" + i.indi +") and "+ (i.sex.equals("M")? "his" :"her" )+" age is "+getAge(i.birthDate));	    
					}
					 System.out.println();
		    	 }
		    	 else {
		    		 System.out.println("	No siblings in Family"+"(" + f.famId +")" );		
		    	 }
		    	 f = null;
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
			if (i.fams != null && !i.fams.isEmpty()){
				
				for(String spouse : i.fams){
					Family f = familyMap.get(spouse);

					if ((i.birthDate != null) && (f.marriageDate != null)){
				if (i.birthDate.after(f.marriageDate)) {
					System.out.println("Error US02:Marriage date of " + i.name+"("+i.indi+")"
							+ " is before the birth date");
					System.out.println();
						}//end of if
					}//end of null check if
				}//end of fams for loop
				
			}//end of fams and famc null check
		}//end of for loop
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
					System.out.println();
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
	
	static void checkUS08() {

		for (String fam : familyList) {
			Family f = familyMap.get(fam);
			if (f.isDivorceBln == false) {
				for (String child : f.childIds) {
					Individual i = individualMap.get(child);
					if (f.marriageDate.after(i.birthDate)) {
						System.out.println("Error US08: Birthdate of " + i.name + "(" + i.indi
								+ ") is before marriage date of the family "+"("+f.famId+")");
						break;
					}

				}
			} else {
				for (String child : f.childIds) {
					Individual i = individualMap.get(child);
					if (f.divorceDate.after(i.birthDate)) {
						System.out.println("Error US08: Birthdate of " + i.name + "(" + i.indi
								+ ") is before divorce date of the family "+"("+f.famId+")");
						break;
					}

				}
			}
		}

	}
	
	/**
	 * This method checks if the divorce date is before the death date of both the partners
	 */
	 static void checkUS06(){
		 for(String itr:individualIdList){
			 Individual i = individualMap.get(itr);
			 if((i.fams != null) &&(!(i.fams.isEmpty()))){
				 for(String spouse : i.fams){
					 Family f = familyMap.get(spouse);
					 if((i.isDeadBln) && (f.isDivorceBln)){
						 if((f.divorceDate).after(i.deathDate)){
							 System.out.println("Error US06:Divorce date of "+ i.name + "("+f.famId+")"
									 + " occurs after the death date");
							 						 System.out.println();
						 }
					 }
				 }
				 
			 }
		 }
	 }
	 
	 /**
	  * This method checks if all male names have the same lastname in a family
	  */
	 static void checkUS16(){
		 
		 for(String itr:familyList){
			 Family f = familyMap.get(itr);
			  Individual husband = individualMap.get(f.husbId);
			  	List<String> childId = f.childIds;
			  		for(String ch:childId){
			  			Individual child = individualMap.get(ch);
			  			if((child.sex).equals("M")){
			  			if(!(husband.lastName.equals(child.lastName))){
			  				System.out.println("Error US16 : The family where all male members don't have the "
			  						+ " same last name is Family id : "+f.famId );
			  				System.out.println("The name of the individual"
			  						+" is " + child.firstName + " " +child.lastName+"\r\n");
			  			}
			 
			  			}
			  				 
				}
			  	
		 }
	}
	 
	 /**
	  * This method prints all the individuals who are living married.
	  */
	 static void checkUS30(){
		 System.out.println("checkUS30 : List of the individuals who are married and alive"+"\r\n");
		 ArrayList<String> marriedAliveIdList = new ArrayList<>();
		 for(String itr:individualIdList){
			 Individual i = individualMap.get(itr);
			 if(!(i.isDeadBln)){
				if((i.fams != null) &&(!(i.fams.isEmpty()))){
					for(String famId : i.fams){
						Family f = familyMap.get(famId);
						
						if(!(f.isDivorceBln) && (!(marriedAliveIdList.contains(itr)))){
							
							marriedAliveIdList.add(itr);
							System.out.println( "Individual id: "+i.indi +" " + " Individual name: "+i.name);
							System.out.println();
						}					
					}
				}
			 }
		 }		 
	 }
	 
	 /**
	  * This method lists all the individuals who are deceased.
	  */
	 static void checkUS29(){
		 System.out.println("Check US29 : The list of the individuals who are deceased:"+"\r\n");
		 for(String itr:individualIdList){
			 Individual i = individualMap.get(itr);
			 if(i.isDeadBln){
				 System.out.println(i.name);
			 }
		 }
	 }
	 static void checkUS19() {
		for (String fam : familyList) {
			Family f = familyMap.get(fam);
			if (isCousin(f.husbId, f.wifeId) == true) {
				System.out.println("Error US19: Marriage between first cousins detected for individuals "+f.husbId+
						" and "+ f.wifeId);
			}
		}
	}

	static boolean isCousin(String ind1, String ind2){
		ArrayList<String> list = new ArrayList<String>();
		Individual i1 = individualMap.get(ind1);
		Individual i2 = individualMap.get(ind2);
		if(i1.famc.size()==0 || i2.famc.size()==0){
			return false;
		}
		String fam1 = i1.famc.get(0);
		String fam2 = i2.famc.get(0);
		Family f1 = familyMap.get(fam1);
		Family f2 = familyMap.get(fam2);
		Individual h1 = individualMap.get(f1.husbId);
		Individual h2 = individualMap.get(f2.husbId);
		Individual w1 = individualMap.get(f1.wifeId);
		Individual w2 = individualMap.get(f2.wifeId);
		if(h1.famc.size()!=0){
			list.add(h1.famc.get(0));
		}
		if(h2.famc.size()!=0){
			list.add(h2.famc.get(0));
		}
		if(w1.famc.size()!=0){
			list.add(w1.famc.get(0));
		}
		if(w2.famc.size()!=0){
			list.add(w2.famc.get(0));
		}
		Set inputSet = new HashSet(list);
        if(inputSet.size()< list.size()){
            return true;
        }
        return false;
	}
	
	
	public static void checkUS17()
	{
		for (String fam : familyList) {
			Family f = familyMap.get(fam);
			ArrayList<String> decendants = new ArrayList<String>();
			findDecendents(f.husbId,decendants);
			if(decendants.contains(f.wifeId))
			{
				System.out.println("Error US17: Marriage to decendents detected for individuals " + f.husbId
						+ " and " + f.wifeId);
			}
			findDecendents(f.wifeId,decendants);
			if(decendants.contains(f.husbId))
			{
				System.out.println("Error US17: Marriage to decendents detected for individuals " + f.husbId
						+ " and " + f.wifeId);
			}
		}
	}
	
	public static void findDecendents(String ind1,ArrayList<String> decendents) {
		Individual i1 = individualMap.get(ind1);
		for (int i = 0; i < i1.fams.size(); i++) {
			String f1 = i1.fams.get(i);
			Family f = familyMap.get(f1);
			if (f.childIds.size() == 0) {

			} else {
				for (int j = 0; j < f.childIds.size(); j++) {
					String child = f.childIds.get(j);
					decendents.add(child);
					findDecendents(child,decendents);
				}
			}
		}
	}
	
	public static void checkUS15() {
		for (String fam : familyList) {
			Family f = familyMap.get(fam);
			if (f.childIds.size() >= 15) {
				System.out.println("Error US15: Family ("+f.famId+") should have fewer than 15 siblings.");
			}
		}
	}
	
	public static void checkUS31() {
		 System.out.println("checkUS31 : List of the individuals who are single and alive");
		 int cnt =0;
		for(String itr:individualIdList){
			 Individual i = individualMap.get(itr);
			 if(!(i.isDeadBln) && getAge(i.birthDate) >= 18){
				if((i.fams == null)  || (i.fams.isEmpty())){
					System.out.println("	"+ ++cnt + ".  "+ i.name +" (" + i.indi +")");
				}
				else {
					boolean isSingle = false;
					for(int j = 0; j < i.fams.size(); j++){
						 Family f = familyMap.get( i.fams.get(j));
						 String spouseId = (i.sex.equals("M") ? f.wifeId : f.husbId );
						
						 if((individualMap.get(spouseId).isDeadBln)|| (f.isDivorceBln)){
							 isSingle = true;
						 }
						 else {
							 isSingle = false;
							 break;
						 }
					}
					if(isSingle == true) {
						 System.out.println("	"+ ++cnt + ".  "+ i.name +" (" + i.indi +")");
					 }
							
				}
			 }
		 }		 
	}
	public static void checkUS36() {
		for (String itr : individualIdList) {
			Individual i = individualMap.get(itr);
			if (i.deathDate != null) {
				LocalDate today = LocalDate.now();
				LocalDate idday = i.deathDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
				long days = ChronoUnit.DAYS.between(idday, today);
				if (days < 30 && days >= 0) {
					System.out.println("\nInfo US36: Recent death detected Individual ID : " + itr + "\n" + "Name : "
							+ i.name + "\n" + "Days Since Death " + days);
					System.out.println();
				}

			}
		}
	}

	public static void checkUS25() {
		for (String fam : familyList) {
			Family f = familyMap.get(fam);
			for (int i = 0; i < f.childIds.size(); i++)
				for (int j = i + 1; j < f.childIds.size(); j++) {
					{
						Individual c1 = individualMap.get(f.childIds.get(i));
						Individual c2 = individualMap.get(f.childIds.get(j));
						if (c1.name.equalsIgnoreCase(c2.name)) {
							if (c1.birthDate.compareTo(c2.birthDate) == 0) {
								System.out.println(
										"\nERROR US25: Individuals with same name and birthdate detected for Family:"
												+ f.famId + " individual ids " + c1.indi + " and " + c2.indi);
								System.out.println();
							}
						}
					}
				}
		}
	}
}
