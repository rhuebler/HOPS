package Utility;

import java.io.File;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import ConfigFileReader.Config;

public class ParameterProcessor {
	/**
	 * @author huebler
	 * @param Config Config;
	 * Is used to evaluate all parameters from the config file and generate a commandline to execute the different 
	 * parts of the pipeline
	 */
	//General Parameters
	private AMPS_Mode ampsMode;
	private ArrayList<String> input; 
	private String output;
	private Logger log;
	private boolean useSlurm = true;
	
	
	//MALT Parameters
	private int threadsMalt;
	private int maxMemoryMalt=500;
	private String partitionMalt;
	private ArrayList<String> MALTCommandLine;
	private String pathToMalt;
	private String index;
	private double id=85.00;
	private String mode="BlastN";
	private String alignmentType="SemiGlobal"; 
	private int	topMalt = 1;
	private int sup=1; 
	private int mq=100;
	private String memoryMode;
	private boolean verboseMalt;
	private ArrayList<String> additionalMALTParameters;
	
	//MaltExtract Parameters
	private int threadsMex;
	private int maxMemoryMex=500;
	private String partitionMex;
	private ArrayList<String> MALTExtractCommandLine;
	private String pathToMaltExtract;
	private String filter = "defAnc";
	private ArrayList<String> taxas = new ArrayList<String>();	
	private String resources;
	private double top=0.01; 
	private int maxLength=0;
	private double minPIdent=0.0;
	private boolean verboseME=false;
	private boolean	wantAlignment=false;
	private boolean wantReads=false;
	private double	minCompME=0.0;
	private boolean	wantMeganSummary=false;
	private boolean	destackingOff=false;
	private boolean dupRemOff=false;
	private boolean	downSampOff=false;
	private ArrayList<String> additionalMaltExtractParameters;
	
	//POSTPROCESSING Parameters
	private ArrayList<String> commandLinePost;
	private String pathToList;
	private String pathToPostProcessing;
	
	

	//Constructor
	public ParameterProcessor(String config, ArrayList<String> in, String out,Logger log, AMPS_Mode aMode){
		this.log = log;
		Config.readConfigFile(config);
		input = in;
		if(!out.endsWith("/"))
			out+="/";
		output = out;
		ampsMode = aMode;
	}
	public boolean useSlurm() {
		return useSlurm;
	}
	public String getPartitionMalt(){
		return partitionMalt;
	}
	public int getThreadsMalt() {
		return threadsMalt;
	}
	public int getMaxMemoryMalt() {
		return maxMemoryMalt;
	}
	
	public int getMaxMemoryMaltEx() {
		return maxMemoryMalt;
	}
	public String getPartitionMaltEx(){
		return partitionMalt;
	}
	public int getThreadsMaltEx() {
		return threadsMalt;
	}	

	public String getOutDir() {
		return output;
	}
	public ArrayList<String> getMALTCommandLine(){
		return MALTCommandLine;
	}
	public ArrayList<String> getMALTExtractCommandLine(){
		return MALTExtractCommandLine;
	}
	public ArrayList<String> getPostProcessingLine(){
		return commandLinePost;
	}
	public void process(){	
		switch(ampsMode){
		case ALL:
			
			 if(Config.entryExists("pathToMalt")){
				 pathToMalt=Config.getString("pathToMalt");
				 processMALTParameters();
				 generateMALTCommandLine(input, output+"malt");
				 if(!pathToMalt.endsWith("malt-run")){
					log.log(Level.SEVERE,"MALT not found");
				 	System.exit(1);
				 }
			 }else{
				 log.log(Level.SEVERE,"MALT not found");
				 System.exit(1);
			 }
		
			 if(Config.entryExists("pathToMaltExtract")){
				 pathToMaltExtract=pathToMalt=Config.getString("pathToMaltExtract");
				 processMALTExtractParameters();
				 generateMALTExtractCommandLine(output+"malt/", output+"maltExtract/");
			 }else{
				 log.log(Level.SEVERE,"MALTExtract not found");
				 System.exit(1);
			 }	 
			 if(Config.entryExists("pathToPostProcessing")){
				 pathToPostProcessing=Config.getString("pathToPostProcessin");
				 generatePostProcessingLine(output+"maltExtract/");
			 }else{
				 log.log(Level.SEVERE,"Postprocessing not found");
				 System.exit(1);
			 }	 	 
			 break;
		case MALT:	
			if(Config.entryExists("pathToMalt")){
				 pathToMalt=Config.getString("pathToMalt");
				 processMALTParameters();
				 generateMALTCommandLine(input, output+"malt");
				 if(!pathToMalt.endsWith("malt-run")){
					 log.log(Level.SEVERE,"malt-run script not found");
				 	System.exit(1);
				 }
			 }else{
				 log.log(Level.SEVERE,"MALT not found");
				 System.exit(1);
			 }
			 break;
		case MALTEX:	
			if(Config.entryExists("pathToMaltExtract")){
				 pathToMaltExtract=pathToMalt=Config.getString("pathToMaltExtract");
				 processMALTExtractParameters();
				 generateMALTExtractCommandLine(input, output+"maltExtract/");
			 }else{
				 log.log(Level.SEVERE,"MALTExtract not found");
				 System.exit(1);
			 }	 
			 break;
		case POST:	
			if(Config.entryExists("pathToPostProcessing")){
				 pathToPostProcessing=Config.getString("pathToPostProcessin");
				 generatePostProcessingLine(input);
			 }else{
				 log.log(Level.SEVERE,"Postprocessing not found");
				 System.exit(1);
			 }
			break;
		}	
		
	}
	private void processMALTParameters(){
		 if(Config.entryExists("index")){
			 index=Config.getString("index");
		 }else{
			 log.log(Level.SEVERE,"Missing MALT Index! Shutting down!");
			 System.exit(1);
		 }
		 if(Config.entryExists("id")){
			 id=Config.getDouble("id");
		 }
		 if(Config.entryExists("m")){
			mode=Config.getString("m");
		 }else{
			 log.log(Level.SEVERE,"MALT Mode unspecified! Shutting down!");
			 System.exit(1);
		 }
		 if(Config.entryExists("at")){
			 alignmentType=Config.getString("at");
		 }else{
			 log.log(Level.SEVERE,"Alignment Type unspecified! Shutting down!");
			 System.exit(1);
		 }	
		 if(Config.entryExists("topMalt")){
			 topMalt=Config.getInt("topMalt");
		 }
		 if(Config.entryExists("sup")){
			 sup=Config.getInt("sup");
		 }	
		 if(Config.entryExists("mq")){
			 mq=Config.getInt("mq");
		 }	
		 if(Config.entryExists("memoryMode")){
			 memoryMode=Config.getString("memoryMode");
		 }else{
			 log.log(Level.SEVERE,"Memory Mode unspecified! Shutting down!");
			 System.exit(1);
		 }	
		 if(Config.entryExists("verboseMalt")){
			 verboseMalt=Config.getBoolean("verboseMalt");
		 }	
		 if(Config.entryExists("additionalMaltParameters")){
				additionalMALTParameters = new ArrayList<String>();
				String s = Config.getString("additionalMaltParameters");
				if(s.contains(";")) {
					for(String p:s.split(";"))
						additionalMALTParameters.add(p);
				}else {
					additionalMALTParameters.add(s);
				}
			}
		 	
		 	if(Config.entryExists("threadsMalt")) {
		 		threadsMalt = Config.getInt("threadsMalt");
			}else {
				log.log(Level.SEVERE, "Using MALT with default Of 1 core highly discouraged use threads XX in config file");
				threadsMalt = 1;
			}
			if(Config.entryExists("maxMemoryMalt")){
				maxMemoryMalt = Config.getInt("maxMemoryMalt");
				log.log(Level.INFO, "Set maximum Memory for Malt to "+ maxMemoryMalt +" GB");
			}else {
				maxMemoryMalt = 300;
				log.log(Level.INFO, "Set Maximum Memorry for Malt to "+maxMemoryMalt +" GB");
			}
			if(Config.entryExists("partitionMalt")){
				partitionMalt = Config.getString("partitionMalt");
				log.log(Level.INFO, "Set Partition for Malt to "+partitionMalt);
			}else {
				partitionMalt = "batch";
				log.log(Level.INFO, "Set Partition for Malt to "+partitionMalt);
			}
	}
	private void generatePostProcessingLine(String inputLine){//TODO rework
		ArrayList<String> line = new ArrayList<String>();
		line.add(pathToPostProcessing);
		line.add(inputLine);
		line.add(pathToList);
		commandLinePost = line;
	}
	private void generatePostProcessingLine(ArrayList<String> input){//TODO rework
		ArrayList<String> line = new ArrayList<String>();
		String inputLine = input.get(0);
		line.add(pathToPostProcessing);
		line.add(inputLine);
		line.add(pathToList);
		commandLinePost = line;
	}
	private void generateMALTCommandLine(ArrayList<String> inputME, String outputME){
		ArrayList<String> maltLine= new ArrayList<String>();
		maltLine.add(pathToMalt);//malt location
		
		maltLine.add("-J-Xmx"+maxMemoryMalt+"G");
		maltLine.add("-d");				maltLine.add(index);//index
		maltLine.add("-i"); 				maltLine.addAll(inputME);//inputfiles
		maltLine.add("-o");			 	maltLine.add(outputME);//output
		maltLine.add("-m"); 				maltLine.add(mode);//maltMode
		maltLine.add("-at");				maltLine.add(alignmentType);//AlignmentType
		maltLine.add("--memoryMode");	maltLine.add(memoryMode);//memoryMode
		maltLine.add("-t");				maltLine.add(""+threadsMalt);//number of threads
		maltLine.add("-sup");			maltLine.add(""+sup);//minimum supportedNumber
		maltLine.add("-mq") ;			maltLine.add(""+mq);//maximumum query
		maltLine.add("-top");			maltLine.add(""+topMalt);//toppercent
		maltLine.add("-mpi");			maltLine.add(""+id);//minimum percentID
		if(verboseMalt)
			maltLine.add("-v");
		if(additionalMALTParameters!=null && additionalMALTParameters.size()>0)
			maltLine.addAll(additionalMALTParameters);//addtionalMaltParameters
		MALTCommandLine = maltLine;
		new File(outputME).mkdir();
	}
	private void processMALTExtractParameters(){
		if(Config.entryExists("filter")){
			filter = Config.getString("filter"); 
		}
		if(Config.entryExists("taxas")){
			String s = Config.getString("taxas");
			File  f = new File(s);
			if(f.exists()&&f.isFile()) {
				taxas.add(s);
			}else {
				for(String t:s.split(";"))
						taxas.add(t);
			}
		}else{
			log.log(Level.SEVERE,"A taxa List has to be specified in order to use MaltExtract"
					+ " either write taxas=/driectory/to/taxas/list.txt or"
					+ "taxas = species1 species 2 species 3");
		}
		if(Config.entryExists("resources")){
			resources = Config.getString("resources");
			if(!resources.endsWith("/"))
				resources+="/";
		}else{
			log.log(Level.SEVERE,"Resources has to be specified in order to use MaltExtract"
					+" provide path to folder with ncbi.map and ncbi.tre file");
			System.exit(1);
		}
		if(Config.entryExists("top")){
			top= Config.getDouble("top");
		}else{
			log.info("Use default top value of 0.01");
		}
		if(Config.entryExists("maxLength")){
			maxLength = Config.getInt("maxLength");
		}
		if(Config.entryExists("minPIdent")){
			minPIdent = Config.getDouble("minPIdent");
		}
		if(Config.entryExists("verboseME")){
			verboseME = Config.getBoolean("verboseME");
		}
		if(Config.entryExists("alignment")){
			wantReads = Config.getBoolean("alignment");
		}
		if(Config.entryExists("reads")){
			wantAlignment = Config.getBoolean("reads");
		}
		if(Config.entryExists("minComp")){
			minCompME = Config.getDouble("minComp");
		}
		if(Config.entryExists("meganSummary")){
			wantMeganSummary = Config.getBoolean("meganSummary");
		}
		if(Config.entryExists("destackingOff")){
			destackingOff = Config.getBoolean("destackingOff");
		}
		if(Config.entryExists("dupRemOff")){
			dupRemOff = Config.getBoolean("dupRemOff");
		}
		if(Config.entryExists("downSampOff")){
			downSampOff = Config.getBoolean("downSampOff");
		}
		if(Config.entryExists("additionalMaltExtractParameters")){
			additionalMaltExtractParameters = new ArrayList<String>();
			String s = Config.getString("additionalMaltExtractParameters");
			if(s.contains(";")) {
				for(String p:s.split(";"))
					additionalMaltExtractParameters.add(p);
			}else {
				additionalMaltExtractParameters.add(s);
			}
		}
		if(Config.entryExists("threadsMaltEx")) {
	 		threadsMex = Config.getInt("threadsMaltEx");
		}else {
			log.log(Level.SEVERE, "Using MALT with default of 16");
			threadsMex = 16;
		}
		if(Config.entryExists("maxMemoryMaltEx")){
			maxMemoryMex = Config.getInt("maxMemoryMaltEx");
			log.log(Level.INFO, "Set maximum Memory for MaltExtact to "+ maxMemoryMex +" GB");
		}else {
			maxMemoryMex = 150;
			log.log(Level.INFO, "Set Maximum Memorry for MaltExtract to "+maxMemoryMex +" GB");
		}
		if(Config.entryExists("partitionMaltEx")){
			partitionMex = Config.getString("partitionMaltEx");
			log.log(Level.INFO, "Set Partition for MaltExtract to "+partitionMex);
		}else {
			partitionMex = "batch";
			log.log(Level.INFO, "Set Partition for MaltExtract to "+partitionMex);
		}
	}
	private void generateMALTExtractCommandLine(String inputME, String outputME){
		ArrayList<String> meLine = new ArrayList<String>();
		meLine.add("java");			meLine.add("-jar");meLine.add("Xmx"+maxMemoryMex+"G");meLine.add(pathToMaltExtract);//start java process
		meLine.add("-i");			meLine.add(inputME);//Input
		meLine.add("-o");			meLine.add(outputME);//output
		meLine.add("-p");			meLine.add(""+threadsMex);//threads
		meLine.add("-t")	;			meLine.addAll(taxas);//taxas
		meLine.add("-f");			meLine.add(filter);//filter
		meLine.add("--resources");	meLine.add(resources);//resources
		if(top>0)
			meLine.add("--top");		meLine.add(""+top);//top percent
		if(maxLength>0)// maxLength
			meLine.add("-maxLength "+maxLength);
		if(minPIdent>0){//MinimumPI
			meLine.add("-minPI "+minPIdent);
		}
		if(verboseME){//verbose
			meLine.add("-verbose");
		}
		if(wantReads){//get Reads
			meLine.add("--reads");
		}
		if(wantAlignment){//get ALignments
			meLine.add("--matches");
		}
		if(minCompME>0){//minimum complexity Wooton Federhen
			meLine.add("-minComp");meLine.add(""+minCompME);
		}
		if(wantMeganSummary){// want Megan summeries
			meLine.add("--meganSummary");
		}
		if(destackingOff){// turn offf destacking
			meLine.add("--destackingOff");
		}
		if(dupRemOff){// turn off exact duplicate removal
			meLine.add("--dupRemOff");
		}
		if(downSampOff){// turn off downsampling
			meLine.add("--downSampOff");
		}
		if(additionalMaltExtractParameters != null && additionalMaltExtractParameters.size()>0)
			meLine.addAll(additionalMaltExtractParameters);
		MALTExtractCommandLine = meLine;
		new File(outputME).mkdir();
	}
	private void generateMALTExtractCommandLine(ArrayList<String> inputME, String outputME){
		ArrayList<String> meLine = new ArrayList<String>();
		meLine.add("java");			meLine.add("-jar");meLine.add("Xmx"+maxMemoryMex+"G");meLine.add(pathToMaltExtract);// start MaltExtract
		meLine.add("-i");			meLine.addAll(inputME);// input
		meLine.add("-o");			meLine.add(outputME);//output
		meLine.add("-p");			meLine.add(""+threadsMex);//threads
		meLine.add("-t");			meLine.addAll(taxas);//taxas
		meLine.add("-f");			meLine.add(filter);//filter
		meLine.add("--resources");	meLine.add(resources);//resources
		if(top>0)
			meLine.add("--top");		meLine.add(""+top);//top percent
		if(maxLength>0)// maxLength
			meLine.add("-maxLength "+maxLength);
		if(minPIdent>0){//MinimumPI
			meLine.add("-minPI "+minPIdent);
		}
		if(verboseME){//verbose
			meLine.add("-verbose");
		}
		if(wantReads){//get Reads
			meLine.add("--reads");
		}
		if(wantAlignment){//get ALignments
			meLine.add("--matches");
		}
		if(minCompME>0){//minimum complexity Wooton Federhen
			meLine.add("-minComp");	meLine.add(""+minCompME);
		}
		if(wantMeganSummary){// want Megan summeries
			meLine.add("--meganSummary");
		}
		if(destackingOff){// turn offf destacking
			meLine.add("--destackingOff");
		}
		if(dupRemOff){// turn off exact duplicate removal
			meLine.add("--dupRemOff");
		}
		if(downSampOff){// turn off downsampling
			meLine.add("--downSampOff");
		}
		if(additionalMaltExtractParameters != null && additionalMaltExtractParameters.size()>0)
			meLine.addAll(additionalMaltExtractParameters);
		MALTExtractCommandLine = meLine;
		new File(outputME).mkdir();
	}
}
