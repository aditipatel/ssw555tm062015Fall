
package com.gedcom;

import java.io.File;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Year;
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

	public Family(String famId, String husbId, String wifeId, ArrayList<String> childIds) {
		this.famId = famId;
		this.husbId = husbId;
		this.wifeId = wifeId;
		this.childIds = childIds;
	}

}

public class GedcomProcessor {

	public static void main(String[] args){

		String indi = "";
		String name = "";
		String sex = "";
		String famc = "";
		String fams = "";
		Date birthday= new Date();
		String bday="";

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

			while(inputFileData.hasNextLine()) {				
				String line = inputFileData.nextLine();
				String inputElements[] = line.split(" "); 
				if(validTags.contains(inputElements[1])) {
					switch(inputElements[1]) {
					case "INDI" :
						if(indiHasDataBool == true) {
							Individual  member = new Individual(indi, name, sex, fams, famc, birthday);

							individualMap.put(indi, member);
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
						//dataSetBool = true;
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
							bday=inputElements[2]+" "+inputElements[3]+" "+inputElements[4];
							SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy");
							try {
								birthday = formatter.parse(bday);
							} catch (ParseException e) {
								e.printStackTrace();
							}
							rootTag="";
						}
						break;
					case "FAM" :
						if(familyHasDataBool == true) {
							com.gedcom.Family  family = new Family(famId, husbId, wifeId, childIds);
							familyMap.put(famId, family);
							familyList.add(famId);

							famId = "";
							husbId = "";
							wifeId = "";
							childIds = new ArrayList<String>();
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
					}					
				}
				else {
				}					
			}
			for(String itr: individualIdList){
				Individual i = individualMap.get(itr);
				LocalDate today = LocalDate.now();
				LocalDate ibday =  i.birthday.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
				long years = ChronoUnit.YEARS.between(ibday, today);
				System.out.println("Individual ID : " + itr + "\n"+"Name : " + i.name+"\n"+"Age "+years);
				System.out.println();
			}

			for(String itr: familyList){
				Family f = familyMap.get(itr);
				System.out.println("Family Id : "+ itr +"\n"+"Husband Id : " + f.husbId +"\n"+ "Husband's name " + individualMap.get(f.husbId).name +
						"\n"+"Wifes Id : " + f.wifeId +"\n"+"Wifes Name : "+individualMap.get(f.wifeId).name);
				System.out.println();
			}

			inputFileData.close();
		}
		catch(FileNotFoundException e) {
			e.printStackTrace();
		}		
	}//end of main 
}
