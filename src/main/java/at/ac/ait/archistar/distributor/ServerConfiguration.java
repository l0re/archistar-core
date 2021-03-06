package at.ac.ait.archistar.distributor;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import at.ac.ait.archistar.storage.StorageServer;

/**
 * This contains the full server configuration.
 * 
 * Later verions should be able to handle server reconfiguraiton (ie.
 * dynamic server management)
 * 
 * @author andy
 */
public class ServerConfiguration {
	
	protected HashMap<String, StorageServer> serverMapId = new HashMap<String, StorageServer>();
	
	public ServerConfiguration(Set<StorageServer> servers) {
		for(StorageServer s : servers) {
			this.serverMapId.put(s.getId(), s);
		}
	}

	/**
	 * this is needed for the BFT network
	 * @return a Map <bft-id, server-port>
	 */
	public Map<Integer, Integer> getBFTServerNetworkPortMap() {
		
		HashMap<Integer, Integer>serverList = new HashMap<Integer, Integer>();
		int i = 0;
		for(StorageServer s : this.serverMapId.values()) {
			serverList.put(s.getBFTId(), 20000 + i++);
		}
		
		return serverList;
	}
	
	public StorageServer getStorageServer(String serverid) {
		return this.serverMapId.get(serverid);
	}

	public Collection<StorageServer> getOnlineStorageServers() {
		return this.serverMapId.values();
	}
	
	public int getOnlineStorageServerCount() {
		return this.getOnlineStorageServers().size();
	}
}
