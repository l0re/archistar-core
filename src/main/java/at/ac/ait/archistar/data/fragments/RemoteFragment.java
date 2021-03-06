package at.ac.ait.archistar.data.fragments;

import java.io.Serializable;

import at.ac.ait.archistar.storage.StorageServer;

/**
 * minimal implementation of the Fragment interface for use with
 * the Archistar protoype
 * 
 * @author andy
 */
public class RemoteFragment implements Fragment, Serializable {
	
	private static final long serialVersionUID = 4677966783656257460L;

	private String fragmentId;
	
	private byte[] data;
	
	private boolean syncedToServer;
	
	private StorageServer server;

	public RemoteFragment(String fragmentId) {
		this.fragmentId = fragmentId;
		this.syncedToServer = false;
	}
	
	public RemoteFragment(String fragmentId, StorageServer server) {
		this.fragmentId = fragmentId;
		this.server = server;
		this.syncedToServer = false;
	}

	public byte[] setData(byte[] data) {
		this.data = data;
		this.syncedToServer = false;
		return this.data;
	}
	
	public byte[] getData() {
		return this.data;
	}

	public boolean isSynchronized() {
		return this.syncedToServer;
	}

	public String getFragmentId() {
		return fragmentId;
	}

	public StorageServer getStorageServer() {
		return this.server;
	}

	public boolean setSynchronized(boolean value) {
		return this.syncedToServer = value;
	}

	public String setFragmentId(String fragmentId) {
		return this.fragmentId = fragmentId;
	}

}
