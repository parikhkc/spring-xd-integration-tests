package org.springframework.xd.dirt.server;

import static org.junit.Assert.assertEquals;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.shell.Bootstrap;
import org.springframework.shell.core.CommandResult;
import org.springframework.shell.core.JLineShellComponent;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.xd.dirt.server.AdminMain;
import org.springframework.xd.dirt.server.options.AdminOptions;
import org.springframework.xd.dirt.stream.StreamServer;


/**
 * Integration test runner
 * 
 * @author Ilayaperumal Gopinathan
 * @author Kashyap Parikh
 * 
 */

@ContextConfiguration({ "classpath:/shell-test-context.xml", "classpath:/META-INF/itest/shell/xd-shell-path-test-context.xml" })
public class XDShellTest {
    
	static StreamServer s;
	
	@BeforeClass
	public static void startUp() {
		String xdhome = System.getenv("XDHome");
		AdminOptions opts = AdminMain.parseOptions(new String[] {"--transport", "local", "--store", "memory", "--disableJmx", "false", "--xdHomeDir", xdhome});
		s = AdminMain.launchStreamServer(opts);
		while(!s.isRunning()) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
		
	@AfterClass
	public static void shutdown() {
		s.stop();
	}
	
	@Test
	public void test(){
        Bootstrap bootstrap = new Bootstrap();
        
        JLineShellComponent shell = bootstrap.getJLineShellComponent();
        
        CommandResult cr = shell.executeCommand("stream create --definition \"http | file\" --name http2file");
        assertEquals(true, cr.isSuccess());
        assertEquals("Created new stream 'http2file'", cr.getResult());

	}

}
