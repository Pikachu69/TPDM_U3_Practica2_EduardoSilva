package mx.edu.ittepic.tpdm_u3_practica2_eduardosilva;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    EditText email, pass;
    Button entrar, registrar;
    FirebaseAuth user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        email = findViewById(R.id.email);
        pass = findViewById(R.id.pass);
        entrar = findViewById(R.id.entrar);
        registrar = findViewById(R.id.registrar);

        user = FirebaseAuth.getInstance();

        entrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String u = email.getText().toString();
                String p = pass.getText().toString();
                if (u.equals("") || p.equals("")){
                    Toast.makeText(MainActivity.this,"Escriba un correo y una contraseña",Toast.LENGTH_SHORT).show();
                } else iniciarSesion(u,p);
            }
        });

        registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String u = email.getText().toString();
                String p = pass.getText().toString();
                if (u.equals("") || p.equals("")){
                    Toast.makeText(MainActivity.this,"Escriba un correo y una contraseña",Toast.LENGTH_SHORT).show();
                } else registrarUsuario(u,p);
            }
        });


    }

    private void iniciarSesion(String u, String p) {
        user.signInWithEmailAndPassword(u,p).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    if (user.getCurrentUser().isEmailVerified()) {
                        startActivity(new Intent(MainActivity.this, PantallaPrincipal.class));
                    } else {
                        AlertDialog.Builder alerta = new AlertDialog.Builder(MainActivity.this);
                        alerta.setTitle("Fallo de autenticacion")
                                .setMessage("Verifique su correo, ¿Desea reenviar correo de verificaion?")
                                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        user.getCurrentUser().sendEmailVerification();
                                        AlertDialog.Builder alerta = new AlertDialog.Builder(MainActivity.this);
                                        alerta.setTitle("Enviado")
                                                .setMessage("Se a enviado su correo de verificacion")
                                                .show();
                                    }
                                }). setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        }).show();
                    }
                } else {
                    AlertDialog.Builder alerta = new AlertDialog.Builder(MainActivity.this);
                    alerta.setTitle("Fallo de autenticacion")
                            .setMessage("Usuario o contraseña invalidos")
                            .show();
                }
            }
        });
    }

    private void registrarUsuario(String u, String p) {
        user.createUserWithEmailAndPassword(u,p).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    user.getCurrentUser().sendEmailVerification();
                    AlertDialog.Builder alerta = new AlertDialog.Builder(MainActivity.this);
                    alerta.setTitle("Registrado")
                            .setMessage("Se a enviado un correo de verificacion, verifique su correo e inicie sesion!").show();
                } else {
                    AlertDialog.Builder alerta = new AlertDialog.Builder(MainActivity.this);
                    alerta.setTitle("Error")
                            .setMessage("Se a producido un error, es posible que el usuario ya este registrado").show();
                }
            }
        });
    }
}
