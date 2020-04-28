package com.example.readyrescuecus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CustomerRegisterActivity extends AppCompatActivity {
    private EditText mEmail, mPassword, mName, mPhone;
    private Button mLogin, mRegister;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_register);

        mAuth = FirebaseAuth.getInstance();

        firebaseAuthListner = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user!=null){
                    Intent intent = new Intent(CustomerRegisterActivity.this, CustomerServiceActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                }
            }
        };

        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        mName = findViewById(R.id.name);
        mPhone = findViewById(R.id.phone_number);

        mLogin = findViewById(R.id.login);
        mRegister = findViewById(R.id.register);

        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = mEmail.getText().toString();
                final String password = mPassword.getText().toString();
                final String name = mName.getText().toString();
                final String phone = mPhone.getText().toString();
                if (email.isEmpty())
                    Toast.makeText(CustomerRegisterActivity.this,"Email required", Toast.LENGTH_SHORT).show();
                if (password.isEmpty())
                    Toast.makeText(CustomerRegisterActivity.this,"Password required", Toast.LENGTH_SHORT).show();
                if (name.isEmpty())
                    Toast.makeText(CustomerRegisterActivity.this,"Name required", Toast.LENGTH_SHORT).show();
                if (phone.isEmpty())
                    Toast.makeText(CustomerRegisterActivity.this,"Phone Number required", Toast.LENGTH_SHORT).show();

                if (!email.isEmpty()&&!password.isEmpty()&&!name.isEmpty()&&!phone.isEmpty()){
                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(CustomerRegisterActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(!task.isSuccessful()){
                                Toast.makeText(CustomerRegisterActivity.this,"sign up error", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                String user_id = mAuth.getCurrentUser().getUid();
                                DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(user_id);
                                current_user_db.setValue(true);
                                DatabaseReference userName = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(user_id).child("Name");
                                userName.setValue(name);
                                DatabaseReference number = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(user_id).child("Phone");
                                number.setValue(phone);
                                DatabaseReference points = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(user_id).child("Points");
                                number.setValue(0);
                            }
                        }
                    });
                }
            }
        });

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CustomerRegisterActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(firebaseAuthListner);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(firebaseAuthListner);
    }
}
