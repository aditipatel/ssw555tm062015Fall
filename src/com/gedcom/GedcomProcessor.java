package com.gedcom;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * This program reads a GEDCOM file , prints each line , level number of each line
 * and prints tha tag of each line that has a valid tagfor our project or prints "invalid tag"
 * @author aditi patel
 * @date 09/16/2015
 *
 */
public class GedcomProcessor {

	public static void main(String[] args){

		JFileChooser chooser = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("GEDCOM files only" , "ged");
		chooser.setFileFilter(filter);
		int returnValue = chooser.showOpenDialog(chooser);

		if(returnValue == JFileChooser.APPROVE_OPTION){
			java.io.File selectedFile = chooser.getSelectedFile();

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

			Scanner input;

			try{
				input = new Scanner(selectedFile);
				while(input.hasNextLine()){
					String line = input.nextLine();
					String inputElements[] = line.split(" ");
					System.out.println(line); 
					System.out.println("Level number:"+ inputElements[0]);
					if(validTags.contains(inputElements[1])){
						System.out.println("Valid tag:" + inputElements[1]);
					}
					else
						System.out.println("Invalid tag");
					System.out.println( );
				}

			}

			catch(FileNotFoundException e){
				e.printStackTrace();
			}
		}
	}//end of main 
}//end of class file
