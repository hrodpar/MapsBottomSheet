package pe.edu.tecsup.mapsbottomsheet;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    // Esta será la vista del BottomSheet, puede ser cualquier tipo de View
    private LinearLayout lnlBottomSheet;

    // Estas serán las otras vistas donde se pondrán los valores
    private TextView txtNombre;
    private TextView txtDireccion;
    private TextView txtTelefono;

    // Objeto que instancia el mapa
    private GoogleMap mMap;

    // Objeto que instancia el BottomSheet
    private BottomSheetBehavior bottomSheetBehavior;

    // Esta es la data de los establecimientos, en su caso vendría de Firebase
    private List<Establecimiento> establecimientos;

    // Este es un mapa que permitirá relacionar a los establecimientos con los marcadores
    private Map<String, Integer> marcadores = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        lnlBottomSheet = findViewById(R.id.lnlBottomSheet);
        txtNombre = findViewById(R.id.txtNombre);
        txtDireccion = findViewById(R.id.txtDireccion);
        txtTelefono = findViewById(R.id.txtTelefono);

        // Se asocia el BottomSheet a partir de la vista contenedora deseada
        bottomSheetBehavior = BottomSheetBehavior.from(lnlBottomSheet);
        // Inicia con el estado HIDDEN
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        // Se genera la data de prueba dummy
        establecimientos = generaDataPrueba();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Se recorre toda la data de establecimientos para agregar marcadores a partir
        // de la latitud y longitud de cada objeto, además se inserta en el Map la relación
        // entre el id generado del nuevo marcador con la posición del objeto en la lista al que le
        // corresponde de manera que posteriormente al hacer click en un marcador se pueda
        // identificar cuál ha sido el establecimiento seleccionado y con ello recuperar los
        // valores que se deben mostrar en las etiquetas
        for (int i = 0; i < establecimientos.size(); i++) {
            Establecimiento establecimiento = establecimientos.get(i);
            LatLng ubicacion = new LatLng(establecimiento.getLatitud(), establecimiento.getLongitud());
            Marker marcador = mMap.addMarker(
                    new MarkerOptions()
                            .position(ubicacion)
                            .title(establecimiento.getNombre()));
            // Aquí se inserta la relación en el mapa entre marcador y establecimiento
            marcadores.put(marcador.getId(), i);
        }

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                // Aquí se debe identificar qué establecimiento ha sido seleccionado
                Establecimiento establecimiento = establecimientos.get(marcadores.get(marker.getId()));
                txtNombre.setText(establecimiento.getNombre());
                txtDireccion.setText(establecimiento.getDireccion());
                txtTelefono.setText(establecimiento.getTelefono());
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                return false;
            }
        });
    }

    private List<Establecimiento> generaDataPrueba() {
        List<Establecimiento> establecimientos = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            establecimientos.add(
                    new Establecimiento(
                            i,
                            "Establecimiento " + i,
                            "Av. Javier Prado #" + i,
                            "317-3900 Anexo " + i,
                            Long.valueOf(-12 + i),
                            Long.valueOf(-77 + i)
                    ));
        }
        return establecimientos;
    }

}