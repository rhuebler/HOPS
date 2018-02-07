package ProcessManagement;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProcessExecutor {
	/**
	 * @author huebler
	 * @param ArrayList<String> command
	 * TRhis class is used to execute the different parts of AMPS pipeline and get console output
	 * @return
	 * @throws
	 */
	public boolean runSlurmJob(ArrayList<String> command,Logger log,String outDir, int threads, int maxMem){
		if(command!=null&&command.size()!=0) {
			String l ="";
			for(String s : command){
				log.log(Level.INFO,s);
				l+=s+" ";
			}
			ArrayList<String> com = new ArrayList<String>(); 
			com.add("sbatch");
			com.add("-o");com.add(outDir+"s.log");
			com.add("-c");com.add(""+threads);
			com.add("--mem");com.add(""+(maxMem*1000));
			com.add("--wrap=\""+l+"\"");
			log.log(Level.INFO,l);
			ProcessBuilder builder = new ProcessBuilder (com);
			boolean continueRun = false;
			//Map<String, String> environ = builder.environment();
			try {
				final Process process = builder.start();//get JobID here
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
			getJobID();
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
	public boolean runDependendSlurmJob(ArrayList<String> command,Logger log,String outDir, int threads, int maxMem, int dependency){
		if(command!=null&&command.size()!=0) {
			String l ="";
			for(String s : command){
				log.log(Level.INFO,s);
				l+=s+" ";
			}
			ArrayList<String> com = new ArrayList<String>(); 
			com.add("sbatch");
			com.add("-o");com.add(outDir+"s.log");
			com.add("-c");com.add(""+threads);
			com.add("--mem");com.add(""+(maxMem*1000));
			com.add("--wrap=\""+l+"\"");
			log.log(Level.INFO,l);
			ProcessBuilder builder = new ProcessBuilder (com);
			boolean continueRun = false;
			//Map<String, String> environ = builder.environment();
			try {
				final Process process = builder.start();//get JobID here
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
	public void getJobID(){
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
			    			System.out.println(line);
			    }
			   process.waitFor();
		    }
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
