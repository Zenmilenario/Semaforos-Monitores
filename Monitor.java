import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class PuenteMonitor {
	private int cochesEnElPuente = 0;
	private int cochesEsperandoSur = 0;
	private int cochesEsperandoNorte = 0;

	private Lock lock = new ReentrantLock();
	private Condition puedeCruzarSur = lock.newCondition();
	private Condition puedeCruzarNorte = lock.newCondition();

	public void cruzarPuenteSur() {
		lock.lock();
		try {
			cochesEsperandoSur++;

			// Esperar si hay coches del norte cruzando o esperando
			while (cochesEnElPuente > 0 || cochesEsperandoNorte > 0) {
				puedeCruzarSur.await();
			}

			cochesEsperandoSur--;
			cochesEnElPuente++;
			System.out.println("Coche del Sur cruzando el puente.");

		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
	}

	public void salirPuenteSur() {
		lock.lock();
		try {
			cochesEnElPuente--;
			System.out.println("Coche del Sur saliendo del puente.");

			// Notificar a los coches del sur y norte que pueden intentar cruzar
			puedeCruzarSur.signalAll();
			puedeCruzarNorte.signalAll();

		} finally {
			lock.unlock();
		}
	}

	public void cruzarPuenteNorte() {
		lock.lock();
		try {
			cochesEsperandoNorte++;

			// Esperar si hay coches del sur cruzando o esperando
			while (cochesEnElPuente > 0 || cochesEsperandoSur > 0) {
				puedeCruzarNorte.await();
			}

			cochesEsperandoNorte--;
			cochesEnElPuente++;
			System.out.println("Coche del Norte cruzando el puente.");

		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
	}

	public void salirPuenteNorte() {
		lock.lock();
		try {
			cochesEnElPuente--;
			System.out.println("Coche del Norte saliendo del puente.");

			// Notificar a los coches del sur y norte que pueden intentar cruzar
			puedeCruzarSur.signalAll();
			puedeCruzarNorte.signalAll();

		} finally {
			lock.unlock();
		}
	}
}

public class Monitor {
	public static void main(String[] args) {
		PuenteMonitor puente = new PuenteMonitor();

		// Ejemplo de uso
		// Coche del Sur
		new Thread(() -> {
			puente.cruzarPuenteSur();
			// Simulación de tiempo en el puente
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			puente.salirPuenteSur();
		}).start();

		// Coche del Norte
		new Thread(() -> {
			puente.cruzarPuenteNorte();
			// Simulación de tiempo en el puente
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			puente.salirPuenteNorte();
		}).start();
	}
}
