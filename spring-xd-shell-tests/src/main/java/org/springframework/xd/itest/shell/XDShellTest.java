package org.springframework.xd.itest.shell;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.shell.CommandResult;
import org.springframework.shell.TestableShell;
import org.springframework.shell.core.JLineShellComponent;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContextManager;


/**
 * Integration test runner
 * 
 * @author Ilayaperumal Gopinathan
 * @author Kashyap Parikh
 * 
 */

@RunWith(Parameterized.class)
@ContextConfiguration({ "classpath:/shell-test-context.xml", "classpath:/META-INF/itest/shell/xd-shell-path-test-context.xml" })
public class XDShellTest {

    @Autowired
    private JLineShellComponent jlineShell;
    
    protected TestableShell shell;
    private TestContextManager testContextManager;
    
    /**
     * Initializes shell if it isn't already.
     */
    @Before
    public void init() {
    	this.testContextManager = new TestContextManager(getClass());
        try {
			this.testContextManager.prepareTestInstance(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
        if (shell == null) {
            shell = (TestableShell) jlineShell;
        }
        
        if (!shell.isRunning()) {
            shell.start();
        }
    }
    	
    /**
     * Resets shell after a test.
     */
	@After
	public void reset() {
	    shell.clear();
	}

	/*
	 * TBD: Move parser/data generator out, its cluttering tests
	 */
	
	@Parameters(name = "{index}-{0}")
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
	
	private String name;
	private String input;
	private String expectedResult;
	
	public XDShellTest(String name, String input, String expectedResult) {
		this.name = name;
		this.input = input;
		this.expectedResult = expectedResult;
	}
	
	@Test
	public void test(){
		String command = input;
		CommandResult cr = shell.exec(command);
        
        String outputText = cr.getOutputText();
        Map<String, Object> result = cr.getCommandOutput();

        assertNotNull("Output text for '" + command + "' command shouldn't be null.", outputText);
        assertTrue(outputText.contains(command));
        assertTrue(result.containsValue(expectedResult));
       
	}

}
