package uniandes.dpoo.aerolinea.modelo;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import uniandes.dpoo.aerolinea.exceptions.VueloSobrevendidoException;
import uniandes.dpoo.aerolinea.modelo.cliente.Cliente;
import uniandes.dpoo.aerolinea.modelo.tarifas.CalculadoraTarifas;
import uniandes.dpoo.aerolinea.tiquetes.GeneradorTiquetes;
import uniandes.dpoo.aerolinea.tiquetes.Tiquete;

public class Vuelo {
	private Ruta ruta;
	private String fecha;
	private Avion avion;
	private Map<String, Tiquete> tiquetes;

	public Vuelo(Ruta ruta, String fecha, Avion avion) {
		this.ruta = ruta;
		this.fecha = fecha;
		this.avion = avion;
		this.tiquetes = new HashMap<String, Tiquete>();
	}

	public Ruta getRuta() {
		return ruta;
	}

	public String getFecha() {
		return fecha;
	}

	public Avion getAvion() {
		return avion;
	}

	public Collection<Tiquete> getTiquetes() {
		return tiquetes.values();

	}

	public int venderTiquetes(Cliente cliente, CalculadoraTarifas calculadora, int cantidad)
			throws VueloSobrevendidoException {
		int cuposDisponibles = avion.getCapacidad() - tiquetes.size();
		if (cantidad > cuposDisponibles)
			throw new VueloSobrevendidoException(this);

		int tarifa = calculadora.calcularTarifa(this, cliente);
		int total = 0;
		for (int i = 0; i < cantidad; i++) {
			Tiquete nuevoTiquete = GeneradorTiquetes.generarTiquete(this, cliente, tarifa);
			GeneradorTiquetes.registrarTiquete(nuevoTiquete);
			total += tarifa;
		}
		return total;
	}

	public void agregarTiquete(Tiquete tiquete) {
		tiquetes.put(tiquete.getCodigo(), tiquete);
	}

	@Override
    public boolean equals( Object obj )
    {
        if( this == obj )
            return true;
        if( obj == null || !( obj instanceof Vuelo ) )
            return false;
        Vuelo otro = ( Vuelo ) obj;
        return this.fecha.equals( otro.fecha ) && this.ruta.getCodigoRuta( ).equals( otro.ruta.getCodigoRuta( ) );
    }
}
