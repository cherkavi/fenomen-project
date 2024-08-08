package fenomen.module.core;

/** интерфейс, который оповещает о наличии на сервере задания для данного модуля */
public interface ITaskExistsNotifier {
	/** оповещение о наличии на сервере заданий для данного модуля */
	public void notifyTaskExists();
}
