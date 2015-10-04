
package com.gedcom;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;


class Individual {
	String indi = "";
	String name = "";
	String sex = "";
	String famc = "";
	String fams = "";
	Date birthday;

	// birthdate and death date will add later
	//constructor for Individual class
	public Individual(String indi,String name,String sex, String famc, String fams,Date birthday)
	{
		this.indi = indi;
		this.name = name;
		this.sex = sex;
		this.famc = famc;
		this.fams = fams;
		this.birthday=birthday;
	}
}

class Family 
{
	String famId = "";
	String husbId = "";
	String wifeId = "";
	ArrayList<String> childIds = new ArrayList<String>();
	Date marriageDate;
	Date divorceDate;

	public Family(String famId, String husbId, String wifeId, ArrayList<String> childIds, Date marriagedate,Date divorcedate) {
		this.famId = famId;
		this.husbId = husbId;
		this.wifeId = wifeId;
		this.childIds = childIds;
		this.marriageDate=marriagedate;
		this.divorceDate=divorcedate;
	}

}

public class GedcomProcessor {

	public static long calcAge(Date from, LocalDate to)
	{
		LocalDate today = to;
		LocalDate ibday =  from.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		long years = ChronoUnit.YEARS.between(ibday, today);
		return years;
	}
	
	public static void checkMarriageAge(Family f,Map < String, Individual >individualMap)
	{
		Date husbandbday = individualMap.get(f.husbId).birthday;
		Date wifebday = individualMap.get(f.wifeId).birthday;
		long husbMarrAge = calcAge(husbandbday,f.marriageDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
		long wifeMarrAge = calcAge(wifebday,f.marriageDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
		
		if(husbMarrAge < 14|| wifeMarrAge < 14)
		{
			System.out.println("Error: Husband or wife's Age is less than 14 at the time of marriage for familyid:" + f.famId);
		}
	}
	public static void main(String[] args){

		String indi = "";
		String name = "";
		String sex = "";
		String famc = "";
		String fams = "";
		Date birthday= new Date();
		Date marriagedate = new Date();
		Date divorcedate= new Date();
		String date="";

		String famId = "";
		String husbId = "";
		String wifeId = "";
		ArrayList<String> childIds = new ArrayList<String>();

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

		Boolean indiHasDataBool = false;
		Boolean familyHasDataBool = false;
		String rootTag="";

		Map < String, Individual > individualMap = new HashMap <> (); 
		Map < String, Family > familyMap = new HashMap <> (); 
		ArrayList<String> individualIdList = new ArrayList<>();
		ArrayList<String> familyList = new ArrayList<>();

		try {
			FileReader file = new FileReader(new File("GedComInput.ged"));
			Scanner inputFileData = new Scanner(file);
			Individual  individual = null;
			Family  family = null;

			while(inputFileData.hasNextLine()) {				
				String line = inputFileData.nextLine();
				String inputElements[] = line.split(" "); 
				if(validTags.contains(inputElements[1])) {
					switch(inputElements[1]) {
					case "INDI" :
						if(indiHasDataBool) {
							individual = new Individual(indi, name, sex, fams, famc, birthday);

							individualMap.put(indi, individual);
							individualIdList.add(indi);

							indi = "";
							name = "";
							sex = "";
							famc = "";
							fams = "";
							indiHasDataBool = false;
						}
						indi = inputElements[2];
						indiHasDataBool = true;
						break;
					case "NAME" :
						name = inputElements[2];
						break;
					case "SEX" :
						sex = inputElements[2];
						break;
					case "FAMS" :
						fams = inputElements[2];							
						break;
					case "FAMC" :
						famc = inputElements[2];							
						break;
					case "BIRT":
						rootTag="BIRT";
						break;
					case "DATE":
						if(rootTag=="BIRT")
						{
							date=inputElements[2]+" "+inputElements[3]+" "+inputElements[4];
							SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy");
							try {
								birthday = formatter.parse(date);
							} catch (ParseException e) {
								e.printStackTrace();
							}
							rootTag="";
						}
						if(rootTag=="MARR")
						{
							date=inputElements[2]+" "+inputElements[3]+" "+inputElements[4];
							SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy");
							try {
								marriagedate = formatter.parse(date);
							} catch (ParseException e) {
								e.printStackTrace();
							}
							rootTag="";	
						}
						if(rootTag=="DIV")
						{
							date=inputElements[2]+" "+inputElements[3]+" "+inputElements[4];
							SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy");
							try {
								divorcedate = formatter.parse(date);
							} catch (ParseException e) {
								e.printStackTrace();
							}
							rootTag="";
						}
						break;
					case "FAM" :
						if(familyHasDataBool) {
							family = new Family(famId, husbId, wifeId, childIds,marriagedate,divorcedate);
							familyMap.put(famId, family);
							familyList.add(famId);

							famId = "";
							husbId = "";
							wifeId = "";
							childIds = new ArrayList<String>();
							marriagedate = new Date();
							divorcedate = new Date();
							familyHasDataBool = false;
						}
						famId = inputElements[2];
						familyHasDataBool = true;
						break;
					case "HUSB" :
						husbId = inputElements[2];							
						break;
					case "WIFE" :
						wifeId = inputElements[2];
						break;
					case "CHIL" :
						childIds.add(inputElements[2]);							
						break;	
					case "MARR":
						rootTag="MARR";
						break;
					case "DIV":
						rootTag="DIV";
						break;
					}					
				}
								
			}
			
			if(null != indi && !"".equals(indi)){
				individual = new Individual(indi, name, sex, fams, famc, birthday);
				individualMap.put(indi, individual);
				individualIdList.add(indi);
			}
			
			if(null != famId && !"".equals(famId)){
				family = new Family(famId, husbId, wifeId, childIds,marriagedate,divorcedate);
				familyMap.put(famId, family);
				familyList.add(famId);
			}
			
			for(String itr: individualIdList){
				Individual i = individualMap.get(itr);
				long years = calcAge(i.birthday,LocalDate.now());
				System.out.println("Individual ID : " + itr + "\n"+"Name : " + i.name+"\n"+"Age "+years);
				System.out.println();
			}

			for(String itr: familyList){
				Family f = familyMap.get(itr);
				System.out.println("Family Id : "+ itr +"\n"+"Husband Id : " + f.husbId +"\n"+ "Husband's name " + individualMap.get(f.husbId).name +
						"\n"+"Wifes Id : " + f.wifeId +"\n"+"Wifes Name : "+individualMap.get(f.wifeId).name);
				checkMarriageAge(f,individualMap);
				System.out.println();
			}

			inputFileData.close();
		}
		catch(FileNotFoundException e) {
			e.printStackTrace();
		}		
	}//end of main 
}
