package ConfigFileReader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * The class Config reads the settings which are stored in the config file.
 * The values are stored in a hash map and can be retrieved by various static
 * functions.
 * 
 * @author Alexander Herbig
 *
 */
public class Config
{	
	private static Map<String,String> values = new HashMap<String, String>();
	
	public static boolean entryExists(String name)
	{
		return values.containsKey(name);
	}
	
	/**
	 * Returns the value of the config file entry whose name is provided. 
	 * 
	 * @param name	the name of the config file entry
	 * @return the String value stored in the config file entry <code>name</code>
	 */
	public static String getString(String name)
	{
		String value = values.get(name);
		if(value==null)
			throw new RuntimeException("No value found for config file entry '"+name+"'. The config file seems to be corrupted.");
		else
			return value;
	}
	
	/**
	 * Returns the value of the config file entry whose name is provided. 
	 * 
	 * @param name	the name of the config file entry
	 * @return the Integer value stored in the config file entry <code>name</code>
	 */
	public static int getInt(String name)
	{
		String value = values.get(name);

		if(value!=null)
		{
			int res = 0;
			try
			{
				res = Integer.parseInt(value);
			}
			catch(NumberFormatException e)
			{
				throw new NumberFormatException("Invalid value ('"+value+"') for config file entry '"+name+"'. Integer expected! The config file seems to be corrupted.");
			}
			return res;
		}		
		else
		{
			throw new RuntimeException("No value found for config file entry '"+name+"'. The config file seems to be corrupted.");
		}
	}
	
	/**
	 * Returns the value of the config file entry whose name is provided. 
	 * 
	 * @param name	the name of the config file entry
	 * @return the Boolean value stored in the config file entry <code>name</code>
	 */
	public static boolean getBoolean(String name)
	{
		String value = values.get(name);

		if(value!=null)
		{
			int res = 0;
			try
			{
				res = Integer.parseInt(value);
			}
			catch(NumberFormatException e)
			{
				throw new NumberFormatException("Invalid value ('"+value+"') for config file entry '"+name+"'. Integer (1 or 0) expected! The config file seems to be corrupted.");
			}
			if(res == 1)
				return true;
			else if(res == 0)
				return false;
			else
				throw new NumberFormatException("Invalid value ('"+value+"') for config file entry '"+name+"'. Integer (1 or 0) expected! The config file seems to be corrupted.");
		}		
		else
		{
			throw new RuntimeException("No value found for config file entry '"+name+"'. The config file seems to be corrupted.");
		}
	}
	
	/**
	 * Returns the value of the config file entry whose name is provided. 
	 * 
	 * @param name	the name of the config file entry
	 * @return the Double value stored in the config file entry <code>name</code>
	 */
	public static double getDouble(String name)
	{
		String value = values.get(name);

		if(value!=null)
		{
			double res = 0;
			try
			{
				res = Double.parseDouble(value);
			}
			catch(NumberFormatException e)
			{
				throw new NumberFormatException("Invalid value ('"+value+"') for config file entry '"+name+"'. Double expected! The config file seems to be corrupted.");
			}
			return res;
		}		
		else
		{
			throw new RuntimeException("No value found for config file entry '"+name+"'. The config file seems to be corrupted.");
		}
	}
	
	/**
	 * Returns the value of the config file entry whose name is provided. 
	 * 
	 * @param name	the name of the config file entry
	 * @return the Char value stored in the config file entry <code>name</code>
	 */
	public static char getChar(String name)
	{
		String value = values.get(name);
		if(value==null||value.length()==0)
			throw new RuntimeException("No value found for config file entry '"+name+"'. The config file seems to be corrupted.");
		else
			return value.charAt(0);
	}
	
	/**
	 * Reads the config file and stores the value in a hash map.
	 * 
	 * @return the hash map containing all config file entries
	 * @throws IOException 
	 */
	public static void readConfigFile(String filename)
	{	
		Map<String,String> res = new java.util.HashMap<String, String>();

		try
		{
			BufferedReader reader = new BufferedReader(new FileReader(filename));
			String line;
			String[] nameValue;
			String name;
			String value;
			
			for(line=reader.readLine();line!=null;line=reader.readLine())
			{
				line=line.trim();
				if(line.length()==0||line.charAt(0)=='#')
					continue;
				
				nameValue = line.split("=");
				name = nameValue[0].trim();
				
				if(nameValue.length>=2)
					value = nameValue[1].trim();
				else 
					value = "";
				
				res.put(name, value);				
			}
			reader.close();
		}
		
		catch(Exception e)
		{
			System.err.println("Something went wrong when reading the config file.");
			e.printStackTrace();
		}
		values = res;
	}
	
	public static void setConfig(Map<String, String> values)
	{	
		Config.values = values;
	}
	
//	private static void writeConfigFile()
//	{
//		try
//		{
//			BufferedWriter bw = new BufferedWriter(new FileWriter(configFile));
//			bw.write(defaultConfigFile);
//			bw.close();
//		}
//		catch(IOException e)
//		{
//			System.err.println("Cannot write configuration file!");
//			e.printStackTrace();
//		}
//	}
			
}

