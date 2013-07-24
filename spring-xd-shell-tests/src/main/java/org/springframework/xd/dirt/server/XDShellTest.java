package org.springframework.xd.dirt.server;

import static org.junit.Assert.*;

import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.DirectFieldAccessor;
import org.springframework.shell.Bootstrap;
import org.springframework.shell.core.CommandResult;
import org.springframework.shell.core.JLineShellComponent;
import org.springframework.web.context.support.XmlWebApplicationContext;
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

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class XDShellTest {
    
	static StreamServer s;
	static JLineShellComponent shell;
	
	@BeforeClass
	public static void startUp() {
		String xdhome = System.getenv("XDHome");
		AdminOptions opts = AdminMain.parseOptions(new String[] {"--transport", "local", "--store", "memory", "--disableJmx", "true", "--xdHomeDir", xdhome});
		s = AdminMain.launchStreamServer(opts);		
        Bootstrap bootstrap = new Bootstrap();
        shell = bootstrap.getJLineShellComponent();
	}
		
	@AfterClass
	public final static void shutdown() {
		s.stop();
        DirectFieldAccessor dfa = new DirectFieldAccessor(s);
        ((XmlWebApplicationContext) dfa.getPropertyValue("webApplicationContext")).destroy();
	}
	
	@Test
	public void a_createStream(){
        CommandResult cr = shell.executeCommand("stream create --definition \"http | file\" --name http2file");
        assertEquals(true, cr.isSuccess());
        assertEquals("Created new stream 'http2file'", cr.getResult());
	}

	@Test
	public void b_listStream(){
        CommandResult cr = shell.executeCommand("stream list");
        assertEquals(true, cr.isSuccess());
        assertTrue((cr.getResult().toString()).contains("http2file"));
	}

	@Test
	public void c_destroyStream(){
        CommandResult cr = shell.executeCommand("stream destroy --name http2file");
        assertEquals(true, cr.isSuccess());
        assertEquals("Destroyed stream 'http2file'", cr.getResult());
	}
}
