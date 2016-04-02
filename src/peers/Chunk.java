package peers;

public class Chunk {
	
	public static final int MAX_SIZE = 64000;
	
	private String fileID, PeerID;
	public int ChunkID, ChunkNo;
	
	/**
	 * Class Constructor
	 * @param fileId
	 * @param chunkNo
	 * @param PeerId
	 */
	public Chunk(String fileId, int chunkNo, String PeerId) {
		fileID= fileId;
		ChunkNo = chunkNo;
		PeerID = PeerId;
	}
	
	public String getFileId() {
		return fileID;
	}
	
	public int getChunkNo() {
		return ChunkNo;
	}
	
	public String getPeerID() {
		return PeerID;
	}
	

}
