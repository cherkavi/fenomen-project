package fenomen.monitor.web_service.client_implementation;

import org.codehaus.xfire.XFireFactory;

import org.codehaus.xfire.client.XFireProxyFactory;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.binding.ObjectServiceFactory;

public abstract class XFireImplementation<T> {
	private Service serviceModel;
	private String serviceUrl;
	private XFireProxyFactory factory;
	/** объект заместитель, который, реально, оборачивается данным классом  */
	private T proxy;

	/** получить класс параметризированного типа T, т.к. средствами языка получить их невозможно */
	protected abstract Class<T> getGenericClass();
	
	protected XFireImplementation(String url, String subUrl){
		String controlValue=url.trim();
		String pathToService=null;
		if(!controlValue.endsWith("/")){
			pathToService=controlValue+"/";
		}else{
			pathToService=controlValue;
		}
        //Create a metadata of the service      
        serviceModel= new ObjectServiceFactory().create(this.getGenericClass());        
        //Create a proxy for the deployed service
        factory = new XFireProxyFactory(XFireFactory.newInstance().getXFire());
        serviceUrl = pathToService+subUrl;
	}
	
	/** получить сервис */
	@SuppressWarnings("unchecked")
	private T connectToService() throws Exception {
        return (T)factory.create(serviceModel, serviceUrl);
	}
	
	/** переподсоединиться к серверу, возможно уже потерян контекст общения, но объект еще живой */
	protected void reconnectToService(){
		try{
			this.proxy=this.connectToService();
		}catch(Exception ex){
		}
	}

	/** получить объект-заместитель по общению с сервером */
	protected T getProxy(){
		if(this.proxy==null){
			try{
				this.proxy=this.connectToService();
			}catch(Exception ex){
			}
		}
		return this.proxy;
	}
}
