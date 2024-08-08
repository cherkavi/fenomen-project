package com.cherkashin.vitaliy.modbus.transport.stable_rxtx;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

/** выходной поток, который записывает данные в Socket */
class SerialPortProxyOutputStream extends OutputStream{
	private byte[] buffer=new byte[1024];
	private String server="127.0.0.1";
	private int port=0;
	int nextPosition=0;
	
	public SerialPortProxyOutputStream(int portWrite) {
		this.port=portWrite;
	}
	public SerialPortProxyOutputStream(String server, int portWrite) {
		this.server=server;
		this.port=portWrite;
	}

	@Override
	public void write(int b) throws IOException {
		this.buffer[nextPosition++]=(byte)b;
		if(nextPosition>=buffer.length){
			this.flush();
		}
	}

	@Override
	public void flush() throws IOException {
		Socket socket=new Socket(this.server, this.port);
		socket.getOutputStream().write(this.buffer,0,this.nextPosition);
		socket.getOutputStream().flush();
		socket.close();
		this.nextPosition=0;
	}
	
	/*
	public static void main(String[] args) throws Exception {
		SerialPortProxyOutputStream output=new SerialPortProxyOutputStream(10);
		// output.write(1029);
		output.write(new byte[]{22,23,25,46});
		output.flush();
	}*/
}
