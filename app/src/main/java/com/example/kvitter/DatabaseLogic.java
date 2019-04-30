package com.example.kvitter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.example.kvitter.Activities.EditSpecificRecieptActivity;
import com.example.kvitter.Activities.Specific_receipt;
import com.example.kvitter.Activities.StartActivity;
import com.example.kvitter.Activities.Validate_reciept;
import com.example.kvitter.Util.CurrentId;
import com.example.kvitter.Util.CurrentReceipt;
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
    public void newSequenceNumber (Context context, Uri filePath, String[] receiptInfo, int validate, String fileName) {
        db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("photo_sequence").document("sequence");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    int seqNumber =  Integer.parseInt(document.getData().get("seq_id").toString());
                    if(validate == 0) {
                        savePhoto(context, filePath, seqNumber, receiptInfo);
                    } else if (validate == 1){
                        savePDF(context, filePath, seqNumber, receiptInfo, fileName);
                    }
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


    public void newSequenceNumberForNewPhoto (Context context, Uri filePath, UserData receipt) {
        db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("photo_sequence").document("sequence");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    int seqNumber =  Integer.parseInt(document.getData().get("seq_id").toString());
                    updatePhoto(context, filePath, seqNumber, receipt);
                    updateSequenceNumber(seqNumber);
                } else {
                    System.out.println("Cached get failed:" + task.getException()); }
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
    private void savePDF(Context context, Uri filePath, int seq, String[] receiptsInfo, String fileName){
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(context);
            progressDialog.setTitle("Laddar upp...");
            progressDialog.show();
            String photoName = "reciept/"+ seq + fileName + ".pdf";
            saveInformation(receiptsInfo, photoName);
            StorageReference ref = storageReference.child(photoName);
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(context, "Uppladdat", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(context, StartActivity.class);
                            context.startActivity(intent);

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
            saveInformation(receiptsInfo, photoName);
            StorageReference ref = storageReference.child(photoName);
            ref.putBytes(bytePhoto)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(context, "Uppladdat", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(context, StartActivity.class);
                            context.startActivity(intent);

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

    public void deleteReceipt(UserData receipt){
        db = FirebaseFirestore.getInstance();

        Map<String, Object> removeReceipt = new HashMap<>();
        removeReceipt.put("data", FieldValue.arrayRemove(receipt));

        db.collection("data").document(CurrentId.getUserId())
                .update(removeReceipt);

        StorageReference storageRef = storage.getReference();

        StorageReference desertRef = storageRef.child(receipt.getPhotoRef());

        desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
              //todo:meddelande
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                //todo:meddelande
            }
        });
    }

    public void updateReceipt(UserData receipt, String name, String amount, String supplier, String comment, String photoRef){


        db = FirebaseFirestore.getInstance();

        Map<String, Object> removeReceipt = new HashMap<>();
        removeReceipt.put("data", FieldValue.arrayRemove(receipt));

        db.collection("data").document(CurrentId.getUserId())
                .update(removeReceipt);

        if(photoRef != null){
            receipt.setPhotoRef(photoRef);
        }

        receipt.setName(name);
        receipt.setAmount(amount);
        receipt.setSupplier(supplier);
        receipt.setComment(comment);

       Map<String, Object> addUserToArrayMap = new HashMap<>();
       addUserToArrayMap.put("data", FieldValue.arrayUnion(receipt));


       db.collection("data").document(CurrentId.getUserId()).update(addUserToArrayMap);
    }

    /**
     * Uploads image to Firebase storage
     * @param context
     * @param filePath
     * @param seq
     */
    private void updatePhoto(Context context, Uri filePath, int seq, UserData receipt){
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
            StorageReference ref = storageReference.child(photoName);
            ref.putBytes(bytePhoto)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(context, "Nya bilden är uppladdad", Toast.LENGTH_SHORT).show();
                            StorageReference storageRef = storage.getReference();
                            StorageReference desertRef = storageRef.child(receipt.getPhotoRef());
                            desertRef.delete();
                            DataEngine engine = new DataEngine();
                            receipt.setPhotoRef(photoName);
                            engine.updateReciept(receipt.getName(),receipt);
                       //     updateReceipt(receipt, receipt.getName(), receipt.getAmount(), receipt.getSupplier(), receipt.getComment(), photoName);
                            Intent intent = new Intent(context, Specific_receipt.class);
                            context.startActivity(intent);

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
     * Saves reveipt to default folder for a specific user
     * @param receiptInfo
     * @param photoName
     */

    //String folderName, String name, String amount, String comment, String photoRef, String supplier)
    private void saveInformation(String[] receiptInfo, String photoName) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        UserData userData = new UserData(receiptInfo[4],receiptInfo[0],receiptInfo[2],receiptInfo[3],photoName,receiptInfo[1], 1);
    //    Map<String, Object> dataToStore = new HashMap<>();
    //    dataToStore.put("test",userData);
        db.collection("data").document(CurrentId.getUserId()).update(receiptInfo[0], userData);
    }
}
