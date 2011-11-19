package org.techhub.techq;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

public class PrintWriterStream extends OutputStream {
	
	private final Writer writer;
	
	public PrintWriterStream(Writer writer) {  
        this.writer = writer;  
    }  
   
    public void write(int b) throws IOException {  
        write(new byte[] {(byte) b}, 0, 1);  
    }  
   
    public void write(byte b[], int off, int len) throws IOException {  
        writer.write(new String(b, off, len));  
    }  
   
    public void flush() throws IOException {  
        writer.flush();  
    }  
   
    public void close() throws IOException {  
        writer.close();  
    }  
}