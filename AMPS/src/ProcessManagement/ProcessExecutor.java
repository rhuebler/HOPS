package ProcessManagement;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ProcessExecutor {
	/**
	 * @author huebler
	 * @param ArrayList<String> command
	 * TRhis class is used to execute the different parts of HOPS pipeline and get console output
	 * @return
	 * @throws
	 */
	private void getErrorCommand(String name, String output,int dependency, Logger log) {
		ArrayList<String> command = new ArrayList<String>();
		switch(name){
			case "MALT":{
				command.add("sbatch");
				command.add("-o");
				command.add("-c");command.add(""+1);
				command.add("--mem");command.add("1G");
				command.add("-p");command.add("short");
				command.add("-t");command.add("02:00:00");
				command.add("--dependency=afterok:"+dependency);
				command.add("--job-name");command.add(name+"_error");
				command.add("--wrap");command.add("grep error|warning|exception" + output + "malt/malt.log>>"+output+"error.log");
				
				break;
				}
			case "ME":{
				command.add("sbatch");
				command.add("-o");
				command.add("-c");command.add(""+1);
				command.add("--mem");command.add("1G");
				command.add("-p");command.add("short");
				command.add("-t");command.add("02:00:00");
				command.add("--dependency=afterok:"+dependency);
				command.add("--job-name");command.add(name+"_error");
				command.add("--wrap");command.add("grep error|warning|exception"+ output + "maltExtract/ME.log>>"+output+"error.log");
				break;
				}
			case "PO":{
				command.add("sbatch");
				command.add("-o");
				command.add("-c");command.add(""+1);
				command.add("--mem");command.add("1G");
				command.add("-p");command.add("short");
				command.add("-t");command.add("02:00:00");
				command.add("--dependency=afterok:"+dependency);
				command.add("--job-name");command.add(name+"_error");
				command.add("--wrap");command.add("grep error|warning|exception"+ output +"maltExtract/ME.log>>"+output+"error.log");
				command.add("grep");
				command.add("error|warning|exception");
				command.add("post/PO.log");
				command.add(">>"+ output+"error.log");
				break;
				}
		}
		//Map<String, String> environ = builder.environment();
		ProcessBuilder builder = new ProcessBuilder (command);
		String line;
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

		log.log(Level.INFO,"Submitted Job: "+name+" with ID: "+getJobID(name)+" for User: "+System.getProperty("user.name"));
	}
	
	public Integer runSlurmJob(ArrayList<String> command,Logger log,String outDir, int threads, int maxMem, String name, String partition, int dependency, String walltime){
		int ID = 0;
		name = "HOPS"+name;
		if(command != null && command.size() != 0) {
			String line ="";
			for(String part : command){
				log.log(Level.INFO,part);
				line += part+" ";
			}
			line.trim();
			
			File f = new File(outDir);
			if(!f.isDirectory())
				f.mkdir();
			ArrayList<String> com = new ArrayList<String>(); 
			com.add("sbatch");
			com.add("-o");com.add(outDir+name+".log");
			com.add("-c");com.add(""+threads);
			com.add("--mem");com.add(""+(maxMem*1000));
			if(partition=="short" || partition=="medium") {
				com.add("-t");com.add(walltime);
			}else {
				//com.add("-t");com.add(walltime);
			}
			if(dependency > 0) {
				com.add("--dependency=afterok:"+dependency);
			}	
			com.add("--job-name");com.add(name);
			if(partition != "none") {
				com.add("-p");com.add(partition);
			}
			com.add("--wrap");com.add(line.trim());
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
		//String output = new File(outDir).getParent();
		//getErrorCommand(name, output, dependency, log);
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
			ArrayList<Integer>jobIds= new ArrayList<Integer>();
			final Process process = builder.start();//get JobID here
		    if(process.isAlive()){
		    	 	BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
			    String line;
			    while ((line = br.readLine()) != null) {
			    		if(line.contains(name)) {
			    			String[] parts = line.trim().split("\\s++");
			    			jobIds.add(Integer.parseInt(parts[0]));
			    		}
			    }	
			   process.waitFor();
		    }
		    for(int id: jobIds)
		    	if(id>jobID)
		    		jobID =id;
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
