package fenomen.server.controller.client.view.modules;

/** интерфейс, который "говорит" о необходимости обновления данных */
public interface IUpdateNotify {
	/** обновить данные */
	public void needUpdate();
}
