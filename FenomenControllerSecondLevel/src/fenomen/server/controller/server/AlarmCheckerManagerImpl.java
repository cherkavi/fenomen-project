package fenomen.server.controller.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import database.ConnectWrap;
import database.StaticConnector;
import database.wrap.ModuleAlarmCheckerWrap;
import database.wrap.ModuleStorage;
import database.wrap.ModuleTaskWrap;


import fenomen.server.controller.client.view.settings.module.sensor_window.sensor_panel.WrapChecker;
import fenomen.server.controller.client.view.settings.module.sensor_window.sensor_panel.alarm_checker_panel.AlarmCheckerManager;
import fenomen.server.controller.client.view.settings.module.sensor_window.sensor_panel.alarm_checker_panel.AlarmCheckerTableElement;
import fenomen.server.controller.server.generator_alarm_checker.calc.Checker;
import fenomen.server.controller.server.generator_alarm_checker.calc_implementation.*;
import fenomen.server.controller.server.generator_alarm_checker.message.AlarmMessage;
import fenomen.server.controller.server.utility.XmlData;
import fenomen.server.controller.server.utility.xml_data.task.TaskData;
import fenomen.server.controller.server.utility.xml_data.task.TaskDataAlarmCheckerAdd;
import fenomen.server.controller.server.utility.xml_data.task.TaskDataAlarmCheckerRemove;
import fenomen.server.controller.server.utility.xml_data.task.TaskDataAlarmCheckerReplace;

public class AlarmCheckerManagerImpl extends RemoteServiceServlet implements AlarmCheckerManager{
	private static final long serialVersionUID = 1L;
	private String fileSeparator=System.getProperty("file.separator");

	/** 
	 * {@inheritDoc}
	 */
	@Override
	public String addAlarmChecker(WrapChecker wrapChecker) {
		System.out.println("addAlarmChecker");
		try{
			Checker<AlarmMessage> checker=null;
			// создать проверяющий элемент
			if(wrapChecker.getValuePresentation().equals(AlarmCheckerManager.presentationInteger)){
				if(wrapChecker.getValueCondition().equals(AlarmCheckerManager.conditionEQ)){
					checker=new EQIntegerChecker(wrapChecker.getRegisterAddress(), wrapChecker.getTimeDelay(), wrapChecker.getAlarmMessage(), Integer.parseInt(wrapChecker.getControlValue()));
				}else if(wrapChecker.getValueCondition().equals(AlarmCheckerManager.conditionNE)){
					checker=new NEIntegerChecker(wrapChecker.getRegisterAddress(), wrapChecker.getTimeDelay(), wrapChecker.getAlarmMessage(), Integer.parseInt(wrapChecker.getControlValue()));
				}else if(wrapChecker.getValueCondition().equals(AlarmCheckerManager.conditionGE)){
					checker=new GEIntegerChecker(wrapChecker.getRegisterAddress(), wrapChecker.getTimeDelay(), wrapChecker.getAlarmMessage(), Integer.parseInt(wrapChecker.getControlValue()));
				}else if(wrapChecker.getValueCondition().equals(AlarmCheckerManager.conditionGT)){
					checker=new GTIntegerChecker(wrapChecker.getRegisterAddress(), wrapChecker.getTimeDelay(), wrapChecker.getAlarmMessage(), Integer.parseInt(wrapChecker.getControlValue()));
				}else if(wrapChecker.getValueCondition().equals(AlarmCheckerManager.conditionLE)){
					checker=new LEIntegerChecker(wrapChecker.getRegisterAddress(), wrapChecker.getTimeDelay(), wrapChecker.getAlarmMessage(), Integer.parseInt(wrapChecker.getControlValue()));
				}else if(wrapChecker.getValueCondition().equals(AlarmCheckerManager.conditionLT)){
					checker=new LTIntegerChecker(wrapChecker.getRegisterAddress(), wrapChecker.getTimeDelay(), wrapChecker.getAlarmMessage(), Integer.parseInt(wrapChecker.getControlValue()));
				}
			}else if(wrapChecker.getValuePresentation().equals(AlarmCheckerManager.presentationDouble)){
				if(wrapChecker.getValueCondition().equals(AlarmCheckerManager.conditionEQ)){
					checker=new EQDoubleChecker(wrapChecker.getRegisterAddress(), wrapChecker.getTimeDelay(), wrapChecker.getAlarmMessage(), Double.parseDouble(wrapChecker.getControlValue()),3);
				}else if(wrapChecker.getValueCondition().equals(AlarmCheckerManager.conditionNE)){
					checker=new NEDoubleChecker(wrapChecker.getRegisterAddress(), wrapChecker.getTimeDelay(), wrapChecker.getAlarmMessage(), Double.parseDouble(wrapChecker.getControlValue()),3);
				}else if(wrapChecker.getValueCondition().equals(AlarmCheckerManager.conditionGE)){
					checker=new GEDoubleChecker(wrapChecker.getRegisterAddress(), wrapChecker.getTimeDelay(), wrapChecker.getAlarmMessage(), Double.parseDouble(wrapChecker.getControlValue()),3);
				}else if(wrapChecker.getValueCondition().equals(AlarmCheckerManager.conditionGT)){
					checker=new GTDoubleChecker(wrapChecker.getRegisterAddress(), wrapChecker.getTimeDelay(), wrapChecker.getAlarmMessage(), Double.parseDouble(wrapChecker.getControlValue()),3);
				}else if(wrapChecker.getValueCondition().equals(AlarmCheckerManager.conditionLE)){
					checker=new LEDoubleChecker(wrapChecker.getRegisterAddress(), wrapChecker.getTimeDelay(), wrapChecker.getAlarmMessage(), Double.parseDouble(wrapChecker.getControlValue()),3);
				}else if(wrapChecker.getValueCondition().equals(AlarmCheckerManager.conditionLT)){
					checker=new LTDoubleChecker(wrapChecker.getRegisterAddress(), wrapChecker.getTimeDelay(), wrapChecker.getAlarmMessage(), Double.parseDouble(wrapChecker.getControlValue()),3);
				}
			}else if(wrapChecker.getValuePresentation().equals(AlarmCheckerManager.presentationString)){
				if(wrapChecker.getValueCondition().equals(AlarmCheckerManager.conditionEQ)){
					checker=new EQStringChecker(wrapChecker.getRegisterAddress(), wrapChecker.getTimeDelay(), wrapChecker.getAlarmMessage(), wrapChecker.getControlValue(),true);
				}else if(wrapChecker.getValueCondition().equals(AlarmCheckerManager.conditionNE)){
					checker=new NEStringChecker(wrapChecker.getRegisterAddress(), wrapChecker.getTimeDelay(), wrapChecker.getAlarmMessage(), wrapChecker.getControlValue(),true);
				}
			}else if(wrapChecker.getValuePresentation().equals(AlarmCheckerManager.presentationZeroOne)){
				if(wrapChecker.getValueCondition().equals(AlarmCheckerManager.conditionEQ)){
					checker=new EQStringChecker(wrapChecker.getRegisterAddress(), wrapChecker.getTimeDelay(), wrapChecker.getAlarmMessage(), wrapChecker.getControlValue(),true);
				}else if(wrapChecker.getValueCondition().equals(AlarmCheckerManager.conditionNE)){
					checker=new NEStringChecker(wrapChecker.getRegisterAddress(), wrapChecker.getTimeDelay(), wrapChecker.getAlarmMessage(), wrapChecker.getControlValue(),true);
				}
			}
			if(checker==null){
				throw new Exception("Checker for recieved parameter was not found ");
			}
			
			// сохранить module_alarm_checker
				// получить путь к хранилищу
			String path=getPathToStorage(XmlData.pathAlarmChecker);
				// сохранить объект в хранилище и получить его имя ( имя файла в каталоге )
			String fileName=this.getUniqueName("ca", ".bin");
			if(this.saveObjectIntoFile(path+fileName, checker)==false){
				throw new Exception("Save checker to storage Error");
			}
			ConnectWrap connector=StaticConnector.getConnectWrap();
			try{
				Date timeWrite=new Date();
				if(wrapChecker.getIdEdit()==null){
					// Add
					// создать Checker который будет являтся отображением в базе ( и на модуле в виде checker-a)
					ModuleAlarmCheckerWrap moduleAlarmChecker=new ModuleAlarmCheckerWrap();
					moduleAlarmChecker.setIdModule(wrapChecker.getIdModule());
					moduleAlarmChecker.setIdState(0);
					moduleAlarmChecker.setIdStorage(fileName);
					moduleAlarmChecker.setTimeWrite(timeWrite);
					moduleAlarmChecker.setDescription(wrapChecker.getAlarmDescription());
					moduleAlarmChecker.setSensorModbusAddress(wrapChecker.getModbusAddress());
					moduleAlarmChecker.setSensorRegisterAddress(wrapChecker.getRegisterAddress()); // если таковой имеет место 
					moduleAlarmChecker.setSensorModbusIdOnDevice(null);

					Session session=connector.getSession();
					session.beginTransaction();
					session.save(moduleAlarmChecker);
					
					// добавить задачу в базу (сигнал модулю о необходимости получения нового Checker-a)
					TaskDataAlarmCheckerAdd task=new TaskDataAlarmCheckerAdd(wrapChecker.getModbusAddress(),
																			 wrapChecker.getRegisterAddress(), 
																			 moduleAlarmChecker.getId());
					String taskFileName=task.saveXmlAsFile(task.getPathToStorage(XmlData.pathTask));
					ModuleTaskWrap moduleTask=new ModuleTaskWrap();
					moduleTask.setIdModule(wrapChecker.getIdModule());
					moduleTask.setIdResult(0);
					moduleTask.setIdState(0);
					moduleTask.setIdStorage(taskFileName);
					moduleTask.setTimeWrite(timeWrite);
					
					session.save(moduleTask);
					session.getTransaction().commit();
				}else{
					// Edit
					// создать Checker который будет являтся отображением в базе ( и на модуле в виде checker-a)
					Session session=connector.getSession();
					ModuleAlarmCheckerWrap databaseChecker=(ModuleAlarmCheckerWrap)session.createCriteria(ModuleAlarmCheckerWrap.class)
																						  .add(Restrictions.eq("id", wrapChecker.getIdEdit()))
																						  .add(Restrictions.eq("idModule", wrapChecker.getIdModule()))
																						  .add(Restrictions.eq("sensorModbusAddress", wrapChecker.getModbusAddress()))
																						  .uniqueResult();
					if(databaseChecker==null){
						throw new Exception("Edit AlarmChecker Exception, object is not found "+wrapChecker.getIdEdit());
					}
					//databaseChecker.setIdModule(wrapChecker.getIdModule());
					databaseChecker.setIdState(0);
					databaseChecker.setIdStorage(fileName);
					databaseChecker.setTimeWrite(timeWrite);
					databaseChecker.setDescription(wrapChecker.getAlarmDescription());
					// databaseChecker.setSensorModbusAddress(wrapChecker.getModbusAddress());
					databaseChecker.setSensorRegisterAddress(wrapChecker.getRegisterAddress()); // если таковой имеет место 
					databaseChecker.setSensorModbusIdOnDevice(null);
					
					session.beginTransaction();
					session.update(databaseChecker);
					
					// добавить задачу в базу (сигнал модулю о необходимости получения нового Checker-a)
					TaskDataAlarmCheckerReplace task=new TaskDataAlarmCheckerReplace(wrapChecker.getModbusAddress(),
																			 wrapChecker.getRegisterAddress(),
																			 databaseChecker.getId(),
																			 databaseChecker.getId());
					String taskFileName=task.saveXmlAsFile(task.getPathToStorage(XmlData.pathTask));
					ModuleTaskWrap moduleTask=new ModuleTaskWrap();
					moduleTask.setIdModule(wrapChecker.getIdModule());
					moduleTask.setIdResult(0);
					moduleTask.setIdState(0);
					moduleTask.setIdStorage(taskFileName);
					moduleTask.setTimeWrite(timeWrite);
					
					session.save(moduleTask);
					session.getTransaction().commit();
				}
			}finally{
				try{
					connector.close();
				}catch(Exception ex){};
			}
			return null;
		}catch(Exception ex){
			return ex.getMessage();
		}
		
	}

	
	/** символы, которые будут использоваться для составления уникального имени файла */
	private final static char[] uniqueChar=new char[]{'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
	private Random random=new Random();
	private SimpleDateFormat sdf=new SimpleDateFormat("yyyy_MM_dd-HH_mm_ss");
	/** сгенерировать уникальную последовательность  */
	private String generateUniqueSiquence(int size){
		StringBuffer returnValue=new StringBuffer();
		for(int counter=0;counter<size;counter++){
			returnValue.append(uniqueChar[random.nextInt(uniqueChar.length)]);
		}
		return returnValue.toString();
	}
	
	/** генерация нового имени для файла с уникальным суффиксом */
	private String getUniqueName(String prefix, String suffix){
		return prefix+this.sdf.format(new Date())+"_"+this.generateUniqueSiquence(4)+suffix;
	}
	
	/** получить полный путь к директории на основании синонима */
	private String getPathToStorage(String synonim){
		String returnValue=null;
		ConnectWrap connector=StaticConnector.getConnectWrap();
		try{
			returnValue=(String) ((ModuleStorage)connector.getSession().createCriteria(ModuleStorage.class).add(Restrictions.eq("name", synonim)).uniqueResult()).getDirectory().trim();
			if(returnValue.endsWith(fileSeparator)==false){
				returnValue=returnValue+fileSeparator;
			}
		}catch(Exception ex){
			System.err.println("Checker#getPathToStorage Exception: "+ex.getMessage());
		}finally{
			connector.close();
		}
		return returnValue;
	}

	/** сохранить объект в файле
	 * @param pathToFile - полный путь к файлу
	 * @param object - объект, который должен быть сохранен (must implement java.io.Serializable)
	 * @return
	 * <li><b>true</b> сохранение успешно завершено </li>
	 * <li><b>false</b> ошибка сохранения данных </li>
	 */
	private boolean saveObjectIntoFile(String pathToFile, Object object){
		boolean returnValue=false;
		ObjectOutputStream fos=null;
		try{
			fos=new ObjectOutputStream(new FileOutputStream(pathToFile));
			fos.writeObject(object);
			fos.flush();
			return true;
		}catch(Exception ex){
			System.err.println("writeObjectToFile Exception: "+ex.getMessage());
		}finally{
			try{
				fos.close();
			}catch(Exception ex){};
		}
		return returnValue;
	}

	/** 
	 * прочесть из файла объект
	 * @param pathToFile - полный путь к файлу 
	 * @return объект, прочитанный из файла
	 */
	private Object loadObjectFromFile(String pathToFile){
		Object returnValue=null;
		ObjectInputStream fis=null;
		try{
			fis=new ObjectInputStream(new FileInputStream(pathToFile));
			returnValue=fis.readObject();
		}catch(Exception ex){
			System.err.println("loadObjectFromFile Exception: "+ex.getMessage());
		}finally{
			try{
				fis.close();
			}catch(Exception ex){};
		}
		return returnValue;
	}

	/** 
	 * {@inheritDoc}
	 */
	@Override
	public AlarmCheckerTableElement[] getListOfAlarmChecker(int idModule,int modbusAddress) {
		AlarmCheckerTableElement[] returnValue=null;
		ConnectWrap connector=StaticConnector.getConnectWrap();
		try{
			Session session=connector.getSession();
			@SuppressWarnings("unchecked")
			List<ModuleAlarmCheckerWrap> list=(List<ModuleAlarmCheckerWrap>)session.createCriteria(ModuleAlarmCheckerWrap.class)
						.add(Restrictions.eq("idModule", new Integer(idModule)))
						.add(Restrictions.eq("sensorModbusAddress", new Integer(modbusAddress)))
						.addOrder(Order.asc("id"))
						.list();
			returnValue=new AlarmCheckerTableElement[list.size()];
			for(int counter=0;counter<list.size();counter++){
				ModuleAlarmCheckerWrap element=list.get(counter);
				returnValue[counter]=new AlarmCheckerTableElement(element.getId(),
																  element.getIdModule(),
																  element.getSensorModbusAddress(),
																  element.getSensorRegisterAddress(),
																  element.getSensorModbusIdOnDevice(),
																  element.getDescription(),
																  element.getIdState(),
																  element.getTimeWrite()
																  );
			}
		}catch(Exception ex){
			System.err.println("AlarmCheckerManagerImpl#getListOfAlarmChecker: "+ex.getMessage());
		}finally{
			connector.close();
		}
		return returnValue;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String removeAlarmChecker(int idModule, 
									 int modbusAddress,
									 int registerAddress,
									 int idChecker) {
		String returnValue=null;
		ConnectWrap connector=StaticConnector.getConnectWrap();
		try{
			// получить данный Checker
			Session session=connector.getSession();
			ModuleAlarmCheckerWrap checker=(ModuleAlarmCheckerWrap)session.createCriteria(ModuleAlarmCheckerWrap.class)
						.add(Restrictions.eq("idModule",new Integer(idModule)))
						.add(Restrictions.eq("sensorModbusAddress",new Integer(modbusAddress)))
						.add(Restrictions.eq("id",new Integer(idChecker))).uniqueResult();
			if(checker!=null){
				if(checker.getIdStorage()!=null){
					// удалить хранилище, которое зарегестрировано по данному объекту 
					String directory=this.getPathToStorage(XmlData.pathAlarmChecker);
					File file=new File(directory+checker.getIdStorage());
					if(file.delete()==false){
						throw new Exception("Ошибка удаления данных из хранилища по файлу:"+directory+checker.getIdStorage());
					}
				}
				session.beginTransaction();
				int checkerIdentifier=checker.getId();
				// удалить запись
				session.delete(checker);
				// положить Task в базу как уведомление об удалении с модуля данного Checker-a
				TaskData data=new TaskDataAlarmCheckerRemove(modbusAddress, registerAddress, checkerIdentifier);
				String fileName=data.saveXmlAsFile(data.getPathToStorage(XmlData.pathTask));
				ModuleTaskWrap task=new ModuleTaskWrap();
				task.setIdModule(idModule);
				task.setIdResult(0);
				task.setIdState(0);
				task.setIdStorage(fileName);
				task.setTimeWrite(new Date());
				session.save(task);
				session.getTransaction().commit();
				returnValue=null;
			}else{
				throw new Exception("module_alarm_checker.id="+idChecker+"   IdModule:"+idModule+"   ModbusAddress:"+modbusAddress+"   was not found");
			}
		}catch(Exception ex){
			returnValue=ex.getMessage();
			System.err.println("AlarmCheckerManagerImpl#removeAlarmChecker: Exception:"+returnValue);
		}finally{
			connector.close();
		}
		return returnValue;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int[] getListOfRegisterAddress(int idModule, int idSensor) {
		// TODO Controller2 получить адреса доступных регистров по указанному модулю 
		return null;
	}

	@Override
	public WrapChecker getAlarmChecker(Integer id) {
		WrapChecker returnValue=null;
		ConnectWrap connector=StaticConnector.getConnectWrap();
		try{
			Session session=connector.getSession();
			// получить объект из базы данных 
			ModuleAlarmCheckerWrap databaseChecker=(ModuleAlarmCheckerWrap)session.get(ModuleAlarmCheckerWrap.class,id);
			if(databaseChecker==null){
				throw new Exception("Edit AlarmChecker Exception, object is not found "+id);
			}
			// прочесть объект с диска
			String directory=this.getPathToStorage(XmlData.pathAlarmChecker);
			Object object=this.loadObjectFromFile(directory+databaseChecker.getIdStorage());
			if(object!=null){
				if(object instanceof EQIntegerChecker){
					returnValue=new WrapChecker();
					returnValue.setIdEdit(id);
					returnValue.setIdModule(databaseChecker.getIdModule());
					returnValue.setModbusAddress(databaseChecker.getSensorModbusAddress());
					returnValue.setRegisterAddress(databaseChecker.getSensorRegisterAddress());
					
					EQIntegerChecker checker=(EQIntegerChecker)object;
					returnValue.setAlarmDescription(databaseChecker.getDescription());
					returnValue.setValuePresentation(AlarmCheckerManager.presentationInteger);
					returnValue.setValueCondition(AlarmCheckerManager.conditionEQ);
					
					returnValue.setAlarmMessage(checker.getTextMessage());
					returnValue.setTimeDelay(checker.getTimeDelay());
					returnValue.setControlValue(Integer.toString(checker.getControlValue()));
				}
				if(object instanceof NEIntegerChecker){
					returnValue=new WrapChecker();
					returnValue.setIdEdit(id);
					returnValue.setIdModule(databaseChecker.getIdModule());
					returnValue.setModbusAddress(databaseChecker.getSensorModbusAddress());
					returnValue.setRegisterAddress(databaseChecker.getSensorRegisterAddress());
					
					NEIntegerChecker checker=(NEIntegerChecker)object;
					returnValue.setAlarmDescription(databaseChecker.getDescription());
					returnValue.setValuePresentation(AlarmCheckerManager.presentationInteger);
					returnValue.setValueCondition(AlarmCheckerManager.conditionNE);
					
					returnValue.setAlarmMessage(checker.getTextMessage());
					returnValue.setTimeDelay(checker.getTimeDelay());
					returnValue.setControlValue(Integer.toString(checker.getControlValue()));
				}
				if(object instanceof GEIntegerChecker){
					returnValue=new WrapChecker();
					returnValue.setIdEdit(id);
					returnValue.setIdModule(databaseChecker.getIdModule());
					returnValue.setModbusAddress(databaseChecker.getSensorModbusAddress());
					returnValue.setRegisterAddress(databaseChecker.getSensorRegisterAddress());
					
					GEIntegerChecker checker=(GEIntegerChecker)object;
					returnValue.setAlarmDescription(databaseChecker.getDescription());
					returnValue.setValuePresentation(AlarmCheckerManager.presentationInteger);
					returnValue.setValueCondition(AlarmCheckerManager.conditionGE);
					
					returnValue.setAlarmMessage(checker.getTextMessage());
					returnValue.setTimeDelay(checker.getTimeDelay());
					returnValue.setControlValue(Integer.toString(checker.getControlValue()));
				}
				if(object instanceof GTIntegerChecker){
					returnValue=new WrapChecker();
					returnValue.setIdEdit(id);
					returnValue.setIdModule(databaseChecker.getIdModule());
					returnValue.setModbusAddress(databaseChecker.getSensorModbusAddress());
					returnValue.setRegisterAddress(databaseChecker.getSensorRegisterAddress());
					
					GTIntegerChecker checker=(GTIntegerChecker)object;
					returnValue.setAlarmDescription(databaseChecker.getDescription());
					returnValue.setValuePresentation(AlarmCheckerManager.presentationInteger);
					returnValue.setValueCondition(AlarmCheckerManager.conditionGT);
					
					returnValue.setAlarmMessage(checker.getTextMessage());
					returnValue.setTimeDelay(checker.getTimeDelay());
					returnValue.setControlValue(Integer.toString(checker.getControlValue()));
				}
				if(object instanceof LEIntegerChecker){
					returnValue=new WrapChecker();
					returnValue.setIdEdit(id);
					returnValue.setIdModule(databaseChecker.getIdModule());
					returnValue.setModbusAddress(databaseChecker.getSensorModbusAddress());
					returnValue.setRegisterAddress(databaseChecker.getSensorRegisterAddress());
					
					LEIntegerChecker checker=(LEIntegerChecker)object;
					returnValue.setAlarmDescription(databaseChecker.getDescription());
					returnValue.setValuePresentation(AlarmCheckerManager.presentationInteger);
					returnValue.setValueCondition(AlarmCheckerManager.conditionLE);
					
					returnValue.setAlarmMessage(checker.getTextMessage());
					returnValue.setTimeDelay(checker.getTimeDelay());
					returnValue.setControlValue(Integer.toString(checker.getControlValue()));
				}
				if(object instanceof LTIntegerChecker){
					returnValue=new WrapChecker();
					returnValue.setIdEdit(id);
					returnValue.setIdModule(databaseChecker.getIdModule());
					returnValue.setModbusAddress(databaseChecker.getSensorModbusAddress());
					returnValue.setRegisterAddress(databaseChecker.getSensorRegisterAddress());
					
					LTIntegerChecker checker=(LTIntegerChecker)object;
					returnValue.setAlarmDescription(databaseChecker.getDescription());
					returnValue.setValuePresentation(AlarmCheckerManager.presentationInteger);
					returnValue.setValueCondition(AlarmCheckerManager.conditionLT);
					
					returnValue.setAlarmMessage(checker.getTextMessage());
					returnValue.setTimeDelay(checker.getTimeDelay());
					returnValue.setControlValue(Integer.toString(checker.getControlValue()));
				}
				
				// Double
				if(object instanceof EQDoubleChecker){
					returnValue=new WrapChecker();
					returnValue.setIdEdit(id);
					returnValue.setIdModule(databaseChecker.getIdModule());
					returnValue.setModbusAddress(databaseChecker.getSensorModbusAddress());
					returnValue.setRegisterAddress(databaseChecker.getSensorRegisterAddress());
					
					EQDoubleChecker checker=(EQDoubleChecker)object;
					returnValue.setAlarmDescription(databaseChecker.getDescription());
					returnValue.setValuePresentation(AlarmCheckerManager.presentationDouble);
					returnValue.setValueCondition(AlarmCheckerManager.conditionEQ);
					
					returnValue.setAlarmMessage(checker.getTextMessage());
					returnValue.setTimeDelay(checker.getTimeDelay());
					returnValue.setControlValue(Double.toString(checker.getControlValue()));
				}
				if(object instanceof NEDoubleChecker){
					returnValue=new WrapChecker();
					returnValue.setIdEdit(id);
					returnValue.setIdModule(databaseChecker.getIdModule());
					returnValue.setModbusAddress(databaseChecker.getSensorModbusAddress());
					returnValue.setRegisterAddress(databaseChecker.getSensorRegisterAddress());
					
					NEDoubleChecker checker=(NEDoubleChecker)object;
					returnValue.setAlarmDescription(databaseChecker.getDescription());
					returnValue.setValuePresentation(AlarmCheckerManager.presentationDouble);
					returnValue.setValueCondition(AlarmCheckerManager.conditionNE);
					
					returnValue.setAlarmMessage(checker.getTextMessage());
					returnValue.setTimeDelay(checker.getTimeDelay());
					returnValue.setControlValue(Double.toString(checker.getControlValue()));
				}
				if(object instanceof GEDoubleChecker){
					returnValue=new WrapChecker();
					returnValue.setIdEdit(id);
					returnValue.setIdModule(databaseChecker.getIdModule());
					returnValue.setModbusAddress(databaseChecker.getSensorModbusAddress());
					returnValue.setRegisterAddress(databaseChecker.getSensorRegisterAddress());
					
					GEDoubleChecker checker=(GEDoubleChecker)object;
					returnValue.setAlarmDescription(databaseChecker.getDescription());
					returnValue.setValuePresentation(AlarmCheckerManager.presentationDouble);
					returnValue.setValueCondition(AlarmCheckerManager.conditionGE);
					
					returnValue.setAlarmMessage(checker.getTextMessage());
					returnValue.setTimeDelay(checker.getTimeDelay());
					returnValue.setControlValue(Double.toString(checker.getControlValue()));
				}
				if(object instanceof GTDoubleChecker){
					returnValue=new WrapChecker();
					returnValue.setIdEdit(id);
					returnValue.setIdModule(databaseChecker.getIdModule());
					returnValue.setModbusAddress(databaseChecker.getSensorModbusAddress());
					returnValue.setRegisterAddress(databaseChecker.getSensorRegisterAddress());
					
					GTDoubleChecker checker=(GTDoubleChecker)object;
					returnValue.setAlarmDescription(databaseChecker.getDescription());
					returnValue.setValuePresentation(AlarmCheckerManager.presentationDouble);
					returnValue.setValueCondition(AlarmCheckerManager.conditionGT);
					
					returnValue.setAlarmMessage(checker.getTextMessage());
					returnValue.setTimeDelay(checker.getTimeDelay());
					returnValue.setControlValue(Double.toString(checker.getControlValue()));
				}
				if(object instanceof LEDoubleChecker){
					returnValue=new WrapChecker();
					returnValue.setIdEdit(id);
					returnValue.setIdModule(databaseChecker.getIdModule());
					returnValue.setModbusAddress(databaseChecker.getSensorModbusAddress());
					returnValue.setRegisterAddress(databaseChecker.getSensorRegisterAddress());
					
					LEDoubleChecker checker=(LEDoubleChecker)object;
					returnValue.setAlarmDescription(databaseChecker.getDescription());
					returnValue.setValuePresentation(AlarmCheckerManager.presentationDouble);
					returnValue.setValueCondition(AlarmCheckerManager.conditionLE);
					
					returnValue.setAlarmMessage(checker.getTextMessage());
					returnValue.setTimeDelay(checker.getTimeDelay());
					returnValue.setControlValue(Double.toString(checker.getControlValue()));
				}
				if(object instanceof LTDoubleChecker){
					returnValue=new WrapChecker();
					returnValue.setIdEdit(id);
					returnValue.setIdModule(databaseChecker.getIdModule());
					returnValue.setModbusAddress(databaseChecker.getSensorModbusAddress());
					returnValue.setRegisterAddress(databaseChecker.getSensorRegisterAddress());
					
					LTDoubleChecker checker=(LTDoubleChecker)object;
					returnValue.setAlarmDescription(databaseChecker.getDescription());
					returnValue.setValuePresentation(AlarmCheckerManager.presentationDouble);
					returnValue.setValueCondition(AlarmCheckerManager.conditionLT);
					
					returnValue.setAlarmMessage(checker.getTextMessage());
					returnValue.setTimeDelay(checker.getTimeDelay());
					returnValue.setControlValue(Double.toString(checker.getControlValue()));
				}

				// String 
				if(object instanceof EQStringChecker){
					returnValue=new WrapChecker();
					returnValue.setIdEdit(id);
					returnValue.setIdModule(databaseChecker.getIdModule());
					returnValue.setModbusAddress(databaseChecker.getSensorModbusAddress());
					returnValue.setRegisterAddress(databaseChecker.getSensorRegisterAddress());
					
					EQStringChecker checker=(EQStringChecker)object;
					returnValue.setAlarmDescription(databaseChecker.getDescription());
					returnValue.setValuePresentation(AlarmCheckerManager.presentationString);
					returnValue.setValueCondition(AlarmCheckerManager.conditionEQ);
					
					returnValue.setAlarmMessage(checker.getTextMessage());
					returnValue.setTimeDelay(checker.getTimeDelay());
					returnValue.setControlValue(checker.getControlValue());
				}
				if(object instanceof NEStringChecker){
					returnValue=new WrapChecker();
					returnValue.setIdEdit(id);
					returnValue.setIdModule(databaseChecker.getIdModule());
					returnValue.setModbusAddress(databaseChecker.getSensorModbusAddress());
					returnValue.setRegisterAddress(databaseChecker.getSensorRegisterAddress());
					
					NEStringChecker checker=(NEStringChecker)object;
					returnValue.setAlarmDescription(databaseChecker.getDescription());
					returnValue.setValuePresentation(AlarmCheckerManager.presentationString);
					returnValue.setValueCondition(AlarmCheckerManager.conditionNE);
					
					returnValue.setAlarmMessage(checker.getTextMessage());
					returnValue.setTimeDelay(checker.getTimeDelay());
					returnValue.setControlValue(checker.getControlValue());
				}
			}
			// заполнить объект для передачи удаленном модулю
		}catch(Exception ex){
			System.err.println("AlarmCheckerManagerImpl#getAlarmChecker Exception:"+ex.getMessage());
		}finally{
			connector.close();
		}
		return returnValue;
	}
}
