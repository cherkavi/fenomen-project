package fenomen.module.web_service.service_implementation.storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/** хранилище объектов в виде файлового хранилища */
public class ObjectFileStorage implements IStorage<Object>{
	private SimpleDateFormat sdf=new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
	private final String fileSeparator=System.getProperty("file.separator");
	/** корневая папка */
	private String rootPath;
	/** расширение для файла */
	private String fileExtension=".bin";
	
	/** хранилище объектов в виде файлового хранилища 
	 * @param rootPath - папка, в которой нужно сохранять файлы
	 * @param fileExtension - расширение файлов  
	 */
	public ObjectFileStorage(String rootPath, String fileExtension){
		String tempPath=rootPath.trim();
		if(tempPath.endsWith(fileSeparator)){
			this.rootPath=tempPath;
		}else{
			this.rootPath=tempPath+fileSeparator;
		}
		this.fileExtension=fileExtension;
	}
	
	
	@Override
	public Object read(String identifier) throws Exception {
		File file=new File(rootPath+identifier);
		if(file.exists()==false){
			throw new Exception("file is not exists: "+rootPath+identifier);
		}else{
			FileInputStream fis=null;
			try{
				fis=new FileInputStream(file);
				ObjectInputStream input=new ObjectInputStream(fis);
				Object returnValue=input.readObject();
				input.close();
				return returnValue;
			}finally{
				try{
					fis.close();
				}catch(Exception ex){};
			}
		}
	}

	@Override
	public String save(Object object) throws Exception {
		String identifier=this.generateFileName();
		FileOutputStream fos=null;
		try{
			File file=new File(rootPath+identifier);
			fos=new FileOutputStream(file);
			ObjectOutputStream output=new ObjectOutputStream(fos);
			output.writeObject(object);
			output.flush();
		}finally{
			try{
				fos.close();
			}catch(Exception ex){};
		}
		return identifier;
	}
	
	/** сгенерировать новое имя файла */
	private String generateFileName(){
		return sdf.format(new Date())+"_"+getUniqueChar(3)+fileExtension;
	}
	
	private final static String hexChars[] = { "0", "1", "2", "3", "4", "5",
		"6", "7", "8", "9", "A", "B", "C", "D", "E", "F" };
	/** сгенерировать случайную последовательность из Hex чисел, указанной длинны 
	 * @param count - длинна случайной последовательности, которую необходимо получить 
	 * */
	private String getUniqueChar(int count){
        StringBuffer return_value=new StringBuffer();
        Random random=new java.util.Random();
        int temp_value;
        for(int counter=0;counter<count;counter++){
            temp_value=random.nextInt(hexChars.length);
            return_value.append(hexChars[temp_value]);
        }
        return return_value.toString();
	}
}
