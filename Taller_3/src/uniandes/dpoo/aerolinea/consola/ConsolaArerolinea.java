package uniandes.dpoo.aerolinea.consola;

import java.io.IOException;

import uniandes.dpoo.aerolinea.exceptions.InformacionInconsistenteException;
import uniandes.dpoo.aerolinea.modelo.Aerolinea;
import uniandes.dpoo.aerolinea.persistencia.CentralPersistencia;
import uniandes.dpoo.aerolinea.persistencia.TipoInvalidoException;

public class ConsolaArerolinea extends ConsolaBasica
{
    private Aerolinea unaAerolinea;

    /**
     * Corre una serie de pruebas para verificar el funcionamiento de la aplicación
     */
    public void correrAplicacion( )
    {
        try
        {
            unaAerolinea = new Aerolinea( );
            unaAerolinea.cargarAerolinea( "./datos/aerolinea.json", CentralPersistencia.JSON );
            unaAerolinea.cargarTiquetes( "./datos/tiquetes.json", CentralPersistencia.JSON );
            System.out.println( "Cargados: " + unaAerolinea.getVuelos( ).size( ) + " vuelos, " + unaAerolinea.getClientes( ).size( ) + " clientes, " + unaAerolinea.getTiquetes( ).size( ) + " tiquetes" );

            int total = unaAerolinea.venderTiquetes( "Alice", "2024-11-05", "4558", 2 );
            System.out.println( "Alice compró 2 tiquetes (temporada baja): " + total );

            total = unaAerolinea.venderTiquetes( "Boeing", "2024-12-20", "7890", 1 );
            System.out.println( "Boeing compró 1 tiquete (temporada alta): " + total );

            System.out.println( "Saldo pendiente de Alice: " + unaAerolinea.consultarSaldoPendienteCliente( "Alice" ) );
            unaAerolinea.registrarVueloRealizado( "2024-11-05", "4558" );
            System.out.println( "Saldo de Alice después del vuelo: " + unaAerolinea.consultarSaldoPendienteCliente( "Alice" ) );

            unaAerolinea.salvarAerolinea( "./datos/aerolinea_salida.json", CentralPersistencia.JSON );
            unaAerolinea.salvarTiquetes( "./datos/tiquetes_salida.json", CentralPersistencia.JSON );
            System.out.println( "Archivos salvados" );
        }
        catch( TipoInvalidoException e )
        {
            e.printStackTrace( );
        }
        catch( IOException e )
        {
            e.printStackTrace( );
        }
        catch( InformacionInconsistenteException e )
        {
            e.printStackTrace( );
        }
        catch( Exception e )
        {
            e.printStackTrace( );
        }
    }

    public static void main( String[] args )
    {
        ConsolaArerolinea ca = new ConsolaArerolinea( );
        ca.correrAplicacion( );
    }
}