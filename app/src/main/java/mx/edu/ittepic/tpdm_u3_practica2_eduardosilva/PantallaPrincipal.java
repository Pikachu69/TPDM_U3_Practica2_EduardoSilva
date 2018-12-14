package mx.edu.ittepic.tpdm_u3_practica2_eduardosilva;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PantallaPrincipal extends AppCompatActivity {
    FirebaseAuth user;
    ListView lista;
    FirebaseFirestore bd;
    FirebaseAuth.AuthStateListener state;
    CollectionReference negocios;
    List<Map> negociosLocal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_principal);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        bd = FirebaseFirestore.getInstance();
        lista = findViewById(R.id.lista);
        negocios = bd.collection("Negocio");
        negociosLocal = new ArrayList<>();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent v = new Intent(PantallaPrincipal.this, AgregarNegocio.class);
                v.putExtra("operacion", 0);
                v.putExtra("clave", "");
                startActivity(v);
            }
        });

        user = FirebaseAuth.getInstance();

        state = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser u = firebaseAuth.getCurrentUser();
                if (u == null || !u.isEmailVerified()) {
                    Toast.makeText(PantallaPrincipal.this, "Inicie Sesion", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(PantallaPrincipal.this, MainActivity.class));
                }
            }
        };

        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view,final int i, long l) {
                if (i >= 0) {
                    AlertDialog.Builder alerta = new AlertDialog.Builder(PantallaPrincipal.this);
                    alerta.setTitle("Atencion")
                            .setMessage("¿Desea modificar el registro " + negociosLocal.get(i).get("nombre").toString()+"?")
                            .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int j) {
                                    Intent ventana = new Intent(PantallaPrincipal.this, AgregarNegocio.class);
                                    ventana.putExtra("clave", negociosLocal.get(i).get("id").toString());
                                    ventana.putExtra("operacion",1);
                                    startActivity(ventana);
                                }
                            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    }).show();
                }
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.cerrarSesion) {
            user.signOut();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        user.addAuthStateListener(state);
        cargarDatos();
    }

    @Override
    protected void onStop() {
        super.onStop();
        user.removeAuthStateListener(state);
    }

    private void cargarDatos(){
        negocios.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if  (queryDocumentSnapshots.size()<=0){
                    mensaje("No hay datos para mostrar");
                    return;
                }
                negociosLocal=new ArrayList<>();
                for (QueryDocumentSnapshot otro:queryDocumentSnapshots){
                    Negocio negocio = otro.toObject(Negocio.class);
                    Map<String,Object> datos = new HashMap<>();
                    datos.put("nombre", negocio.getNombre());
                    datos.put("domicilio", negocio.getDomicilio());
                    datos.put("telefono", negocio.getTelefono());
                    datos.put("id",otro.getId());
                    negociosLocal.add(datos);
                    llenarLista();
                }
            }
        });
    }

    private void llenarLista(){
        String data[]=new String[negociosLocal.size()];
        for (int i=0;i<data.length;i++){
            String cad = negociosLocal.get(i).get("nombre").toString()+
                    "\nDomicilio: "+negociosLocal.get(i).get("domicilio").toString()+
                    "\nTelefono: "+negociosLocal.get(i).get("telefono").toString();
            data[i]=cad;
        }
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(PantallaPrincipal.this,android.R.layout.simple_list_item_1,data);
        lista.setAdapter(adapter);
    }

    private void mensaje(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show();
    }
}
