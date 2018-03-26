package ProcessManagement;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import Utility.AMPS_Mode;

public class ProcessExecutor {
	/**
	 * @author huebler
	 * @param ArrayList<String> command
	 * TRhis class is used to execute the different parts of AMPS pipeline and get console output
	 * @return
	 * @throws
	 */
	public Integer runSlurmJob(ArrayList<String> command,Logger log,String outDir, int threads, int maxMem, String name, String partition,  AMPS_Mode mode, int dependency){
		int ID = 0;
		name = "AMPS"+name;
		if(command != null && command.size() != 0) {
			String line ="";
			for(String part : command){
				log.log(Level.INFO,part);
				line += part+" ";
			}
			line.trim();
			
		
			ArrayList<String> com = new ArrayList<String>(); 
			com.add("sbatch");
			com.add("-o");com.add(outDir+name+".log");
			com.add("-c");com.add(""+threads);
			com.add("--mem");com.add(""+(maxMem*1000));
			if(dependency > 0) {
				com.add("--dependency=afterok:"+dependency);
			}	
			com.add("--job-name");com.add(name);
			com.add("-p");com.add(partition);
			switch(mode){
			default: com.add("--wrap");com.add(line.trim());
				break;
			case POST:
				com.add("--wrap");com.add("\""+line.trim()+"\"");
				break;
			}
			line="";
			for(String part : com)
				line += part+" ";
			
			log.log(Level.INFO,line);
			ProcessBuilder builder = new ProcessBuilder (com);
			//Map<String, String> environ = builder.environment();
			try {
				final Process process = builder.start();//get JobID here
			    if(process.isAlive()){
			    	 	BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
				    line = null;
				    while ((line = br.readLine()) != null) {
				      log.log(Level.INFO,line);
				    }
				    BufferedReader be = new BufferedReader(new InputStreamReader(process.getErrorStream()));
				    while ((line = be.readLine()) != null) {
					      log.log(Level.WARNING,line);
				    }

				    int status = process.waitFor();
					log.log(Level.INFO,"Process Exited with status: " + status);
			    }
			} catch (IOException | InterruptedException e) {
				e.printStackTrace();
			}
			ID = getJobID(name);
			log.log(Level.INFO,"Submitted Job: "+name+" with ID: "+ID+" for User: "+System.getProperty("user.name"));
		}
		else {
			log.log(Level.SEVERE,"Process interuppted");
			System.exit(1);
		}
		return ID;
	}
	
	public int getJobID(String name){
		int jobID = 0;
		ArrayList<String> cmd= new ArrayList<String>();
		cmd.add("squeue");
		cmd.add("-u");
		cmd.add(System.getProperty("user.name"));
		ProcessBuilder builder = new ProcessBuilder (cmd);
		builder.redirectErrorStream();
		try {
			final Process process = builder.start();//get JobID here
		    if(process.isAlive()){
		    	 	BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
			    String line;
			    while ((line = br.readLine()) != null) {
			    		if(line.contains(name)) {
			    			String[] parts = line.trim().split("\\s++");
			    			jobID = Integer.parseInt(parts[0]);
			    		}
			    }	
			   process.waitFor();
		    }
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jobID;
	}
	public boolean run(ArrayList<String> command,Logger log){
		if(command!=null&&command.size()!=0) {
			String l ="";
			for(String s : command){
				log.log(Level.INFO,s);
				l+=s+" ";
			}
			log.log(Level.INFO,l);
			ProcessBuilder builder = new ProcessBuilder (command);
			boolean continueRun = false;
			//Map<String, String> environ = builder.environment();
			try {
				final Process process = builder.start();
			    if(process.isAlive()){
			    	 	BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
				    String line;
				    while ((line = br.readLine()) != null) {
				      log.log(Level.INFO,line);
				    }
				    BufferedReader be = new BufferedReader(new InputStreamReader(process.getErrorStream()));
				    while ((line = be.readLine()) != null) {
					      log.log(Level.WARNING,line);
				    }

				    int status = process.waitFor();
					log.log(Level.INFO,"Process Exited with status: " + status);
			    }
				continueRun = true;
			} catch (IOException | InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(continueRun)
				return continueRun;
			else{
				 log.log(Level.SEVERE,"Process interuppted");
				System.exit(1);
				return continueRun;
			}	
		}
		else {
			log.log(Level.SEVERE,"Process interuppted");
			System.exit(1);
			return false;
		}
	}
}
