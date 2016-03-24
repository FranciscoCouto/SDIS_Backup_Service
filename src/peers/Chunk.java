package peers;

public class Chunk {
	
	public static final int MAX_SIZE = 64000;
	
	private String fileID;
	public int ChunkID, ChunkNo;
	public byte[] data;
	
	public Chunk(String fileId, int chunkNo, byte[] Data) {
		fileID= fileId;
		ChunkNo = chunkNo;
		data = Data;
	}
	
	public String getFileId() {
		return fileID;
	}
	
	public int getChunkNo() {
		return ChunkNo;
	}
	
	public byte[] getData() {
		return data;
	}

}
