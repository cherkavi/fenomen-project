package fenomen.monitor.web_service.common;

import java.io.ByteArrayInputStream;

import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/** общий класс для всех сообщений */
public abstract class XmlMessage {
	/** сообщение из таблицы monitor_event_alarm */
	public static String typeAlarm="alarm";
	/** сообщение из таблицы monitor_event_information */
	public static String typeInformation="information";
	/** сообщение из таблицы monitor_event_heart_beat */
	public static String typeHeartBeat="heart_beat";
	/** сообщение из таблицы monitor_event_restart */
	public static String typeRestart="restart";
	
	/** уникальный номер записи из таблицы базы данных (monitor_event_*) */
	private int id;
	/** тип сообщения {@link XmlMessage#typeAlarm} {@link XmlMessage#typeInformation} {@link XmlMessage#typeRestart} {@link XmlMessage#typeHeartBeat}*/
	private String type;
	/** адрес монитора в сети Jabber */
	private String jabberAddress;
	/** уникальный id из таблицы события ({@link #getType}) module_event_alarm, module_event_information, module_event_heart_beat, module_event_restart */
	private int eventId;
	/** дата и время события */
	private Date timeWrite;
	/** описание события */
	private String description;
	/** описание события */
	private String moduleName;
	/** адрес модуля */
	private String moduleAddress;
	
	/** флаг оповещения */
	private boolean flagNotify=false;
	/** флаг подтверждения */
	private boolean flagConfirm=false;
	/** флаг решения проблемы */
	private boolean flagResolve=false;
	
	/** решение проблемы код из таблицы monitor_event_resolve */
	private int resolveId;
	/** решение проблемы, свой вариант */
	private String resolveName;
	
	/** общий класс для всех сообщений */
	public XmlMessage(String type){
		this.type=type;
	}

	/** уникальный номер записи из таблицы базы данных (monitor_event_*) */
	public int getId() {
		return id;
	}

	/** уникальный номер записи из таблицы базы данных (monitor_event_*) */
	public void setId(int id) {
		this.id = id;
	}

	/** тип сообщения {@link XmlMessage#typeAlarm} {@link XmlMessage#typeInformation} {@link XmlMessage#typeRestart} {@link XmlMessage#typeHeartBeat}*/
	public String getType() {
		return type;
	}

	/** тип сообщения {@link XmlMessage#typeAlarm} {@link XmlMessage#typeInformation} {@link XmlMessage#typeRestart} {@link XmlMessage#typeHeartBeat}*/
	public void setType(String type) {
		this.type = type;
	}

	/** адрес монитора в сети Jabber */
	public String getJabberAddress() {
		return jabberAddress;
	}

	/** адрес монитора в сети Jabber */
	public void setJabberAddress(String jabberAddress) {
		this.jabberAddress = jabberAddress;
	}

	/** уникальный id из таблицы события ({@link #getType}) module_event_alarm, module_event_information, module_event_heart_beat, module_event_restart */
	public int getEventId() {
		return eventId;
	}

	/** уникальный id из таблицы события ({@link #getType}) module_event_alarm, module_event_information, module_event_heart_beat, module_event_restart */
	public void setEventId(int eventId) {
		this.eventId = eventId;
	}

	/** дата и время события */
	public Date getTimeWrite() {
		return timeWrite;
	}

	/** дата и время события */
	public void setTimeWrite(Date timeWrite) {
		this.timeWrite = timeWrite;
	}

	/** описание события */
	public String getDescription() {
		return description;
	}

	/** описание события */
	public void setDescription(String description) {
		this.description = description;
	}

	/** имя модуля */
	public String getModuleName() {
		return moduleName;
	}

	/** имя модуля */
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	/** адрес модуля */
	public String getModuleAddress() {
		return moduleAddress;
	}

	/** адрес модуля */
	public void setModuleAddress(String moduleAddress) {
		this.moduleAddress = moduleAddress;
	}
	
	
	/** добавить к родительскому элементу лист XML с простым текстовым значением 
	 * @param document - XML документ
	 * @param parent - родительский элемент
	 * @param elementName - имя элемента 
	 * @param elementValue - текстовое значение элемента
	 */
	private void appendLeafToElement(Document document, Element parent, String elementName, String elementValue){
		Element element=document.createElement(elementName);
		element.setTextContent(elementValue);
		parent.appendChild(element);
	}
	
	private String getStringFromXmlDocument(Document document){
		Writer out=null;
		try{
			javax.xml.transform.TransformerFactory transformer_factory = javax.xml.transform.TransformerFactory.newInstance();  
			javax.xml.transform.Transformer transformer = transformer_factory.newTransformer();  
			javax.xml.transform.dom.DOMSource dom_source = new javax.xml.transform.dom.DOMSource(document); // Pass in your document object here  
			out=new StringWriter();
			//string_writer = new Packages.java.io.StringWriter();  
			javax.xml.transform.stream.StreamResult stream_result = new javax.xml.transform.stream.StreamResult(out);  
			transformer.transform(dom_source, stream_result);  
		}catch(Exception ex){
			System.err.println("getStringFromXmlDocument:"+ex.getMessage());
		}
		return (out==null)?"":out.toString();
	}


	
	/** флаг оповещения */
	public boolean isFlagNotify() {
		return flagNotify;
	}

	/** флаг оповещения */
	public void setFlagNotify(boolean flagNotify) {
		this.flagNotify = flagNotify;
	}

	/** флаг подтверждения */
	public boolean isFlagConfirm() {
		return flagConfirm;
	}

	/** флаг подтверждения */
	public void setFlagConfirm(boolean flagConfirm) {
		this.flagConfirm = flagConfirm;
	}

	/** флаг решения проблемы */
	public boolean isFlagResolve() {
		return flagResolve;
	}

	/** флаг решения проблемы */
	public void setFlagResolve(boolean flagResolve) {
		this.flagResolve = flagResolve;
	}

	/** код решения проблемы из таблицы  monitor_event_resolve.id */
	public int getResolveId() {
		return resolveId;
	}

	/** код решения проблемы из таблицы  monitor_event_resolve.id, необходима установка флага {@link #setFlagResolve}*/
	public void setResolveId(int resolveId) {
		this.resolveId = resolveId;
	}

	/** решение проблемы, свой вариант  */
	public String getResolveName() {
		return resolveName;
	}

	/** решение проблемы, свой вариант, необходима установка флага {@link #setFlagResolve} */
	public void setResolveName(String resolveName) {
		this.resolveName = resolveName;
	}
	
	private static SimpleDateFormat sdf=new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");

	/** преобразовать данный объект в XML в виде строки, для передачи по каналам связи  */
	public String convertToXml() throws Exception{
		// create empty XML document
		javax.xml.parsers.DocumentBuilderFactory document_builder_factory=javax.xml.parsers.DocumentBuilderFactory.newInstance();
		document_builder_factory.setValidating(false);
		document_builder_factory.setIgnoringComments(true);
		javax.xml.parsers.DocumentBuilder document_builder=document_builder_factory.newDocumentBuilder();
		Document document=document_builder.newDocument();

		Element root=document.createElement("message");
		document.appendChild(root);
		
		this.appendLeafToElement(document, root, "id", Integer.toString(this.getId()));
		this.appendLeafToElement(document, root, "type", this.getType());
		try{
			this.appendLeafToElement(document, root, "time_write", sdf.format(this.getTimeWrite()));
		}catch(Exception ex){
			this.appendLeafToElement(document, root, "time_write", "");
		}
		this.appendLeafToElement(document, root, "description", this.getDescription());
		this.appendLeafToElement(document, root, "module_name", this.getModuleName());
		this.appendLeafToElement(document, root, "module_address", this.getModuleAddress());
		if(this.isFlagNotify()){
			Element notify=document.createElement("notify");
			root.appendChild(notify);
		} else
		if(this.isFlagConfirm()){
			Element confirm=document.createElement("confirm");
			root.appendChild(confirm);
		}else 
		if(this.isFlagResolve()){
			Element resolve=document.createElement("resolve");
			root.appendChild(resolve);
			this.appendLeafToElement(document, resolve, "id", Integer.toString(this.getResolveId()));
			if(this.resolveName!=null){
				this.appendLeafToElement(document, resolve, "name", this.getResolveName());
			}else{
				this.appendLeafToElement(document, resolve, "name", "");
			}
			
		}
		return this.getStringFromXmlDocument(document);
	}

	/** получить документ на основании текста */
	private static Document getXmlFromString(String value){
		Document return_value=null;
		javax.xml.parsers.DocumentBuilderFactory document_builder_factory=javax.xml.parsers.DocumentBuilderFactory.newInstance();
        // установить непроверяемое порождение Parser-ов
        document_builder_factory.setValidating(false);
        try {
            // получение анализатора
            javax.xml.parsers.DocumentBuilder parser=document_builder_factory.newDocumentBuilder();
            // Parse источник
            return_value=parser.parse(new ByteArrayInputStream(value.getBytes("UTF-8")));
            //return_value=parser.parse(new StringBufferInputStream(value));
        }catch(Exception ex){
        	System.err.println("XmlExchange#getXmlFromString ERROR: "+ex.getMessage());
        }
		return return_value;
	}
	
	
	/** получить подэлемент из корневого элемента на основании XPath пути
	 * @param parentElement - родительский элемент 
	 * @param path - путь XPath
	 * @return - найденный Element или null
	 */
	private static Element getSubElement(XPath xpath, Element parentElement, String path){
		try{
			Object returnValue=xpath.evaluate(path, parentElement,XPathConstants.NODE);
			if(returnValue instanceof Element){
				return (Element)returnValue;
			}else{
				return null;
			}
		}catch(Exception ex){
			return null;
		}
	}
	
	/** получить текстовое содержимое элемента, на основании родительского элемента и пути  */
	private static String getTextContent(XPath xpath, Element parentElement, String path){
		Element element=getSubElement(xpath, parentElement,path);
		if(element!=null){
			return element.getTextContent();
		}else{
			return null;
		}
	}
	
	private static void debug(Object message){
		System.out.println("XmlMessage DEBUG: "+message);
	}

	private static void error(Object message){
		System.out.println("XmlMessage ERROR: "+message);
	}
	
	/** получить {@link XmlMessage} на основании строки текста ( распарсить входящее от сервера сообщение ) 
	 * @param text - сообщение, полученное от сервера и созданное методом {@link #convertToXml()}
	 * @return
	 */
	public static XmlMessage getInstanceByMessage(String text){
		XPath xpath=XPathFactory.newInstance().newXPath();
		debug("преобразование в XML: "+text);
		Document document=getXmlFromString(text);
		if(document!=null){
			NodeList list=document.getElementsByTagName("message");
			debug("корневой элемент message найден ");
			if(list.getLength()>0){
				Element root=(Element)list.item(0);
				String type=getTextContent(xpath, root, "type");
				XmlMessage message=new XmlMessage(type){};
				Element notify=getSubElement(xpath, root, "notify");
				Element confirm=getSubElement(xpath, root, "confirm");
				Element resolve=getSubElement(xpath, root,"resolve");
				if(notify!=null){
					message.setFlagNotify(true);
					debug("оповещение о новом событии");
					try{
						debug("получить id");
						message.setId(Integer.parseInt(getTextContent(xpath, root,"id")));
						debug("получить time_write");
						try{
							message.setTimeWrite(sdf.parse(getTextContent(xpath, root,"time_write")));
						}catch(Exception ex){};
						debug("получить module_name");
						message.setModuleName(getTextContent(xpath, root,"module_name"));
						debug("получить module_address");
						message.setModuleAddress(getTextContent(xpath, root,"module_address"));
						debug("получить description");
						message.setDescription(getTextContent(xpath, root,"description"));
						return message;
					}catch(Exception ex){
						error("ошибка во время попытки чтения параметров ");
						return null;
					}
				}else if(confirm!=null){
					message.setFlagConfirm(true);
					try{
						debug("получить id");
						message.setId(Integer.parseInt(getTextContent(xpath, root,"id")));
					}catch(Exception ex){};
				}else if(resolve!=null){
					message.setFlagResolve(true);
					try{
						debug("получить id");
						message.setId(Integer.parseInt(getTextContent(xpath, root,"id")));
					}catch(Exception ex){};
					try{
						debug("получить resolveId");
						message.setResolveId(Integer.parseInt(getTextContent(xpath, resolve,"id")));
					}catch(Exception ex){};
					message.setResolveName(getTextContent(xpath, resolve,"name"));
				}else{
					debug("тип входящего сообщения не опознан");
				}
				return message;
			}else{
				debug("не найден корневой элемент message");
				return null;
			}
		}else{
			debug("ошибка преобразования текста в XML файл"); 
			return null;
		}
	}
}


