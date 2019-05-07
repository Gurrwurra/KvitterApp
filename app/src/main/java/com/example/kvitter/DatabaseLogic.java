package com.example.kvitter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.example.kvitter.Activities.Specific_receipt;
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
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DatabaseLogic {
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private StorageReference storageReference;


    /**
     * Fetches the sequence number to give a photo/ file reference a unique value
     * @param context from the activity
     * @param filePath uri from photo or file
     * @param receiptInfo receipt information that will be send to method saveInformation
     * @param validate validation of it is a file or photo
     * @param fileName if it is a file name of the file
     */
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

    /**
     * Updates the sequence number to reference a unique photo or file
     * @param seqNo old sequence number
     */
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
     * Gets Sequence number for the updated photo from old receipt.
     * Calls method updateSequenceNumber to update the sequence number
     * @param context from activity
     * @param filePath uri from the photo
     * @param receipt object of the receipt to update the photo
     */
    public void newSequenceNumberForNewPhoto (Context context, Uri filePath, UserData receipt, int validate, String fileName) {
        db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("photo_sequence").document("sequence");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    int seqNumber =  Integer.parseInt(document.getData().get("seq_id").toString());
                    if(validate == 0) {
                        updatePhoto(context, filePath, seqNumber, receipt);
                    } else {
                        updatePDF(context, filePath, seqNumber, receipt, fileName);
                    }
                    updateSequenceNumber(seqNumber);
                } else {
                    System.out.println("Cached get failed:" + task.getException()); }
            }
        });
    }


    /**
     * Uploads image to Firebase storage
     * @param context from the activity
     * @param filePath uri from the photo to upload
     * @param seq sequence number from method newSequenceNumber
     * @param receiptsInfo receipt information that will be send to method saveInformation
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
     * @param context from the activity
     * @param filePath uri from the photo to upload
     * @param seq sequence number from method newSequenceNumber
     * @param receiptsInfo receipt information that will be send to method saveInformation
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
     * Deletes the receipt from database
     * @param receipt object of the receipt
     */
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

    /**
     * Updates PDF from a receipt
     * @param context from activity
     * @param filePath uri of the photo
     * @param seq sequence number for the photo
     */
    private void updatePDF(Context context, Uri filePath, int seq, UserData receipt, String fileName){
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(context);
            progressDialog.setTitle("Laddar upp...");
            progressDialog.show();
            String photoName = "reciept/"+ seq + fileName + ".pdf";
            StorageReference ref = storageReference.child(photoName);
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(context, "Nya PDFen är uppladdad", Toast.LENGTH_SHORT).show();
                            StorageReference storageRef = storage.getReference();
                            StorageReference desertRef = storageRef.child(receipt.getPhotoRef());
                            desertRef.delete();
                            DataEngine engine = new DataEngine();
                            receipt.setPhotoRef(photoName);
                            engine.updateReciept(receipt.getName(),receipt);
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
     * Updates photo from a receipt
     * @param context from activity
     * @param filePath uri of the photo
     * @param seq sequence number for the photo
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
     * Saves receipt that users created
     * @param receiptInfo the values that the user entered from AddReceiptActivity
     * @param photoName photo/file reference from method savePhoto or savePDF
     */

    private void saveInformation(String[] receiptInfo, String photoName) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        UserData userData = new UserData(receiptInfo[4],receiptInfo[0],receiptInfo[2],receiptInfo[3],photoName,receiptInfo[1], 1);
        db.collection("data").document(CurrentId.getUserId()).update(receiptInfo[0], userData);
    }
}
