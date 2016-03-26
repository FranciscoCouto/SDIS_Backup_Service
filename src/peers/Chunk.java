package peers;

public class Chunk {
	
	public static final int MAX_SIZE = 64000;
	
	private String fileID;
	public int ChunkID, ChunkNo;
	
	public Chunk(String fileId, int chunkNo) {
		fileID= fileId;
		ChunkNo = chunkNo;
	}
	
	public String getFileId() {
		return fileID;
	}
	
	public int getChunkNo() {
		return ChunkNo;
	}
	

}
