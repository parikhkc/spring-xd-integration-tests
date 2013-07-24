package org.springframework.xd.dirt.server;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;


import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.shell.Bootstrap;
import org.springframework.shell.core.CommandResult;
import org.springframework.shell.core.JLineShellComponent;
import org.springframework.test.context.ContextConfiguration;


/**
 * Integration test runner
 * 
 * @author Ilayaperumal Gopinathan
 * @author Kashyap Parikh
 * 
 */


@ContextConfiguration({ "classpath:/shell-test-context.xml", "classpath:/META-INF/itest/shell/xd-shell-path-test-context.xml" })
public class XDShellTestcase {

	

	@BeforeClass
	public static void startUp() {
		System.out.print("==========Starting Stream Server");
//		AdminOptions opts = AdminMain.parseOptions(new String[] {"--transport", "local", "--store", "memory", "--disableJmx", "true",  "--xdHomeDir", "/Users/kparikh/git/parikhkc/spring-xd/build/dist/spring-xd/xd"});
//		StreamServer s = AdminMain.launchStreamServer(opts);
		Runtime rt = Runtime.getRuntime();
        try {
			//Process proc = rt.exec("/Users/kparikh/git/parikhkc/spring-xd/build/dist/spring-xd/xd/bin/xd-singlenode");
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	public static Collection<Object[]> generateData() {
		Collection<Object[]> testCases = new ArrayList<Object[]>();
		
		try {
			PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
			Resource[] resources = resolver.getResources("classpath:/tests/*");
			
			for (int i=0; i<resources.length; i++) {
				File testFile = resources[i].getFile();
        	    JsonFactory jsonFactory = new JsonFactory();
	        	JsonParser jParser = jsonFactory.createJsonParser(testFile);
	        	try {
	        		while (jParser.nextToken() != JsonToken.END_OBJECT) {
	        			String fieldname = jParser.getCurrentName();
	        			if ("testcases".equals(fieldname)) {
	        				while (jParser.nextToken() != JsonToken.END_ARRAY) {
	    	        			String cmd = null, result = null, name = null;
		        				fieldname = jParser.getCurrentName();
		        				if (("name").equals(fieldname)) {
			        				  jParser.nextToken();
				        			  name = jParser.getText();
				        			  System.out.println("Name = " + name); 
				        			  jParser.nextToken();
				        			  fieldname=jParser.getCurrentName();
		        				}
		        				if ("command".equals(fieldname)) {
			        			  // current token is "command",
		        				  jParser.nextToken();
			        			  cmd = jParser.getText();
			        			  System.out.println("Command = " + cmd); 
			        			  jParser.nextToken();
			        			  fieldname=jParser.getCurrentName();
			        			}
			        			if ("result".equals(fieldname)) {
			        			  // current token is "result"
			        			  jParser.nextToken();
			        			  result = jParser.getText();
			        			  System.out.println("Result = " + result); 
			        			}
			        			if (cmd!=null && result!=null) {
									testCases.add(new Object[] { name, cmd, result });
								}
	        				}
	        			}
	        		}
	        		  jParser.close();
	        	     } catch (IOException e) {
	        		  e.printStackTrace();
	        	     }
	        }
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("testCases = " + testCases); 
		return testCases;
		
	}
	

	
	@Test
	public void test(){
      
        
        Bootstrap bootstrap = new Bootstrap();
       
        JLineShellComponent shell = bootstrap.getJLineShellComponent();
        
        CommandResult cr = shell.executeCommand("stream create --definition \"http | file\" --name http2file3");
        assertEquals(true, cr.isSuccess());
        System.out.println(cr.getResult());
        assertEquals("Message = [hello] Location = [null]", cr.getResult());

	}

}
