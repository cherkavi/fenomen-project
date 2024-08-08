package fenomen.module.core;

import fenomen.module.core.sensor.SensorContainer;

/** интерфейс, который содержит ссылки на датчики и позволяет управлять ими */
public interface ISensorContainerAware {
	/** получить контейнер с сенсорами/датчиками, которые существуют в системе  */
	public SensorContainer getSensorContainer();
}
