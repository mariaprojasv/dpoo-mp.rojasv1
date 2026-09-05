package uniandes.dpoo.aerolinea.persistencia;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import uniandes.dpoo.aerolinea.exceptions.AeropuertoDuplicadoException;
import uniandes.dpoo.aerolinea.exceptions.InformacionInconsistenteException;
import uniandes.dpoo.aerolinea.modelo.Aerolinea;
import uniandes.dpoo.aerolinea.modelo.Aeropuerto;
import uniandes.dpoo.aerolinea.modelo.Avion;
import uniandes.dpoo.aerolinea.modelo.Ruta;
import uniandes.dpoo.aerolinea.modelo.Vuelo;

public class PersistenciaAerolineaJson implements IPersistenciaAerolinea
{
    @Override
    public void cargarAerolinea( String archivo, Aerolinea aerolinea ) throws IOException, InformacionInconsistenteException
    {
        String jsonCompleto = new String( Files.readAllBytes( new File( archivo ).toPath( ) ) );
        JSONObject raiz = new JSONObject( jsonCompleto );

        Map<String, Aeropuerto> aeropuertos = cargarAeropuertos( raiz.getJSONArray( "aeropuertos" ) );
        cargarAviones( aerolinea, raiz.getJSONArray( "aviones" ) );
        cargarRutas( aerolinea, aeropuertos, raiz.getJSONArray( "rutas" ) );
        cargarVuelos( aerolinea, raiz.getJSONArray( "vuelos" ) );
    }

    @Override
    public void salvarAerolinea( String archivo, Aerolinea aerolinea ) throws IOException
    {
        JSONObject jobject = new JSONObject( );

        salvarAeropuertos( aerolinea, jobject );
        salvarAviones( aerolinea, jobject );
        salvarRutas( aerolinea, jobject );
        salvarVuelos( aerolinea, jobject );

        PrintWriter pw = new PrintWriter( archivo );
        jobject.write( pw, 2, 0 );
        pw.close( );
    }

    private Map<String, Aeropuerto> cargarAeropuertos( JSONArray jAeropuertos ) throws InformacionInconsistenteException
    {
        Map<String, Aeropuerto> aeropuertos = new HashMap<String, Aeropuerto>( );
        for( int i = 0; i < jAeropuertos.length( ); i++ )
        {
            JSONObject jAeropuerto = jAeropuertos.getJSONObject( i );
            String nombre = jAeropuerto.getString( "nombre" );
            String codigo = jAeropuerto.getString( "codigo" );
            String nombreCiudad = jAeropuerto.getString( "nombreCiudad" );
            double latitud = jAeropuerto.getDouble( "latitud" );
            double longitud = jAeropuerto.getDouble( "longitud" );
            try
            {
                Aeropuerto aeropuerto = new Aeropuerto( nombre, codigo, nombreCiudad, latitud, longitud );
                aeropuertos.put( codigo, aeropuerto );
            }
            catch( AeropuertoDuplicadoException e )
            {
                throw new InformacionInconsistenteException( e.getMessage( ) );
            }
        }
        return aeropuertos;
    }

    private void cargarAviones( Aerolinea aerolinea, JSONArray jAviones )
    {
        for( int i = 0; i < jAviones.length( ); i++ )
        {
            JSONObject jAvion = jAviones.getJSONObject( i );
            String nombre = jAvion.getString( "nombre" );
            int capacidad = jAvion.getInt( "capacidad" );
            aerolinea.agregarAvion( new Avion( nombre, capacidad ) );
        }
    }

    private void cargarRutas( Aerolinea aerolinea, Map<String, Aeropuerto> aeropuertos, JSONArray jRutas ) throws InformacionInconsistenteException
    {
        for( int i = 0; i < jRutas.length( ); i++ )
        {
            JSONObject jRuta = jRutas.getJSONObject( i );
            String codigoRuta = jRuta.getString( "codigoRuta" );
            String codigoOrigen = jRuta.getString( "origen" );
            String codigoDestino = jRuta.getString( "destino" );
            String horaSalida = jRuta.getString( "horaSalida" );
            String horaLlegada = jRuta.getString( "horaLlegada" );

            Aeropuerto origen = aeropuertos.get( codigoOrigen );
            if( origen == null )
                throw new InformacionInconsistenteException( "La ruta " + codigoRuta + " tiene un origen que no existe: " + codigoOrigen );
            Aeropuerto destino = aeropuertos.get( codigoDestino );
            if( destino == null )
                throw new InformacionInconsistenteException( "La ruta " + codigoRuta + " tiene un destino que no existe: " + codigoDestino );
            if( aerolinea.getRuta( codigoRuta ) != null )
                throw new InformacionInconsistenteException( "Ya existe una ruta con el código " + codigoRuta );

            aerolinea.agregarRuta( new Ruta( origen, destino, horaSalida, horaLlegada, codigoRuta ) );
        }
    }

    private void cargarVuelos( Aerolinea aerolinea, JSONArray jVuelos ) throws InformacionInconsistenteException
    {
        for( int i = 0; i < jVuelos.length( ); i++ )
        {
            JSONObject jVuelo = jVuelos.getJSONObject( i );
            String codigoRuta = jVuelo.getString( "codigoRuta" );
            String fecha = jVuelo.getString( "fecha" );
            String nombreAvion = jVuelo.getString( "nombreAvion" );
            try
            {
                aerolinea.programarVuelo( fecha, codigoRuta, nombreAvion );
            }
            catch( Exception e )
            {
                throw new InformacionInconsistenteException( "No se pudo cargar el vuelo " + codigoRuta + " del " + fecha + ": " + e.getMessage( ) );
            }
        }
    }

    private void salvarAeropuertos( Aerolinea aerolinea, JSONObject jobject )
    {
        Map<String, Aeropuerto> aeropuertos = new HashMap<String, Aeropuerto>( );
        for( Ruta ruta : aerolinea.getRutas( ) )
        {
            aeropuertos.put( ruta.getOrigen( ).getCodigo( ), ruta.getOrigen( ) );
            aeropuertos.put( ruta.getDestino( ).getCodigo( ), ruta.getDestino( ) );
        }

        JSONArray jAeropuertos = new JSONArray( );
        for( Aeropuerto aeropuerto : aeropuertos.values( ) )
        {
            JSONObject jAeropuerto = new JSONObject( );
            jAeropuerto.put( "nombre", aeropuerto.getNombre( ) );
            jAeropuerto.put( "codigo", aeropuerto.getCodigo( ) );
            jAeropuerto.put( "nombreCiudad", aeropuerto.getNombreCiudad( ) );
            jAeropuerto.put( "latitud", aeropuerto.getLatitud( ) );
            jAeropuerto.put( "longitud", aeropuerto.getLongitud( ) );
            jAeropuertos.put( jAeropuerto );
        }
        jobject.put( "aeropuertos", jAeropuertos );
    }

    private void salvarAviones( Aerolinea aerolinea, JSONObject jobject )
    {
        JSONArray jAviones = new JSONArray( );
        for( Avion avion : aerolinea.getAviones( ) )
        {
            JSONObject jAvion = new JSONObject( );
            jAvion.put( "nombre", avion.getNombre( ) );
            jAvion.put( "capacidad", avion.getCapacidad( ) );
            jAviones.put( jAvion );
        }
        jobject.put( "aviones", jAviones );
    }

    private void salvarRutas( Aerolinea aerolinea, JSONObject jobject )
    {
        JSONArray jRutas = new JSONArray( );
        for( Ruta ruta : aerolinea.getRutas( ) )
        {
            JSONObject jRuta = new JSONObject( );
            jRuta.put( "codigoRuta", ruta.getCodigoRuta( ) );
            jRuta.put( "origen", ruta.getOrigen( ).getCodigo( ) );
            jRuta.put( "destino", ruta.getDestino( ).getCodigo( ) );
            jRuta.put( "horaSalida", ruta.getHoraSalida( ) );
            jRuta.put( "horaLlegada", ruta.getHoraLlegada( ) );
            jRutas.put( jRuta );
        }
        jobject.put( "rutas", jRutas );
    }

    private void salvarVuelos( Aerolinea aerolinea, JSONObject jobject )
    {
        JSONArray jVuelos = new JSONArray( );
        for( Vuelo vuelo : aerolinea.getVuelos( ) )
        {
            JSONObject jVuelo = new JSONObject( );
            jVuelo.put( "codigoRuta", vuelo.getRuta( ).getCodigoRuta( ) );
            jVuelo.put( "fecha", vuelo.getFecha( ) );
            jVuelo.put( "nombreAvion", vuelo.getAvion( ).getNombre( ) );
            jVuelos.put( jVuelo );
        }
        jobject.put( "vuelos", jVuelos );
    }
}