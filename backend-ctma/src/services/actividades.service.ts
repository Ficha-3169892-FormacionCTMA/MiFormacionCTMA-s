// Este archivo representa el acceso real a datos (Base de Datos)
export class ActividadesService {
  private static actividades = [
    { id: 1, titulo: 'Actividad Real', descripcion: 'Dato de DB', progreso: 0, prioridad: 'ALTA' }
  ];

  static async listar() {
    // Simula una llamada asíncrona a una DB
    return this.actividades;
  }

  static async crear(datos: any) {
    const nueva = { id: Date.now(), ...datos, progreso: 0 };
    this.actividades.push(nueva);
    return nueva;
  }
}
