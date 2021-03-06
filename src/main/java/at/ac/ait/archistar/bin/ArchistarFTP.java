package at.ac.ait.archistar.bin;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.mockftpserver.stub.StubFtpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.ac.ait.archistar.Engine;
import at.ac.ait.archistar.distributor.TestServerConfiguration;
import at.ac.ait.archistar.bin.archistarftp.FakeDeleCommand;
import at.ac.ait.archistar.bin.archistarftp.FakeLsCommand;
import at.ac.ait.archistar.bin.archistarftp.FakeRetrCommand;
import at.ac.ait.archistar.bin.archistarftp.FakeStorCommand;
import at.ac.ait.archistar.cryptoengine.CryptoEngine;
import at.ac.ait.archistar.cryptoengine.PseudoMirrorCryptoEngine;
import at.ac.ait.archistar.data.CustomSerializer;
import at.ac.ait.archistar.distributor.BFTDistributor;
import at.ac.ait.archistar.distributor.Distributor;
import at.ac.ait.archistar.metadata.MetadataService;
import at.ac.ait.archistar.metadata.SimpleMetadataService;
import at.ac.ait.archistar.storage.FilesystemStorage;
import at.ac.ait.archistar.storage.StorageServer;

/**
 * This is a simple testclient that implements a fake FTP Server on port 30022.
 * 
 * @author andy
 */
public class ArchistarFTP {
	
	private static Set<StorageServer> createNewServers() {		
		File baseDir = new File("/tmp/test-ftp-filesystem/");
		baseDir.mkdirs();
			
		File dir1 = new File(baseDir, "1");
		dir1.mkdir();
		File dir2 = new File(baseDir, "2");
		dir2.mkdir();
		File dir3 = new File(baseDir, "3");
		dir3.mkdir();
		File dir4 = new File(baseDir, "4");
		dir4.mkdir();
			
		HashSet<StorageServer> servers = new HashSet<StorageServer>();
		servers.add(new FilesystemStorage(0, dir1));
		servers.add(new FilesystemStorage(1, dir2));
		servers.add(new FilesystemStorage(2, dir3));
		servers.add(new FilesystemStorage(3, dir4));
		return servers;
	}
	
	private static Engine createEngine() {
		TestServerConfiguration serverConfig = new TestServerConfiguration(createNewServers());
		
		serverConfig.setupTestServer(1);
		CryptoEngine crypto = new PseudoMirrorCryptoEngine(new CustomSerializer());
		Distributor distributor = new BFTDistributor(serverConfig);
		MetadataService metadata = new SimpleMetadataService(serverConfig, distributor);
		return new Engine(serverConfig, metadata, distributor, crypto);
	}
	
	public static void main(String[] args) {
		
		Logger logger = LoggerFactory.getLogger(ArchistarFTP.class);
		
		logger.info("Starting archistar storage engine");
		Engine engine = createEngine();
		engine.connect();
		
		int port =30022;
		logger.info("Starting FTP server on port " + port);
		StubFtpServer stubFtpServer = new StubFtpServer();
		stubFtpServer.setServerControlPort(30022);
		
		stubFtpServer.setCommandHandler("LIST", new FakeLsCommand(engine));
		stubFtpServer.setCommandHandler("RETR", new FakeRetrCommand(engine));
		stubFtpServer.setCommandHandler("STOR", new FakeStorCommand(engine));
		stubFtpServer.setCommandHandler("DELE", new FakeDeleCommand(engine));
		
		stubFtpServer.start();
	}
}