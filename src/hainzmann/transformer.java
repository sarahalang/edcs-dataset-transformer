package hainzmann;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.io.*;
import java.io.File;  // Import the File class
import java.io.IOException;  // Import the IOException class to handle errors

//------------------------------------------------------------------------------------------
public class transformer {
    public static void main(String[] args) throws IOException  {
        
    	// this program will only work if the source file containing EDCS datasets (Clauss-Slaby) is called "transformieren.txt"
    	// loading the file 
        final String FILE_PATH = "transformieren.txt";
        File file = new File(FILE_PATH);
        FileInputStream fileStream = new FileInputStream(file);
        byte[] byteArray = new byte[(int)file.length()];
        fileStream.read(byteArray);
        String data = new String(byteArray);
        
        // splitting paragraph delimiters irrespective of OS using \R
        // datasets is a String array containing one dataset per element
        String[] datasets = data.toString().split("\\R\\R\\R\\R+"); 
        String[] transformed = new String[datasets.length];
        
        for (int i = 0; i < datasets.length; i++) {
        	transformed[i] = makeReplacements(datasets[i]);
        }
        writeToFile(transformed);
        fileStream.close();
    }
  //--------------------------------------------------------------------------------------
    static String makeReplacements(String dataset) {
    	// split the dataset into its paragraphs 
    	String[] paragraphs = dataset.split("\\R\\R\\R"); 
    	String[] resulting_paragraphs = new String[3];
    	
    	// all paragraphs which have already been processed will be cleared / set to ""

    	// (not all datasets are structured exactly the same way!)
    	if (paragraphs[0].contains("Publikation") && !paragraphs[0].contains("EDCS")) {
        	String mergeParaOneAndTwo = paragraphs[0].substring(0, (paragraphs[0].length() - 1)) + paragraphs[1];
        	paragraphs[0] = "";
        	paragraphs[1] = mergeParaOneAndTwo;
    	}
    	
    	for (String paragraph : paragraphs) {
    		// delete unwanted paragraphs using their "headers"
        	if (paragraph.startsWith("Material") || paragraph.contains("Inschriftengattung") 
        		|| paragraph.contains("Personenstatus") || paragraph.startsWith("Kommentar")) {
        		paragraph = "";
        	}
        	// store HERKUNFT
        	if (paragraph.contains("Provinz") || paragraph.contains("Ort") ) {
        		resulting_paragraphs[1] = paragraph + "$";
        		paragraph = "";
        	}
        	
        	// store REFERENZ
        	if (paragraph.contains("Publikation")) {
        		resulting_paragraphs[0] = paragraph;
        		paragraph = "";
        	}
        	
        	// no headers means --> store TRANSKRIPTION
        	if (!paragraph.isEmpty()) {
        		resulting_paragraphs[2] = ":" + paragraph + ":";
        	}

    		System.out.println(paragraph + "\n-------------\n");
    	}
    	// print info for debugging purposes
    	for (int i = 0; i < 3; i++) {
    		System.out.println("Resulting ("+i+"): " + resulting_paragraphs[i] +"\n---\n");
    	}
    	// store result in variable
    	String updated_dataset = String.join(System.lineSeparator(), resulting_paragraphs);
    	
    	// set up list of patterns to match and their replacements to perform
    	ArrayList<String> patterns = new ArrayList<String>();
    	ArrayList<String> replacements = new ArrayList<String>();
    	
    	patterns.add("Publikation: ");
    	replacements.add("");
    	
    	patterns.add("Datierung: ");
    	replacements.add("=Dat* ");
    	
    	patterns.add("EDCS-ID: ");
    	replacements.add(" =");
    	
    	patterns.add("Provinz: ");
    	replacements.add("\\$1*");
    	
    	patterns.add("Ort: ");
    	replacements.add("\\$2*");
    	
    	patterns.add("[|][(](\\w+)[)]");
    	replacements.add("(($1))");

    	patterns.add("[<][a-z][=]([A-Z])[>](\\w+)"); //TODO make the letter in $1 to lowercase
    	replacements.add("$1$2(!)"); // \\l is to lowercase the next following char but only seems to work in some search+replace editors

    	patterns.add(" +"); // make multiple whitespace into just one 
    	replacements.add(" ");
    	
    	// perform replacements
        for(int i = 0; i < patterns.size(); i++)
        {
        	final Pattern COMPILED_PATTERN = Pattern.compile(patterns.get(i));
        	Matcher matcher = COMPILED_PATTERN.matcher(updated_dataset);
        	updated_dataset = matcher.replaceAll(replacements.get(i));
        }  
        
        System.out.println(updated_dataset);
        return updated_dataset;
    }
    //--------------------------------------------------------------------------------------
    static void writeToFile(String[] datasets) {
        try {
            FileWriter fileWriter = new FileWriter("resultat.txt");
            for (String dataset : datasets) {
            	fileWriter.write(dataset + System.lineSeparator() + System.lineSeparator() +  System.lineSeparator());
            }
            fileWriter.close();
            System.out.println("Successfully wrote to the file.");
            } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
            }
        }
    }
