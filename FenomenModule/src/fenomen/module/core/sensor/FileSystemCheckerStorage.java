package fenomen.module.core.sensor;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.apache.log4j.Logger;

/** ��������� � �������� ������� 
 * ������������ ������� � ������� ������ T ����� �������������� hashCode() ��� ���������� ������������� ������� (�������� ��� ��������, � ������ ������ ��� ���������� ����� �����)
 * ��� Checker-�� �� ����� ������ �������-����� �� ������� ��� ����������� ������ ���� ��� ������ ������������� 
 * */
public class FileSystemCheckerStorage<T extends Serializable> implements ICheckerStorage<T> {
	private Logger logger=Logger.getLogger(this.getClass());
	private String fileSuffix=".bin";
	/** �������� ���� � ��������, �� �������� ����� ������ ����� */
	private String rootPath;
	/** ������ ���� ������ */
	private ArrayList<String> listOfFile=new ArrayList<String>();
	/** ��������������� ������ ��������*/
	private ArrayList<T> listOfObject=new ArrayList<T>();
	
	/** ��������� � �������� �������, �������������� ��������� ���� Checker-�� */
	public FileSystemCheckerStorage(String rootPath){
		String separator=System.getProperty("file.separator");
		rootPath=rootPath.trim();
		if(!rootPath.endsWith(separator)){
			this.rootPath=rootPath+separator;
		}else{
			this.rootPath=rootPath;
		}
		// �������� ����� �� ��������
		this.readAllFilesFromDirectory();
		// ������������� ����� �� �����������
		Collections.sort(this.listOfFile,new Comparator<String>(){
			@Override
			public int compare(String o1, String o2) {
				try{
					int dotO1=o1.indexOf(".");
					int dotO2=o2.indexOf(".");
					int i1=0;
					if(dotO1>=0){
						i1=Integer.parseInt(o1.substring(0,dotO1));
					}else{
						i1=Integer.parseInt(o1);
					}
					
					int i2=0;					
					if(dotO2>=0){
						i2=Integer.parseInt(o2.substring(0,dotO2));
					}else{
						i2=Integer.parseInt(o2);
					}

					if(i1==i2){
						return 0;
					}else{
						if(i1>i2){
							return 1;
						}else{
							return -1;
						}
					}
				}catch(Exception ex){
					return 0;
				}
			}
		});
		// �������� ������� �� ��������� ������
		for(int counter=0;counter<this.listOfFile.size();counter++){
			this.listOfObject.add(this.getObjectFromFile(this.rootPath+this.listOfFile.get(counter)));
		}
		// ��������� ������� �� null
		int counter=this.listOfFile.size()-1;
		while(counter>=0){
			if(this.listOfObject.get(counter)==null){
				this.listOfObject.remove(counter);
				this.listOfFile.remove(counter);
			}
			counter--;
		}
	}
	
	/** ����������� ������� �� �������� */
	@SuppressWarnings("unchecked")
	private T getObjectFromFile(String path){
		try{
			ObjectInputStream input=new ObjectInputStream(new FileInputStream(path));
			Object object=input.readObject();
			return (T)object;
		}catch(Exception ex){
			return null;
		}
	}
	
	/** �������� ��� ����� �� �������� */
	private void readAllFilesFromDirectory(){
		try{
			File file=new File(this.rootPath);
			if(file.exists()==false){
				//������� �� ������ - ������� 
				File dir=new File(this.rootPath);
				if(dir.mkdirs()==false){
					System.err.println("Create directory Exception: ");
				}
				file=new File(this.rootPath);
			}
			File[] files=file.listFiles(new FileFilter(){
				@Override
				public boolean accept(File file) {
					return file.getName().endsWith(fileSuffix);
				}
			});
			if(files!=null){
				for(int counter=0;counter<files.length;counter++){
					this.listOfFile.add(files[counter].getName());
				}
			}
		}catch(Exception ex){
			logger.error("readAllFilesFromDirectory Exception:"+ex.getMessage());
		}
	}
	
	@Override
	public int getStorageSize() {
		return this.listOfFile.size();
	}

	@Override
	public T readFromStorage(int index) {
		if(index<listOfFile.size()){
			return listOfObject.get(index);
		}else{
			return null;
		}
	}

	@Override
	public String saveToStorage(T value) {
		// ������������� ����� ��� �����
		String fileName=this.getNextFileName(value);
		// ��������� ���� �� �����  
		if(this.saveObjectToFile(this.rootPath+fileName, value)){
			// �������� ����� ������� 
			this.listOfFile.add(fileName);
			this.listOfObject.add(value);
			return fileName;
		}else{
			return null;
		}
	}

	/** ��������� ������ �� ������� �������� */
	private boolean saveObjectToFile(String path, T value){
		try{
			ObjectOutputStream output=new ObjectOutputStream(new FileOutputStream(path));
			output.writeObject(value);
			output.flush();
			output.close();
			return true;
		}catch(Exception ex){
			logger.error("saveObjectToFile Exception: "+ex.getMessage());
			return false;
		}
	}
	
	/** �������� ��������� ��� �����  */
	private String getNextFileName(T object){
		/*
		int maxValue=0;
		if(this.listOfFile.size()==0){
			maxValue=0;
		}else{
			maxValue=this.getIntFromFileName(this.listOfFile.get(0));
		}
		
		for(int counter=0;counter<this.listOfFile.size();counter++){
			int currentValue=this.getIntFromFileName(this.listOfFile.get(counter));
			if(maxValue<currentValue){
				maxValue=currentValue;
			}
		}
		return Integer.toString(maxValue+1)+this.fileSuffix;
		*/
		return Integer.toString(object.hashCode())+this.fileSuffix;
	}
	
	/*private int getIntFromFileName(String fileName){
		try{
			return Integer.parseInt(fileName.substring(0,fileName.length()-this.fileSuffix.length()));
		}catch(Exception ex){
			return 0;
		}
	}*/
	
	@Override
	public boolean removeFromStorage(int code) {
		boolean returnValue=false;
		for(int index=0;index<this.listOfObject.size();index++){
			if((this.listOfObject.get(index)!=null)&&(this.listOfObject.get(index).hashCode()==code)){
				File file=new File(this.rootPath+this.listOfFile.get(index));
				if(file.delete()){
					// ������� �� ������� �������
					this.listOfFile.remove(index);
					this.listOfObject.remove(index);
					returnValue=true;
					break;
				}else{
					returnValue=false;
					break;
				}
			}
		}
		return returnValue;
	}

}
