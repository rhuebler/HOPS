package CommandLineReader;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Scanner;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import Utility.AMPS_Mode;

/**
 * This class is used To Parse Input Parameters for RMA Extractor to make input more flexible and less error prone
 * uses the console parameters as input and fill up all Parameter slots to control subsequent functions
 * @author huebler
 *
 */
public class InputParameterProcessor {
	/**
	 * @param String[] args all commandline parameters
	 * @throws none thrown all caught
	 * @return return values and parameters to run and control the program
	 */
	// initialize class with standard values;	
	private ArrayList<String> fileNames = new ArrayList<String>();
	private String outDir;
	private String configFile;
	private AMPS_Mode ampsMode =AMPS_Mode.ALL;
	
	// constructor
	public InputParameterProcessor(String[] params) throws IOException, ParseException{
		process(params);	
	}
	
	// read all files a file containing input filesnames and check if they match the correct extension
	private void readFileList(File f, String fileExtension) throws IOException{
		try {
			 Scanner	in = new Scanner(f.getCanonicalFile());
			 while(in.hasNext()){
				String line = in.nextLine();
				 if(line.endsWith(fileExtension))
					 fileNames.add(in.nextLine());
			 }
			 in.close();
		 	}catch (FileNotFoundException e) {
		
		}		
	}	
	// getters for parameters
	public AMPS_Mode getAMPS_Mode(){
		return ampsMode;
	}
	public ArrayList<String> getInputFiles(){
		return this.fileNames;
	}
	public String getConfigFile(){
		return this.configFile;
	}
	public String getOutDir(){
		return this.outDir;
	}
	public String getAllOptions(){
		String input = "All Parameters:\n -i";
		for(String d: fileNames)
			input+= " "+d;
		input += "\n-o "+outDir +"\n--configFile "+configFile+"\n-m "+ampsMode;
		return input;
	}
	private void process(String[] parameters) throws IOException, ParseException{	
    	 CommandLine commandLine;
    	 	// Short Flags Are necessary parameters that are necessary for any run
    	 	// here we describe all CLI options
    	    Option option_Input = Option.builder("i").longOpt("input").argName("String").hasArgs().desc("Specify input directory or files valid option depend on mode").build();
    	    Option option_Output = Option.builder("o").longOpt("output").argName("String").hasArg().desc("Specify out directory").build();
    	    Option optionConfigFile = Option.builder("c").longOpt("configFile").argName("String").hasArg().desc("Path to Config File").build();
    	    Option optionMode = Option.builder("m").longOpt("mode").argName("String").hasArg().desc("AMPS Mode to run accpeted full, malt, maltex, post").build();
    	    Option option_Help = Option.builder("h").longOpt("help").optionalArg(true).desc("Print Help").build();
    	    Options options = new Options();
    	    
    	    // add all parameters to the parser
    	    CommandLineParser parser = new DefaultParser();

    	    options.addOption(option_Input);
    	    options.addOption(option_Output);
    	    options.addOption(optionMode);
    	    options.addOption(optionConfigFile);
    	    options.addOption(option_Help);

    	    //parse arguments into the comandline parser
    	        commandLine = parser.parse(options, parameters);
 
    	        //check if mode is set and if allowed values are set
    	        if(commandLine.hasOption('m')){
	    	        	String m = commandLine.getOptionValue("m");
	    	        	if(m.equals("full")){
	    	        		ampsMode =AMPS_Mode.ALL;
	    	        	}else if(m.equals("malt")){
	    	        		ampsMode =AMPS_Mode.MALT;
	    	        	}else if(m.equals("maltex")){
	    	        		ampsMode =AMPS_Mode.MALTEX;
	    	        	}else if(m.equals("post")){
	    	        		ampsMode =AMPS_Mode.POST;
	    	        	}else{
	    	        		System.err.println("Unspecified Mode use either full, malt, maltex or post");
	    	        		System.exit(1);
	    	        	}
    	        }else {
    	        		ampsMode =AMPS_Mode.ALL;
    	        }
    	        if (commandLine.hasOption("input"))//evaluate input files and directory
    	        {
    	            for(String arg :commandLine.getOptionValues("input")){
    	            		File inFile = null;
    	            		if(new File(arg).getParent()!=null){//get Path of inFile either by getting the canonical Path from the parent
    	            			inFile = new File(new File(arg).getParentFile().getCanonicalPath()+"/"+ new File(arg).getName());
    	            		}else {// or by patching together the Path
    	            			inFile = new File(System.getProperty("user.dir")+"/"+arg);
    	            		}
    	            		if(inFile.isDirectory()){ //TODO add file extension check for malt and all if the file is an directory
    	            			switch(ampsMode){ //if directory provided as input
    	            			case ALL:
    	            				for(String name : inFile.list())//if file ends with RMA6 or is as a soft link at to files
    	            					if(name.endsWith("fa")||name.endsWith("fq")||name.endsWith("fasta")||name.endsWith("fastq")||name.endsWith("gz"))
        	            				this.fileNames.add(inFile.getPath()+"/" + name);
    	            				break;
    	            			case MALT:
    	            				for(String name : inFile.list())//if file ends with RMA6 or is as a soft link at to files
    	            					if(name.endsWith("fa")||name.endsWith("fq")||name.endsWith("fasta")||name.endsWith("fastq")||name.endsWith("gz"))
    	            					this.fileNames.add(inFile.getPath()+"/" + name);
    	            				break;
    	            			case MALTEX:	
    	            				for(String name : inFile.list())//if file ends with RMA6 or is as a soft link at to files
        	            				if(name.endsWith("rma6") || Files.isSymbolicLink(new File(inFile.getPath()+"/" + name).toPath()))
        	            				this.fileNames.add(inFile.getPath()+"/" + name);
    	            				break;
    	            			case POST:
    	            				fileNames.add(inFile.getPath());
    	            				break;
								default:
									break;
    	            			}
    	            			
    	            		}else if(inFile.isFile()){// if file is provided as input
    	             			switch(ampsMode){
    	            			case ALL:
    	            				if(arg.endsWith("fa")||arg.endsWith("fq")||arg.endsWith("fasta")||arg.endsWith("fastq")||arg.endsWith("gz")){ 
    	            					fileNames.add(inFile.getPath());
    	            				}
    	            				break;
    	            			case MALT:
    	            				if(arg.endsWith("fa")||arg.endsWith("fq")||arg.endsWith("fasta")||arg.endsWith("fastq")||arg.endsWith("gz")){ 
    	            					fileNames.add(inFile.getPath());
    	            				}
    	            				break;
    	            			case MALTEX:
    	            				if(arg.endsWith("rma6")||Files.isSymbolicLink(new File(inFile.getPath()).toPath())){ 
    	            					fileNames.add(inFile.getPath());
    	            				}
    	            				break;
    	            			case POST:
    	            				fileNames.add(inFile.getPath());
    	            				break;
								default:
									break;
    	            			
    	            				
    	            			}
    	             		}else{ // read file names from text file
    	             			switch(ampsMode){
    	            			case ALL:
    	            				readFileList(inFile,"fa");
    	            				readFileList(inFile,"fg");
    	            				readFileList(inFile,"fasta");
    	            				readFileList(inFile,"fastq");
    	            				readFileList(inFile,"gz");
    	            				break;
    	            			case MALT:
    	            				readFileList(inFile,"fa");
    	            				readFileList(inFile,"fg");
    	            				readFileList(inFile,"fasta");
    	            				readFileList(inFile,"fastq");
    	            				readFileList(inFile,"gz");
    	            				break;
    	            			case MALTEX:
    	            				readFileList(inFile,"rma6");
    	            				break;
    	            			case POST:
    	            				System.err.println("File List not egilable for mode postprocessing");
    	            				break;
								default:
									break;
    	             			}	
    	             			
    	             		}
    	            }  
    	        }
    	        
    	        if (commandLine.hasOption("output"))//set output directorty
    	        {
    	        	
    	        	try{
    	        		String path = commandLine.getOptionValue("output");
    	        		if(path.contains("\n")||path.contains("$")||path.contains("\'")||path.contains("=")|| path.contains("\"")){ // check if path contains illegal characters
    	        			System.err.println("Illegal Character detected");
    	        			System.exit(1);
    	        		}
    	        		File f = new File(path); // get canonical path
    	        		f.isDirectory();
    	        		this.outDir = f.getCanonicalPath()+"/";
    	        		}catch(IOException io){
    	        			System.err.println(io);
    	        		}
    	        }
    	        if(commandLine.hasOption("configFile")){//set config File
    	        	String line = commandLine.getOptionValue("configFile");
    	        	File f = new File(line);
	    	        	if(f.isFile())
	    	        		configFile = line;
	    	        	else {
	    	        		System.err.println("Config File is not a valid file shutting down!!!");
	    	        		System.exit(1);
	    	        	}
    	        }
    	        if(commandLine.hasOption("h")){////help
    	        	String header = "AMPS version 0.1";
    	    	    String footer = "In case you encounter an error drop an email with an useful description to huebler@shh.mpg.de";
    	    	    HelpFormatter formatter = new HelpFormatter();
    	    	    formatter.setWidth(500);
    	    	    formatter.printHelp("AMPS", header, options, footer, true);   
    	    	    System.exit(0);
    	        }
    	        if(!commandLine.hasOption("o")||!commandLine.hasOption("i")) {
	    	        	System.err.println("input and output ave to be specified to run AMPS use -h for help \n Shutting down");
	    	        	System.exit(1);
    	        }
    	    }
    }
  