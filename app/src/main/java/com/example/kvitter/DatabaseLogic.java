package com.example.kvitter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.example.kvitter.Activities.StartActivity;
import com.example.kvitter.Util.CurrentId;
import com.example.kvitter.Util.ImageHelper;
import com.example.kvitter.Util.UserData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class DatabaseLogic {
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private StorageReference storageReference;

 //GETS CURRENT SEQ. NO AND RUNS METHOD "updateSequenceNumber" WITH SEQ. NO AS PARAMETER
    public void newSequenceNumber (Context context, Uri filePath, String[] receiptInfo) {
        db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("photo_sequence").document("sequence");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    int seqNumber =  Integer.parseInt(document.getData().get("seq_id").toString());
                    savePhoto(context, filePath, seqNumber, receiptInfo);
                    updateSequenceNumber(seqNumber);
                } else {
                    System.out.println("Cached get failed:" + task.getException()); }
            }
        });
    }
//UPDATES SEQ.NO IN DOCS BY 1.
    public void updateSequenceNumber(int seqNo) {
        int newSeq = seqNo +1;
        Map<String, Object> seq = new HashMap<>();
        seq.put("seq_id", newSeq);
        db = FirebaseFirestore.getInstance();
        db.collection("photo_sequence").document("sequence")
                .set(seq)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        System.out.println("DocumentSnapshots successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("Error writing document"+ e);
                    }
                });
    }

    /**
     * Uploads image to Firebase storage
     * @param context
     * @param filePath
     * @param seq
     * @param receiptsInfo
     */
    private void savePhoto(Context context, Uri filePath, int seq, String[] receiptsInfo){
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        byte[] bytePhoto = null;
        Bitmap bitmap = null;

            try {
                    bitmap = ImageHelper.getCorrectlyOrientedImage(context, filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        bytePhoto = stream.toByteArray();
        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(context);
            progressDialog.setTitle("Laddar upp...");
            progressDialog.show();
            String photoName = "reciept/"+ UUID.randomUUID().toString() + "-" + seq;
            saveInformation("Renovering",receiptsInfo, photoName);
            StorageReference ref = storageReference.child(photoName);
            ref.putBytes(bytePhoto)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(context, "Uppladdat", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(context, "Misslyckad uppladdning:  "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uppladdat "+(int)progress+"%");
                        }
                    });
        }
    }
    /**
     * The user creates a new folder for receipts
     * @param folderName
     * @param context
     */
    public void addNewfolder(String folderName, Context context){

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        System.out.println(CurrentId.getUserId());
        DocumentReference myRef = db.collection("user_data").document(CurrentId.getUserId());

        if(folderName != null || folderName != "") {

            Map<String, Object> folder = new HashMap<>();
            Map<String, Object> addFolder = new HashMap<>();
            addFolder.put("!" + folderName, "");
            folder.put("folder", addFolder);
            myRef.set(folder, SetOptions.merge());

            Toast.makeText(context, "Mappen " + folderName + " har lagts till.", Toast.LENGTH_SHORT).show();
        }else{
            //TODO: Kolla om mappen användaren skapar redan finns
            Toast.makeText(context, "Namnet på mappen finns redan.", Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * Creates default folder for the new registered user
     */
    public void createFolder(String user_id){



        Map<String, Object> recieptData = new HashMap<>();
        recieptData.put("folder", "folder");


        db.collection("user_data").document(user_id)
                .set(recieptData, SetOptions.mergeFields("folder"))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }
    /**
     * Saves reveipt to default folder for a specific user
     * @param receiptInfo
     * @param photoName
     */

    //String folderName, String name, String amount, String comment, String photoRef, String supplier)
    private void saveInformation(
             String folderName,
             String[] receiptInfo, String photoName) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if (folderName == null) {
            folderName = "Default";
        }

        UserData userData = new UserData(folderName,receiptInfo[0],receiptInfo[2],receiptInfo[3],photoName,receiptInfo[1], 1);
        db.collection("data").document(CurrentId.getUserId()).update("data", FieldValue.arrayUnion(userData));
        /*
        Map<String,Object> docData = new HashMap<>();
        docData.put("folderName", "Default");
        Map<String, Object> nestedReceipt = new HashMap<>();
        nestedReceipt.put("name", receiptInfo[0]);
        nestedReceipt.put("supplier", receiptInfo[1]);
        nestedReceipt.put("amount", receiptInfo[2]);
        nestedReceipt.put("comment", receiptInfo[3]);
        nestedReceipt.put("photoRef", photoName);

        docData.put("receipt", nestedReceipt);
        DocumentReference reference = db.collection("data").document(CurrentId.getUserId());
        reference.update("data",FieldValue.arrayUnion(docData));


        UserData data = new UserData("Hobby;", receiptInfo[0]+";", ";"+receiptInfo[2]+";",
                receiptInfo[3]+";", photoName+";",receiptInfo[1]+";");
        Map<String, Object> recieptMap = new HashMap<>();
        recieptMap.put("name", receiptInfo[0]);
        recieptMap.put("supplier", receiptInfo[1]);
        recieptMap.put("amount", receiptInfo[2]);
        recieptMap.put("comment", receiptInfo[3]);
        recieptMap.put("photoRef", photoName);
*/
     //   DocumentReference myRef = db.collection("user_data").document(CurrentId.getUserId());

       // myRef.update("folder.!Default.receipts", FieldValue.arrayUnion(data));
     //   myRef.update("folder.!Hobby.receipts", FieldValue.arrayUnion(data));
    }
}
