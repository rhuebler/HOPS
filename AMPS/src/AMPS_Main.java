import CommandLineReader.InputParameterProcessor;
import java.io.File;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import ProcessManagement.ProcessExecutor;
import Utility.AMPS_Mode;
import Utility.ParameterProcessor;
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
			//Go through config file if Provided and overwrite default values where necessary than generate commandLine for all included function
			ParameterProcessor processor;
			if(inputProcessor.getConfigFile()!=null) {
				processor = new ParameterProcessor(config, inputProcessor.getInputFiles(), inputProcessor.getOutDir(),log,inputProcessor.getAMPS_Mode());
			}else {
				processor = new ParameterProcessor(inputProcessor.getInputFiles(), inputProcessor.getOutDir(),log,inputProcessor.getAMPS_Mode());
			}
			processor.process();
			log.log(Level.INFO,"Run Mode " +  inputProcessor.getAMPS_Mode());
			if(processor.useSlurm()){// want to use the slurm schedular
				switch(inputProcessor.getAMPS_Mode()){
				case ALL:{
					ProcessExecutor executor = new ProcessExecutor();
					int MALT_ID = 0;
					if(processor.wantPreprocessing()) {
						int Pre_ID = executor.runSlurmJob(processor.getPreProcessingCommand(), log, processor.getOutDir()+"pre/", 
								32, processor.getMaxMemoryMalt(),"pre", "batch", 0);
						if(Pre_ID>0) {
							MALT_ID = executor.runSlurmJob(processor.getMALTCommandLine(), log, processor.getOutDir()+"malt/", 
								processor.getThreadsMalt(), processor.getMaxMemoryMalt(),"malt", processor.getPartitionMalt(),
								 Pre_ID);
						}
					}
					
					MALT_ID = executor.runSlurmJob(processor.getMALTCommandLine(), log, processor.getOutDir()+"malt/", 
							processor.getThreadsMalt(), processor.getMaxMemoryMalt(),"malt", processor.getPartitionMalt(),  0);
					if(MALT_ID>0){
						int MALTExID = executor.runSlurmJob(processor.getMALTExtractCommandLine(), log,  processor.getOutDir()+"maltExtract/", 
								processor.getThreadsMaltEx(), processor.getMaxMemoryMaltEx(),"ME", processor.getPartitionMaltEx(), MALT_ID);
						if(MALTExID>0){
							//log.log(Level.INFO, "Here Post processing would start with dependency "+ MALTExID);
							int postID = executor.runSlurmJob(processor.getPostProcessingLine(), log,processor.getOutDir()+"post/",processor.getMaxThreadsPost(),
									processor.getMaxMemoryPost(),"PO", processor.getPartitionPost(), MALTExID);
							if( postID==0){
								log.log(Level.SEVERE,"Postprocessing interuppted");
							}else{
								if(processor.wantCleaningUp()) {
									log.log(Level.INFO,"Commencing Cleanup Step");
									int cleaningID = executor.runSlurmJob(processor.getCleaningLine(), log,processor.getOutDir(),1,
											20,"CLEAN", "batch", postID);
									if(cleaningID==0)
										log.log(Level.SEVERE,"Cleaning interuppted");
								}
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
						int MALT_ID = executor.runSlurmJob(processor.getMALTCommandLine(), log, processor.getOutDir()+"malt/", 
							processor.getThreadsMalt(), processor.getMaxMemoryMalt(),"malt",processor.getPartitionMalt(), 0);
					if(MALT_ID == 0){
						log.log(Level.SEVERE,"MALT interuppted");
					}
					break;
				}
				case MALTEX:{
					ProcessExecutor executor = new ProcessExecutor();
					int MALTExID = executor.runSlurmJob(processor.getMALTExtractCommandLine(), log,  processor.getOutDir()+"maltExtract/", 
							processor.getThreadsMaltEx(), processor.getMaxMemoryMaltEx(),"ME", processor.getPartitionMaltEx(),  0);
					if(MALTExID == 0){
						log.log(Level.SEVERE,"MALTExtract interupted");
					}
					break;
				}
				case POST:{
					ProcessExecutor executor = new ProcessExecutor();
					int postProcessinID = executor.runSlurmJob(processor.getPostProcessingLine(), log,processor.getOutDir()+"post/",processor.getMaxThreadsPost(),
							processor.getMaxMemoryPost(),"PO",processor.getPartitionPost(), 0);
					if( postProcessinID==0){
						log.log(Level.SEVERE,"Postprocessing interuppted");
					}
					break;
				}
				default:
					break;
			}
			}else{
				//we can use this to run a job without a specific schedular and without preprocessing
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
				default:
					log.log(Level.WARNING,"Unsupported AMPS Mode!");
					break;
				}
			}	
			log.log(Level.INFO,"AMPS run finished!");
			System.exit(0);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}
