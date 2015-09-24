
package com.gedcom;

import java.io.File;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;


class Individual {
	String indi = "";
	String name = "";
	String sex = "";
	String famc = "";
	String fams = "";

	// birthdate and death date will add later
	//constructor for Individual class
	public Individual(String indi,String name,String sex, String famc, String fams)
	{
		this.indi = indi;
		this.name = name;
		this.sex = sex;
		this.famc = famc;
		this.fams = fams;
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

		Map < String, Individual > individualMap = new HashMap <> (); 
		Map < String, Family > familyMap = new HashMap <> (); 
		ArrayList<String> individualIdList = new ArrayList<>();
		ArrayList<String> familyList = new ArrayList<>();

		try {
			FileReader file = new FileReader(new File("Test2.ged"));
			Scanner inputFileData = new Scanner(file);

			while(inputFileData.hasNextLine()) {				
				String line = inputFileData.nextLine();
				String inputElements[] = line.split(" "); 
				if(validTags.contains(inputElements[1])) {
					switch(inputElements[1]) {
					case "INDI" :
						if(indiHasDataBool == true) {
							Individual  member = new Individual(indi, name, sex, fams, famc);

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

				System.out.println("Individual ID : " + itr + "\n"+"Name : " + i.name);
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
