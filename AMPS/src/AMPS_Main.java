import CommandLineReader.InputParameterProcessor;
import java.io.File;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import ProcessManagement.ProcessExecutor;
import Utility.ParameterProcessor;
//TODO MALT works now set memory requirements for it.... still not integreated well with slurm maybe create second version
public class AMPS_Main {
	/**
	 * @author huebler 
	 * @params String args
	 * Main method of AMPS pipeline read in command line and config file and execute parts of the pipeline
	 */
	private static final Logger log = Logger.getLogger(AMPS_Main.class.getName());
	public static void main(String[] args) {
		try {
			InputParameterProcessor inputProcessor = new InputParameterProcessor(args);
			new File(inputProcessor.getOutDir()).mkdir();// make amps dir 
			FileHandler fh = new FileHandler(inputProcessor.getOutDir()+"amps.log", false);   // true forces append mode
			SimpleFormatter sf = new SimpleFormatter();
			fh.setFormatter(sf);
			log.addHandler(fh);
			log.log(Level.INFO, inputProcessor.getAllOptions());
			String config = inputProcessor.getConfigFile();
			log.log(Level.INFO,"Process Config File");
			//figure out Mode
			//check if stuff is mandatory
			ParameterProcessor processor = new ParameterProcessor(config, inputProcessor.getInputFiles(), inputProcessor.getOutDir(),log,inputProcessor.getAMPS_Mode());
			processor.process();
			log.log(Level.INFO,"Run Mode " +  inputProcessor.getAMPS_Mode());
			if(processor.useSlurm()){
				//run(ArrayList<String> command,Logger log,String outDir, int threads, int maxMem)
				switch(inputProcessor.getAMPS_Mode()){
			//TODO test	
				case ALL:{
					ProcessExecutor executor = new ProcessExecutor();
					int MALT_ID = executor.runSlurmJob(processor.getMALTCommandLine(), log, processor.getOutDir(), 
							processor.getThreadsMalt(), processor.getMaxMemoryMalt(),"malt", processor.getPartitionMalt());
					if(MALT_ID>0){
						int MALTExID = executor.runDependendSlurmJob(processor.getMALTExtractCommandLine(), log,  processor.getOutDir(), 
								processor.getThreadsMaltEx(), processor.getMaxMemoryMaltEx(),"maltEx",MALT_ID, processor.getPartitionMaltEx());
						if(MALTExID>0){
//							boolean PostProcessing = executor.run(processor.getPostProcessingLine(), log);
//							if( !PostProcessing){
//								log.log(Level.SEVERE,"Postprocessing interuppted");
//							}
						}else{
							log.log(Level.SEVERE,"MALTExtract interupted");
						}
					}else{
						log.log(Level.SEVERE,"MALT interuppted");
						System.exit(1);
					}
						
					break;
				}
				case MALT:{
					ProcessExecutor executor = new ProcessExecutor();
						int MALT_ID = executor.runSlurmJob(processor.getMALTCommandLine(), log, processor.getOutDir()+"malt/", 
							processor.getThreadsMalt(), processor.getMaxMemoryMalt(),"malt",processor.getPartitionMalt());
					if(MALT_ID == 0){
						log.log(Level.SEVERE,"MALT interuppted");
					}
					break;
				}
				case MALTEX:{
					ProcessExecutor executor = new ProcessExecutor();
					int MALTExID = executor.runSlurmJob(processor.getMALTExtractCommandLine(), log,  processor.getOutDir(), 
							processor.getThreadsMaltEx(), processor.getMaxMemoryMaltEx(),"maltEx", processor.getPartitionMaltEx());
					if(MALTExID == 0){
						log.log(Level.SEVERE,"MALTExtract interupted");
					}
					break;
				}
				case POST:{
					ProcessExecutor executor = new ProcessExecutor();
					boolean PostProcessing = executor.run(processor.getPostProcessingLine(), log);
					if( !PostProcessing){
						log.log(Level.SEVERE,"Postprocessing interuppted");
					}
					break;
				}
			}
			}else{
				switch(inputProcessor.getAMPS_Mode()){
					case ALL:{
						ProcessExecutor executor = new ProcessExecutor();
						boolean MALTfinished = executor.run(processor.getMALTCommandLine(), log);
						if(MALTfinished){
							boolean MALTExFinished = executor.run(processor.getMALTExtractCommandLine(), log);
							if(MALTExFinished){
								boolean PostProcessing = executor.run(processor.getPostProcessingLine(), log);
								if( !PostProcessing){
									log.log(Level.SEVERE,"Postprocessing interuppted");
								}
							}else{
								log.log(Level.SEVERE,"MALTExtract interupted");
							}
						}else{
							log.log(Level.SEVERE,"MALT interuppted");
							System.exit(1);
						}
							
						break;
					}
					case MALT:{
						ProcessExecutor executor = new ProcessExecutor();
						boolean MALTfinished = executor.run(processor.getMALTCommandLine(), log);
						if(!MALTfinished){
							log.log(Level.SEVERE,"MALT interuppted");
						}
						break;
					}
					case MALTEX:{
						ProcessExecutor executor = new ProcessExecutor();
						boolean MALTExFinished = executor.run(processor.getMALTExtractCommandLine(), log);
						if(!MALTExFinished){
							log.log(Level.SEVERE,"MALTExtract interupted");
						}
						break;
					}
					case POST:{
						ProcessExecutor executor = new ProcessExecutor();
						boolean PostProcessing = executor.run(processor.getPostProcessingLine(), log);
						if( !PostProcessing){
							log.log(Level.SEVERE,"Postprocessing interuppted");
						}
						break;
					}
				}
			}	
			log.log(Level.INFO,"AMPS run finished!");
			System.exit(0);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}
