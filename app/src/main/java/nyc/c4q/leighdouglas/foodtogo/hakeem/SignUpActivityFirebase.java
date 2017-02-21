package nyc.c4q.leighdouglas.foodtogo.hakeem;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import nyc.c4q.leighdouglas.foodtogo.R;

/**
 * Created by leighdouglas on 2/18/17.
 */

public class SignUpActivityFirebase extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListenerSUA;
    private final String TAG = "FireBase";
    EditText userName;
    EditText nameSignup;
    EditText password;
    EditText passwordConfirm;
    EditText emailSignup;
    EditText emailConfirm;
    Button signUpButton;
    private FirebaseDatabase myFirebaseDatabase ;
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        mAuth = FirebaseAuth.getInstance();
        myFirebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = myFirebaseDatabase.getReference("userInfo");
        databaseReference.setValue("users");

        mAuthListenerSUA = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }// ...
            }
        };
        //Read from the database
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                Log.d(TAG, "Value is: " + value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        userName = (EditText) findViewById(R.id.username_signup);
        nameSignup = (EditText) findViewById(R.id.name_signup);
        password = (EditText) findViewById(R.id.password_signup);
        passwordConfirm = (EditText) findViewById(R.id.confirm_password_signup);
        emailSignup = (EditText) findViewById(R.id.email_signup);
        emailConfirm = (EditText) findViewById(R.id.confirm_email_signup);
        signUpButton = (Button) findViewById(R.id.sign_up_button);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFormComplete()) {
                    createAccount(emailSignup.getText().toString(), password.getText().toString());
                } else {
                    Toast.makeText(getApplicationContext(), "enter usermame password and email", Toast.LENGTH_SHORT);
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListenerSUA);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListenerSUA != null) {
            mAuth.removeAuthStateListener(mAuthListenerSUA);
        }
    }

    private void createAccount(String email, String password) {
        if ((email + "").equals("") || (password + "").equals("")) {
            Toast.makeText(getApplicationContext(), "enter username and password", Toast.LENGTH_SHORT);
        } else {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());
                            if (!task.isSuccessful()) {

                                Log.d(TAG, "onComplete: " + task.getException().getMessage());
                                Toast.makeText(getApplicationContext(), "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            } else {
//
                                User user = new User(userName.getText().toString(), emailSignup.getText().toString());
                                databaseReference.child("userInfo").child(mAuth.getCurrentUser().getProviderId()).setValue(user);
                                Intent intent = new Intent(getApplicationContext(), LoginActivityFirebase.class);
                                startActivity(intent);
                            }
                        }
                    });
        }
    }

    private boolean isFormComplete() {
        boolean bool = !(userName.getText().toString() + "").equals("") &&
                !(nameSignup.getText().toString() + "").equals("") &&
                !(emailConfirm.getText().toString() + "").equals("") &&
                !(emailSignup.getText().toString() + "").equals("") &&
                !(password.getText().toString() + "").equals("") &&
                !(passwordConfirm.getText().toString() + "").equals("");
        return bool;
    }
    public void launchLogInActivity(View v){
        Intent intent = new Intent(getApplicationContext(), LoginActivityFirebase.class);
        startActivity(intent);
    }
}

