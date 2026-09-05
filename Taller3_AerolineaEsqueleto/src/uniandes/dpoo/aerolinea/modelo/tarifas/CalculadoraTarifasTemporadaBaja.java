package uniandes.dpoo.aerolinea.modelo.tarifas;

import uniandes.dpoo.aerolinea.modelo.Vuelo;
import uniandes.dpoo.aerolinea.modelo.cliente.Cliente;
import uniandes.dpoo.aerolinea.modelo.cliente.ClienteCorporativo;
import uniandes.dpoo.aerolinea.modelo.cliente.ClienteNatural;

public class CalculadoraTarifasTemporadaBaja extends CalculadoraTarifas
{
    protected final int COSTO_POR_KM_NATURAL = 600;
    protected final int COSTO_POR_KM_CORPORATIVO = 900;
    protected final double DESCUENTO_PEQ = 0.02;
    protected final double DESCUENTO_MEDIANAS = 0.1;
    protected final double DESCUENTO_GRANDES = 0.2;

    @Override
    protected int calcularCostoBase( Vuelo vuelo, Cliente cliente )
    {
        int distancia = calcularDistanciaVuelo( vuelo.getRuta( ) );
        if( ClienteNatural.NATURAL.equals( cliente.getTipoCliente( ) ) )
            return COSTO_POR_KM_NATURAL * distancia;
        else
            return COSTO_POR_KM_CORPORATIVO * distancia;
    }

    @Override
    protected double calcularPorcentajeDescuento( Cliente cliente )
    {
        if( ClienteCorporativo.CORPORATIVO.equals( cliente.getTipoCliente( ) ) )
        {
            ClienteCorporativo corporativo = ( ClienteCorporativo ) cliente;
            int tamano = corporativo.getTamanoEmpresa( );
            if( tamano == ClienteCorporativo.GRANDE )
                return DESCUENTO_GRANDES;
            else if( tamano == ClienteCorporativo.MEDIANA )
                return DESCUENTO_MEDIANAS;
            else if( tamano == ClienteCorporativo.PEQUENA )
                return DESCUENTO_PEQ;
        }
        return 0;
    }
}
