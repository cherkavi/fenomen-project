package fenomen.module.core;

import fenomen.module.core.sensor.SensorContainer;

/** ���������, ������� �������� ������ �� ������� � ��������� ��������� ��� */
public interface ISensorContainerAware {
	/** �������� ��������� � ���������/���������, ������� ���������� � �������  */
	public SensorContainer getSensorContainer();
}
