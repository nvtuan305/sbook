package com.example.sherman.sbook.services;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.sherman.sbook.R;
import com.example.sherman.sbook.constants.Database;
import com.example.sherman.sbook.models.Book;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by kenp on 29/10/2016.
 */

public class NotifyService extends IntentService {
    private static final String TAG = "NotifyService";

    private static int notificationId = 0;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference categoryRef = database.getReference(Database.CATEGORIES + "nt0WxiJNyRQ9r1OXPNIE7wNr4p12/books");
    private DatabaseReference bookRef = database.getReference(Database.BOOKS);

    public NotifyService() {
        super("Notify new book service");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        categoryRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "onChildAdded: " + dataSnapshot.toString());
                String bookId = dataSnapshot.getValue().toString();

                DatabaseReference bookRef = database.getReference(Database.BOOKS + bookId);
                bookRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Book newBook = dataSnapshot.getValue(Book.class);
                        handleNewBook(newBook);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        bookRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Book newBook = dataSnapshot.getValue(Book.class);
                if (newBook != null) {
                    handleNewBook(newBook);
                } else Log.e(TAG, "onChildAdded: Cannot parse new book.");
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void handleNewBook(Book newBook) {

        if (isMatchInterest(newBook))
            sendNotification(newBook);
    }

    private void sendNotification(Book newBook) {

        Notification n  = new Notification.Builder(this)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(newBook.getTitle() + getString(R.string.had_just_been_added))
                .setSmallIcon(R.mipmap.ic_launcher)
                .build();


        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        notificationManager.notify(notificationId++, n);
    }

    private boolean isMatchInterest(Book book) {

        String[] currentInterestingBook = {"Nếu mình còn yêu nhau", "Đừng nói với anh ấy rằng tôi vần còn yêu",
        "Hậu duệ mặt trời", "Trái tim bên lề", "Mua xuân trên lò gạch"};

        String normalizedNewBookTitle = normalizeBookTitle(book.getTitle());

        for (int i = 0; i < currentInterestingBook.length; i++) {
            String normalizedTitle = normalizeBookTitle(currentInterestingBook[i]);
            if (compareTitle(normalizedNewBookTitle, normalizedTitle)) {
                return true;
            }
        }

        return false;
    }

    private boolean compareTitle(String normalizedNewBookTitle, String normalizedTitle) {
        if (normalizedNewBookTitle.equals(normalizedTitle))
            return true;
        return false;
    }

    private String normalizeBookTitle(String title) {
        return title.toLowerCase();
    }
}
