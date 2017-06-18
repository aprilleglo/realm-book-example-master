package com.zhuinden.realmbookexample.paths.books;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.zhuinden.realmbookexample.application.RealmManager;
import com.zhuinden.realmbookexample.data.entity.Book;
import com.zhuinden.realmbookexample.data.entity.BookFields;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import io.realm.Realm;

/**
 * Created by Zhuinden on 2016.08.16..
 */
public class BooksPresenter {

    public  File BloisUserDir;
    public  File BloisUserTempFile;
    public  File BloisUserPhotoFile;

    public static BooksPresenter getService(Context context) {
        //noinspection ResourceType
        return (BooksPresenter) context.getSystemService(TAG);
    }

    public static final String TAG = "BooksPresenter";

    public interface ViewContract {
        void showAddBookDialog();

        void showMissingTitle();

        void showEditBookDialog(Book book);

        interface DialogContract {
            String getTitle();
            String getAuthor();
            String getThumbnail();

            void bind(Book book);
        }
    }

    ViewContract viewContract;

    boolean isDialogShowing;

    boolean hasView() {
        return viewContract != null;
    }

    public void bindView(ViewContract viewContract) {
        this.viewContract = viewContract;
        if(isDialogShowing) {
            showAddDialog();
        }
    }

    public void unbindView() {
        this.viewContract = null;
    }

    public void showAddDialog() {
        if(hasView()) {
            isDialogShowing = true;
            viewContract.showAddBookDialog();
        }
    }

    public void dismissAddDialog() {
        isDialogShowing = false;
    }

    public void showEditDialog(Book book) {
        if(hasView()) {
            viewContract.showEditBookDialog(book);
        }
    }

    public void saveBook(ViewContract.DialogContract dialogContract) {
        if(hasView()) {
            final String author = dialogContract.getAuthor();
            final String title = dialogContract.getTitle();
            final String thumbnail = dialogContract.getThumbnail();

            if(title == null || "".equals(title.trim())) {
                viewContract.showMissingTitle();
            } else {
                Realm realm = RealmManager.getRealm();
                realm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        Book book = new Book();


                        String date = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(Calendar.getInstance().getTime());

                        Log.e("TravellerLog :: ", date );
                        String id = date;
                        String bookPhotoId = id + ".png";
                        BloisUserDir = new File(Environment.getExternalStoragePublicDirectory(
                                Environment.DIRECTORY_PICTURES), "userImages");
                        if (BloisUserDir.exists() ){
                            Log.e("TravellerLog :: ", "temp dir exists" );
                        } else {
                            Log.e("TravellerLog :: ", "temp dir DO NOT exist" + BloisUserTempFile.toString() );
                        }
                        BloisUserTempFile = new File(BloisUserDir, "myimage.png");
                        if (BloisUserTempFile.exists() ){
                            Log.e("TravellerLog :: ", "tempfile exists" );

                            BloisUserPhotoFile = new File(BloisUserDir, bookPhotoId);
                            BloisUserTempFile.renameTo(BloisUserPhotoFile);
                            book.setImageUrl(bookPhotoId);
                        } else {
                            Log.e("TravellerLog :: ", "tempfile DO NOT exist" + BloisUserTempFile.toString() );
                            book.setImageUrl(bookPhotoId);
                        }

                        book.setId(id);
                        book.setAuthor(author);
                        book.setDescription("");

                        book.setTitle(title);
                        realm.insertOrUpdate(book);
                    }
                });
            }
        }
    }

    public void deleteBookById(final String id) {
        Realm realm = RealmManager.getRealm();
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Book book = realm.where(Book.class).equalTo(BookFields.ID, id).findFirst();
                if(book != null) {
                    book.deleteFromRealm();
                }
            }
        });
    }

    public void editBook(final ViewContract.DialogContract dialogContract, final String id) {
        Realm realm = RealmManager.getRealm();
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Book book = realm.where(Book.class).equalTo(BookFields.ID, id).findFirst();
                if(book != null) {
                    book.setTitle(dialogContract.getTitle());
                    book.setImageUrl(dialogContract.getThumbnail());
                    book.setAuthor(dialogContract.getAuthor());
                }
            }
        });
    }
}
