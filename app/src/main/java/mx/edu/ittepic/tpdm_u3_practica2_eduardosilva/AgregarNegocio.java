package mx.edu.ittepic.tpdm_u3_practica2_eduardosilva;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AgregarNegocio extends AppCompatActivity {
    EditText nombre, domicilio, telefono;
    Button guardar, actualizar, eliminar;
    FirebaseFirestore bd;
    Map<String, Object> datos;
    int op;
    String cl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_negocio);

        nombre = findViewById(R.id.nombre);
        domicilio = findViewById(R.id.domicilio);
        telefono = findViewById(R.id.telefono);
        guardar = findViewById(R.id.guardar);
        actualizar = findViewById(R.id.actualizar);
        eliminar = findViewById(R.id.eliminar);

        actualizar.setVisibility(View.INVISIBLE);
        eliminar.setVisibility(View.INVISIBLE);

        bd = FirebaseFirestore.getInstance();
        datos = new HashMap<>();
        op = Integer.parseInt(getIntent().getExtras().get("operacion").toString());
        cl = getIntent().getExtras().get("clave").toString();

        if (op == 1){
            guardar.setVisibility(View.INVISIBLE);
            actualizar.setVisibility(View.VISIBLE);
            eliminar.setVisibility(View.VISIBLE);
            cargarCampos();
        }

        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertar();
            }
        });

        actualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actualizar();
            }
        });

        eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eliminar();
            }
        });
    }

    private void cargarCampos() {
        DocumentReference negocio = bd.collection("Negocio").document(cl);
        negocio.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot resul = task.getResult();
                    String nom = resul.get("nombre").toString();
                    String dom = resul.get("domicilio").toString();
                    String tel = resul.get("telefono").toString();
                    nombre.setText(nom);
                    domicilio.setText(dom);
                    telefono.setText(tel);
                    datos.put("nombre", nom);
                    datos.put("cantidad", dom);
                    datos.put("precio", tel);
                    datos.put("id",resul.getId());
                }
                else{
                    mensaje("Error al recuperar datos");
                }
            }
        });
    }

    private void mensaje(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show();
    }

    private void eliminar() {
        bd.collection("Negocio").document(datos.get("id").toString()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mensaje("Se elimino correctamente");
                guardar.setVisibility(View.VISIBLE);
                actualizar.setVisibility(View.INVISIBLE);
                eliminar.setVisibility(View.INVISIBLE);
                limpiarCampos();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mensaje("Error al eliminar");
            }
        });
    }

    private void insertar() {
        bd.collection("Negocio").add(obtenerCampos()).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                mensaje("Se ha insertado con exito");
                limpiarCampos();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mensaje("Error al insertar");
            }
        });
    }

    private void limpiarCampos() {
        nombre.setText("");
        domicilio.setText("");
        telefono.setText("");
    }

    private void actualizar() {
        bd.collection("Negocio").document(datos.get("id").toString()).update(obtenerCampos()).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mensaje("Error al actualizar");
                guardar.setVisibility(View.VISIBLE);
                actualizar.setVisibility(View.INVISIBLE);
                eliminar.setVisibility(View.INVISIBLE);
                limpiarCampos();
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mensaje("Se actualizo correctamente");
                limpiarCampos();
                startActivity(new Intent(AgregarNegocio.this, PantallaPrincipal.class));
                finish();
            }
        });
    }

    private Map<String,Object> obtenerCampos(){
        Map<String,Object> data=new HashMap<>();
        data.put("nombre", nombre.getText().toString());
        data.put("domicilio", domicilio.getText().toString());
        data.put("telefono", telefono.getText().toString());
        return data;
    }
}
