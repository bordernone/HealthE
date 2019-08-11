package io.coderslab.yourheartbeat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import Utilities.User;
import Utilities.utils;

public class Dashboard extends AppCompatActivity {

    private String className = "Dashboard.java";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        if (User.isUserLoggedIn()) {
            utils.alertInfo("Logged in", this);

            FirebaseFirestore db = FirebaseFirestore.getInstance();

//            DocumentReference docRef = db.collection("Users").document("PPlOb7VBfaTkfPebksEMuCk1bwJ3");
//            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                @Override
//                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                    if (task.isSuccessful()) {
//                        DocumentSnapshot document = task.getResult();
//                        if (document.exists()) {
//                            utils.logInfo("DocumentSnapshot data: " + document.getData(), className);
//                        } else {
//                            utils.logInfo("No such document", className);
//                        }
//                    } else {
//                        utils.logError( "get failed with " + task.getException(), className);
//                    }
//                }
//            });

        } else {
            utils.alertError("Not logged in", this);
        }
    }
}
