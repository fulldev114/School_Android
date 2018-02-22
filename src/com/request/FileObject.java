package com.request;

import android.net.Uri;

import java.util.Arrays;

public class FileObject {

	private String fileName;
	private String accessName;
	private String fileType;
	private String filePath;
	private byte[] byteData;
	private String contentType;
	private Uri fileURI;
	
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	public String getAccessName() {
		return accessName;
	}
	public void setAccessName(String accessName) {
		this.accessName = accessName;
	}
	public String getFileType() {
		return fileType;
	}
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public byte[] getByteData() {
		return byteData;
	}
	public void setByteData(byte[] byteData) {
		this.byteData = byteData;
	}
	
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	
	public Uri getFileURI() {
		return fileURI;
	}
	public void setFileURI(Uri fileURI) {
		this.fileURI = fileURI;
	}
	@Override
	public String toString() {
		return "FileObject [fileName=" + fileName + ", accessName="
				+ accessName + ", fileType=" + fileType + ", filePath="
				+ filePath + ", byteData=" + Arrays.toString(byteData)
				+ ", contentType=" + contentType + "]";
	}
	
}
