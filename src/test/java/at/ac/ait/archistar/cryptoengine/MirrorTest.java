package at.ac.ait.archistar.cryptoengine;

import static org.mockito.Mockito.*;
import static org.fest.assertions.api.Assertions.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;

import at.ac.ait.archistar.data.CustomSerializer;
import at.ac.ait.archistar.data.fragments.Fragment;
import at.ac.ait.archistar.data.user.FSObject;
import at.ac.ait.archistar.storage.StorageServer;

public class MirrorTest {
	
	private static FSObject testData;
	private static Set<Fragment> distribution;
	private static CryptoEngine cryptoEngine;
	private final static byte[] mockSerializedData = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
	private static Fragment frag1;
	private static Fragment frag2;
	
	@BeforeClass
	public static void onceSetup() {
		// GIVEN some test data
		testData = mock(FSObject.class);
		CustomSerializer serializer = mock(CustomSerializer.class);
		when(serializer.serialize(testData)).thenReturn(mockSerializedData);
		when(serializer.deserialize(mockSerializedData)).thenReturn(testData);		
		cryptoEngine = new PseudoMirrorCryptoEngine(serializer);
		
		/* GIVEN some mock fragments */
		distribution = new HashSet<Fragment>();
		StorageServer server = mock(StorageServer.class);

		frag1 = mock(Fragment.class);
		when(frag1.getStorageServer()).thenReturn(server);
		when(frag1.getFragmentId()).thenReturn("id-1");
		when(frag1.setData(eq(mockSerializedData))).thenReturn(mockSerializedData);
		when(frag1.getData()).thenReturn(mockSerializedData);
		when(frag1.isSynchronized()).thenReturn(true);
		distribution.add(frag1);
		
		frag2 = mock(Fragment.class);
		when(frag2.getStorageServer()).thenReturn(server);
		when(frag2.getFragmentId()).thenReturn("id-2");
		when(frag2.setData(eq(mockSerializedData))).thenReturn(mockSerializedData);
		when(frag2.getData()).thenReturn(mockSerializedData);
		when(frag2.isSynchronized()).thenReturn(true);
		distribution.add(frag2);
	}
	
	@Test
	public void testIfCryptoEngineProducesEnoughFragments() {
		// WHEN i encrypt some data
		Set<Fragment> encrypted = cryptoEngine.encrypt(testData, distribution);
		
		// THEN i except the data to be distributed to all servers
		assertThat(encrypted).hasSize(distribution.size());
	}
	
	@Test
	public void testIfMirroringWorks() {
		// WHEN i encrypt data
		cryptoEngine.encrypt(testData, distribution);
		
		// THEN i expect the serialized data to be forwarded to the fragments
		verify(frag1).setData(mockSerializedData);
		verify(frag2).setData(mockSerializedData);
	}
		
	@Test
	public void testIfDecryptionProducesOriginalData() {
		Set<Fragment> encrypted = cryptoEngine.encrypt(testData, distribution);
		
		FSObject result = null;
		try {
			result = cryptoEngine.decrypt(encrypted);
		} catch (DecryptionException e) {
			fail("error while decryption", e);
		}
		assertThat(result).isNotNull().isEqualTo(result);
	}
}