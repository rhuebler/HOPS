package Utility;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.List;

public class tester {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		RuntimeMXBean bean = ManagementFactory.getRuntimeMXBean();
		List<String> aList = bean.getInputArguments();
		for (int i = 0; i < aList.size(); i++) {
		 System.out.println( aList.get( i ) );

		  } 
	}

}
