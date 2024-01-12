import java.util.concurrent.Semaphore;

class RecursoCompartido {
	private int unidades;

	public RecursoCompartido(int unidades) {
		this.unidades = unidades;
	}

	public void reservar(int r) {
		try {
			// Bloquear hasta que haya suficientes unidades disponibles
			// utilizando un semáforo binario
			// (semaforoDisponibilidad.acquire() decrementará el semáforo)
			System.out.println("Proceso esperando para reservar " + r + " unidades del recurso.");
			semaforoDisponibilidad.acquire();

			// También se necesita asegurar la exclusión mutua en la sección crítica
			// usando otro semáforo binario (semaforoExclusion.acquire() y
			// semaforoExclusion.release())
			semaforoExclusion.acquire();

			if (r <= unidades) {
				unidades -= r;
				System.out.println("Proceso reservó " + r + " unidades. Unidades restantes: " + unidades);
			} else {
				System.out.println("No hay suficientes unidades disponibles para la reserva.");
			}

		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			// Liberar el semáforo de exclusión mutua después de la sección crítica
			// (semaforoExclusion.release() incrementará el semáforo)
			semaforoExclusion.release();

			// Liberar el semáforo de disponibilidad después de la sección crítica
			// (semaforoDisponibilidad.release() incrementará el semáforo)
			semaforoDisponibilidad.release();
		}
	}

	public void liberar(int l) {
		try {
			// Bloquear hasta liberar las unidades utilizando un semáforo binario
			// (semaforoLiberacion.acquire() decrementará el semáforo)
			System.out.println("Proceso esperando para liberar " + l + " unidades del recurso.");
			semaforoLiberacion.acquire();

			// Sección crítica
			unidades += l;
			System.out.println("Proceso liberó " + l + " unidades. Unidades restantes: " + unidades);

		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			// Liberar el semáforo de liberación después de la sección crítica
			// (semaforoLiberacion.release() incrementará el semáforo)
			semaforoLiberacion.release();
		}
	}

	// Crear semáforos binarios adicionales según sea necesario
	private Semaphore semaforoDisponibilidad = new Semaphore(1);
	private Semaphore semaforoExclusion = new Semaphore(1);
	private Semaphore semaforoLiberacion = new Semaphore(1);
}

public class Main {
	public static void main(String[] args) {
		// Número total de unidades del recurso
		int k = 5;

		// Crear un objeto RecursoCompartido
		RecursoCompartido recurso = new RecursoCompartido(k);

		// Ejemplo de uso
		// Proceso 1
		new Thread(() -> {
			int unidadesReserva = 2;
			recurso.reservar(unidadesReserva);
		}).start();

		// Proceso 2
		new Thread(() -> {
			int unidadesLiberacion = 1;
			recurso.liberar(unidadesLiberacion);
		}).start();
	}
}
