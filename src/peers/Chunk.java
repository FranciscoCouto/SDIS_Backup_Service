package peers;

public class Chunk {
	
	public static final int MAX_SIZE = 64000;
	
	private String fileID;
	public int ChunkID, ChunkNo;
	public String peerID;
	
	public Chunk(String fileId, int chunkNo, String peerId) {
		fileID= fileId;
		ChunkNo = chunkNo;
		peerID = peerId;
		
	}
	
	public String getFileId() {
		return fileID;
	}
	
	public int getChunkNo() {
		return ChunkNo;
	}
	
	public String getPeerID() {
		return peerID;
	}	

}
