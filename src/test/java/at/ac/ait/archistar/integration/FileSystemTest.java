package at.ac.ait.archistar.integration;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.fest.assertions.api.Assertions.*;

import at.ac.ait.archistar.TestEngine;
import at.ac.ait.archistar.distributor.TestServerConfiguration;
import at.ac.ait.archistar.cryptoengine.CryptoEngine;
import at.ac.ait.archistar.cryptoengine.DecryptionException;
import at.ac.ait.archistar.cryptoengine.PseudoMirrorCryptoEngine;
import at.ac.ait.archistar.data.CustomSerializer;
import at.ac.ait.archistar.data.user.FSObject;
import at.ac.ait.archistar.data.user.SimpleFile;
import at.ac.ait.archistar.distributor.BFTDistributor;
import at.ac.ait.archistar.distributor.Distributor;
import at.ac.ait.archistar.metadata.MetadataService;
import at.ac.ait.archistar.metadata.SimpleMetadataService;
import at.ac.ait.archistar.storage.FilesystemStorage;
import at.ac.ait.archistar.storage.StorageServer;

public class FileSystemTest extends AbstractIntegrationTest {
	
	private Logger logger = LoggerFactory.getLogger(FileSystemTest.class);
	
	private static Set<StorageServer> createNewServers() {		
		File baseDir = new File("/tmp/test-filesystem/" + UUID.randomUUID() + "/");
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
	
	@BeforeClass
	public static void prepareServer() {
		serverConfig = new TestServerConfiguration(createNewServers());
		serverConfig.setupTestServer(1);
	
		CryptoEngine crypto = new PseudoMirrorCryptoEngine(new CustomSerializer());
		Distributor distributor = new BFTDistributor(serverConfig);
		MetadataService metadata = new SimpleMetadataService(serverConfig, distributor);
		engine = new TestEngine(serverConfig, metadata, distributor, crypto);
	}
	
	@Test
	public void testPersistedStoreAndRetrieveOperation() {
	
		logger.info("starting test");
		SimpleFile testObject = new SimpleFile(randomTestFilename(), testData, new HashMap<String, String>());
		String path = testObject.getPath();
		
		// WHEN I connect engine and store a fragment 
		engine.connect();
		engine.putObject(testObject);

		// AND I disconnect and reconnect
		engine.disconnect();
		
		logger.info("reconnecting");
		assertThat(engine.connect()).isEqualTo(engine.getNumberOfServers());
		
		// THEN the data should still be available
		try {
			FSObject retrObject = engine.getObject(path);
			assertThat(retrObject).isNotNull().isInstanceOf(SimpleFile.class);
			assertThat(path).isEqualTo(retrObject.getPath());
			assertThat(((SimpleFile)retrObject).getData()).isEqualTo(testData);
		} catch (DecryptionException e) {
			fail("could not decrypt fragment", e);
		}
	}
	
	@AfterClass
	public static void shutdownServers() {
		serverConfig.teardownTestServer();
	}
}
