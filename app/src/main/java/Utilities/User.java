package Utilities;

import android.content.Context;
import android.location.Location;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

import io.coderslab.yourheartbeat.R;

public class User {
    // class name
    private final String className = "User.java";

    private String phoneNumber;
    private String bloodGroup;
    private Integer bloodGroupId;
    private Location location = null;
    private String firebaseUserId;
    private FirebaseUser firebaseUser;

    // Interface
    public interface FetchUserData{
        void remoteUserFetchSuccess();
    }
    private FetchUserData fetchUserDataListener;

    public void setFetchUserDataListener(FetchUserData listenert){
        fetchUserDataListener = listenert;
    }

    // Constructors
    public User(String number, Integer id, Context _this) throws Exception {
        setPhoneNumber(number);
        setBloodGroupId(id, _this);
    }

    public User() {
    }

    public void fetchFromRemote() throws Exception {
        if (isUserLoggedIn()) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            final DocumentReference docRef = db.collection("Users")
                    .document(getFirebaseUserId());

            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            // Get location
                            if (document.contains("Location")){
                                GeoPoint geoPoint = document.getGeoPoint("Location");
                                Location fetchedLocation = new Location("");
                                fetchedLocation.setLatitude(geoPoint.getLatitude());
                                fetchedLocation.setLongitude(geoPoint.getLongitude());
                                location = fetchedLocation;
                            } else {
                                utils.logError("No location key", className);
                            }

                            // Get blood group
                            if (document.contains("BloodGroup")){
                                bloodGroup = document.get("BloodGroup").toString();
                            } else {
                                utils.logError("BloodGroup field not found in remote", className);
                            }

                            // Get phone number
                            if (getFirebaseUser().getPhoneNumber().length() > 0){
                                phoneNumber = getFirebaseUser().getPhoneNumber();
                            } else {
                                utils.logError("cannot retrieve phone number", className);
                            }

                            fetchUserDataListener.remoteUserFetchSuccess();
                        } else {
                            utils.logInfo("No such document", className);
                        }
                    } else {
                        utils.logError("Fetch failed with " + task.getException(), className);
                    }
                }
            });
        } else {
            utils.logError("Fetching from remote when user isn't logged in", className);
            throw new Exception("Trying to fetch from remote when user isn't logged in");
        }
    }

    public void setPhoneNumber(String number) {
        phoneNumber = number;
    }

    public void setBloodGroupId(Integer id, Context _this) throws Exception {
        if (isValidBloodGrpId(id, _this) == true) {
            bloodGroupId = id;
            setBloodGroup(_this);
        } else {
            utils.logError("Not a valid blood group id", className);
            throw new Exception("Not a valid blood group id");
        }
    }

    private void setBloodGroup(Context _this) {
        String[] bloodGrpArray = _this.getResources().getStringArray(R.array.blood_grp_array_reg);
        bloodGroup = bloodGrpArray[bloodGroupId];
    }

    public void setLocation(Location l) {
        location = l;
    }

    public Location getLocation() {
        return location;
    }

    public String getPhoneNumber() {
        return getCountryCode() + phoneNumber;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public String getFirebaseUserId() {
        if (firebaseUserId == null || firebaseUserId.length() <= 0) {
            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            firebaseUserId = firebaseUser.getUid();
        }
        return firebaseUserId;
    }

    public FirebaseUser getFirebaseUser(){
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        return firebaseUser;
    }

    public void saveBloodGroup(Context _this) throws Exception {
        if (isValidBloodGrpId(bloodGroupId, _this)) {
            if (isUserLoggedIn()) {
                Map<String, String> user = new HashMap<>();
                user.put("BloodGroup", getBloodGroup());

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("Users")
                        .document(getFirebaseUserId())
                        .set(user, SetOptions.merge())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                utils.logInfo("DocumentSnapshot successfully written!", className);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                utils.logError("Error writing document" + e.getMessage(), className);
                            }
                        });
            } else {
                utils.logError("Trying to save user when user not logged in", className);
                throw new Exception("Trying to save user when user not logged in");
            }
        } else {
            utils.logError("Trying to save user when invalid bloodgroupid is passed", className);
            throw new Exception("Trying to save user when invalid bloodgroupid is passed");
        }
    }

    public void saveGeoLocation() throws Exception {
        if (getLocation() != null) {
            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            firebaseUserId = firebaseUser.getUid();
            GeoPoint userLocation = new GeoPoint(getLocation().getLatitude(), getLocation().getLongitude());
            Map<String, GeoPoint> user = new HashMap<>();
            user.put("Location", userLocation);

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("Users")
                    .document(firebaseUserId)
                    .set(user, SetOptions.merge())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            utils.logInfo("DocumentSnapshot successfully written!", className);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            utils.logError("Error writing document" + e.getMessage(), className);
                        }
                    });
        } else {
            throw new Exception("Cannot save a null location");
        }
    }

    public static boolean isUserLoggedIn() {
        Boolean isLoggedIn;
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is signed in
            isLoggedIn = true;
        } else {
            // No user is signed in
            isLoggedIn = false;
        }
        return isLoggedIn;
    }

    public static boolean isValidBloodGrpId(Integer id, Context _this) {
        String[] bloodGrpArray = _this.getResources().getStringArray(R.array.blood_grp_array_reg);
        Integer lengthOfArray = bloodGrpArray.length;
        if (id < lengthOfArray && id > 0) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isNumberValid(String number) {
        Boolean isValid = true;
        if (!number.matches("^(?=(?:[8-9]){1})(?=[0-9]{9}).*")) {
            isValid = false;
        } else {
            if (number.length() != 10) {
                isValid = false;
            }
        }
        return isValid;
    }

    public String getCountryCode() {
        return "+977";
    }
}
