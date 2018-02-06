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
	 */
	public boolean run(ArrayList<String> command,Logger log,String outDir, int threads, int maxMem){
		if(command!=null&&command.size()!=0) {
			String l ="";
			for(String s : command){
				log.log(Level.INFO,s);
				l+=s+" ";
			}
			ArrayList<String> com = new ArrayList<String>(); 
			com.add("slurm");
			com.add("-o");com.add(outDir+"s.log");
			com.add("-p");com.add(""+threads);
			com.add("memory");com.add(""+(maxMem*1000));
			com.add("--wrap=\""+l+"\"");
			log.log(Level.INFO,l);
			ProcessBuilder builder = new ProcessBuilder (com);
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
